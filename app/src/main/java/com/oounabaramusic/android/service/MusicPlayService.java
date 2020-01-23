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
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.Nullable;

public class MusicPlayService extends Service {

    public static final int NOT_PREPARE=0;
    public static final int IS_PREPARE=1;
    public static final int IS_STOP=2;
    public static final int IS_START=3;
    public static final int IS_PAUSE=4;
    public static final int EVENT_UPDATE_TIME =10;
    private int status=NOT_PREPARE;
    private MusicPlayBinder mBinder;
    private MediaPlayer mp;
    private List<String> playlist;    //播放列表 存放音乐的md5值
    private Handler handler=null;
    private int currentPlay=-1;      //当前播放的位置
    private int currentProgress=0;   //当前进度
    private Music currentMusic;      //当前播放的音乐

    private LocalMusicDao localMusicDao;

    @Override
    public void onCreate() {
        LogUtil.printLog("onCreate ");
        mBinder=new MusicPlayBinder();
        mp=new MediaPlayer();
        playlist=new LinkedList<>();

        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
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
                new Thread(new Runnable() {

                    //需要维持的秒数
                    int length=mp.getDuration();
                    //这个线程所代表的音乐的md5
                    String currentMusicMd5=currentMusic.getMd5();
                    @Override
                    public void run() {
                        for(int i=0;i<length;i+=400){
                            try {
                                //一秒更新一次UI
                                Thread.sleep(400);

                                //音乐暂停的时候，时间更新暂停
                                while(status==IS_PAUSE){
                                    Thread.sleep(200);
                                }

                                //如果线程保存的md5和现在播放的歌曲的md5不一致，说明切歌了，结束这个线程
                                if(!currentMusicMd5.equals(currentMusic.getMd5()))
                                    break;

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
                    }
                }).start();
            }
        });
        super.onCreate();
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

    public void playMusic(int position){
        currentMusic=localMusicDao.selectMusicByMd5(playlist.get(position));
        if(currentMusic.getFilePath()!=null){
            File file=new File(currentMusic.getFilePath());
            if(file.exists()){
                try {
                    currentPlay=position;
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
        public void startMusic(){
            if(mp!=null){
                status=IS_START;
                mp.start();
            }
        }

        public void pauseMusic(){
            if(mp!=null){
                status=IS_PAUSE;
                mp.pause();
            }
        }

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
                playlist.add(currentPlay+1,item);
            }
        }

        public int getStatus(){
            return status;
        }

        public int getCurrentProgress(){
            return currentProgress;
        }

        public int getCurrentMusicPosition(){
            return currentPlay;
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
    }
}
