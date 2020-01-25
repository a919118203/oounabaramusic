package com.oounabaramusic.android.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.dao.LocalMusicDao;
import com.oounabaramusic.android.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import androidx.annotation.Nullable;

public class MusicPlayService extends Service {

    public static final int NOT_PREPARE=0;
    public static final int IS_PREPARE=1;
    public static final int IS_STOP=2;
    public static final int IS_START=3;
    public static final int IS_PAUSE=4;
    public static final int EVENT_UPDATE_TIME =10;
    public static final int EVENT_DELETE_MUSIC=9;
    private int status=NOT_PREPARE;
    private MusicPlayBinder mBinder;
    private MediaPlayer mp;
    private List<String> playlist;    //播放列表 存放音乐的md5值
    private Handler handler=null;
    private int currentPlayPosition =-1;      //当前播放的位置
    private int currentProgress=0;   //当前进度
    private Music currentMusic;      //当前播放的音乐
    private PlayRunnable currentRunnable;

    private LocalMusicDao localMusicDao;
    private Random random=new Random(new Date().getTime());

    public static final int LOOP_TYPE_RANDOM=11;
    public static final int LOOP_TYPE_SINGLE=12;
    public static final int LOOP_TYPE_LIST=13;
    private int currentLoopType=LOOP_TYPE_LIST;

    @Override
    public void onCreate() {
        LogUtil.printLog("onCreate ");
        mBinder=new MusicPlayBinder();
        mp=new MediaPlayer();
        playlist=new LinkedList<>();

        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                prepared();
            }
        });
        super.onCreate();
    }

    private void prepared(){
        //播放歌曲时，结束上一次还没走完的线程
        if(currentRunnable!=null){
            currentRunnable.stop();
        }

        //通知已经准备完毕
        handler.sendEmptyMessage(IS_PREPARE);

        //将时间设置为00:00
        Message message=new Message();
        message.what= EVENT_UPDATE_TIME;
        message.arg1=0;
        handler.sendMessage(message);

        //播放音乐
        status=IS_START;
        mp.start();

        //新建一个线程更新UI
        new Thread(currentRunnable=new PlayRunnable()).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.printLog("onBind ");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.printLog("onUnbind");
        return super.onUnbind(intent);
    }

    private void playMusic(int position){
        if(currentPlayPosition==position){
            mp.seekTo(0);
            mp.pause();
            prepared();
            return;
        }

        currentMusic=localMusicDao.selectMusicByMd5(playlist.get(position));
        if(currentMusic.getFilePath()!=null){
            File file=new File(currentMusic.getFilePath());
            if(file.exists()){
                try {
                    currentPlayPosition =position;
                    mp.reset();
                    mp.setDataSource(file.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.prepareAsync();
                return;
            }
        }

        switch (currentMusic.getIsServer()){
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }

    private void playNextMusic(){
        switch (currentLoopType){
            case LOOP_TYPE_LIST:
                playMusic((currentPlayPosition+1)%playlist.size());
                break;
            case LOOP_TYPE_RANDOM:
                playMusic(random.nextInt(playlist.size()));
                break;
            case LOOP_TYPE_SINGLE:
                playMusic(currentPlayPosition);
                break;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.printLog("onStartCommand");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        LogUtil.printLog("onDestroy ");
        mp.stop();
        super.onDestroy();
    }

    public class MusicPlayBinder extends Binder{

        //开始播放
        public void startMusic(){
            if(mp!=null){
                status=IS_START;
                mp.start();
            }
        }

        //暂停播放
        public void pauseMusic(){
            if(mp!=null){
                status=IS_PAUSE;
                mp.pause();
            }
        }

        //播放歌曲
        public void playMusic(String item){
            if(playlist.contains(item)){
                MusicPlayService.this.playMusic(playlist.indexOf(item));
            }else{
                playlist.add(item);
                MusicPlayService.this.playMusic(playlist.size()-1);
            }
        }

        //下一首播放
        public void nextPlay(String item){
            if(status==NOT_PREPARE){
                playlist.add(item);
                MusicPlayService.this.playMusic(0);
            }else{
                if(!playlist.contains(item)){
                    playlist.add(currentPlayPosition +1,item);
                }
            }
        }

        //从播放列表中删除一首音乐
        public void deleteMusic(String md5){
            if(playlist.size()==1){
                LogUtil.printLog("删除全部");
                deleteAllMusic();
                return;
            }

            if(md5.equals(currentMusic.getMd5())){
                int index=0;//其实没意义
                switch (getCurrentLoopType()){
                    case MusicPlayService.LOOP_TYPE_LIST:
                    case MusicPlayService.LOOP_TYPE_SINGLE:
                        index=(getCurrentMusicPosition()+1)%getPlayList().size();
                        break;
                    case MusicPlayService.LOOP_TYPE_RANDOM:
                        while((index=random.nextInt(getPlayList().size()))==getCurrentMusicPosition());
                        break;
                }
                MusicPlayService.this.playMusic(index);
            }
            playlist.remove(md5);
            currentPlayPosition=playlist.indexOf(currentMusic.getMd5());
            handler.sendEmptyMessage(EVENT_DELETE_MUSIC);
        }

        public void deleteAllMusic(){

            //清空整个播放列表
            playlist.clear();

            //重置播放器
            mp.reset();

            //设置状态
            status=NOT_PREPARE;

            //重置位置
            currentPlayPosition=-1;
            handler.sendEmptyMessage(EVENT_DELETE_MUSIC);
        }

        public int getStatus(){
            return status;
        }

        public int getCurrentProgress(){
            return currentProgress;
        }

        public int getCurrentMusicPosition(){
            return currentPlayPosition;
        }

        public void setHandler(Handler handler){
            MusicPlayService.this.handler=handler;
        }

        public void setLocalMusicDao(LocalMusicDao dao){
            MusicPlayService.this.localMusicDao=dao;
        }

        public int getCurrentMusicDuration(){
            return mp.getDuration();
        }

        public List<String> getPlayList(){
            return playlist;
        }

        public Music getCurrentMusic(){
            return currentMusic;
        }

        public int getCurrentLoopType(){
            return currentLoopType;
        }

        public void setCurrentLoopType(int loopType){
            MusicPlayService.this.currentLoopType=loopType;
        }
    }

    private class PlayRunnable implements Runnable{

        private boolean f=true;
        //需要维持的秒数
        int length=mp.getDuration();
        @Override
        public void run() {
            for(int i=0;i<length;i+=400){
                try {
                    //400ms更新一次UI
                    Thread.sleep(400);

                    //音乐暂停的时候，时间更新暂停
                    while(status==IS_PAUSE){
                        Thread.sleep(200);
                    }

                    //结束当前线程
                    if(!f)
                        return;

                    if(status!=IS_START)
                        return;

                    //设置UI
                    Message message=new Message();
                    message.what= EVENT_UPDATE_TIME;
                    message.arg1=i;
                    currentProgress=message.arg1;
                    handler.sendMessage(message);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //自然结束时，根据循环类型选择下一首
            playNextMusic();
        }

        public void stop(){
            f=false;
        }
    }
}
