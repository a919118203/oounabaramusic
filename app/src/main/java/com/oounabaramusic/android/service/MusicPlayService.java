package com.oounabaramusic.android.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.oounabaramusic.android.LocalMusicActivity;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.dao.LocalMusicDao;
import com.oounabaramusic.android.okhttputil.HttpUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;

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
    public static final int EVENT_FILE_NO_EXISTS=8;
    private int status=NOT_PREPARE;
    private MusicPlayBinder mBinder;
    private MediaPlayer mp;
    private List<Music> playlist;    //播放列表 存放音乐的md5值
    private Handler handler=null;
    private int currentPlayPosition =-1;      //当前播放的位置
    private Music currentMusic;      //当前播放的音乐
    private PlayRunnable currentRunnable;

    private LocalMusicDao localMusicDao;
    private Random random=new Random(new Date().getTime());
    private SharedPreferences sp;

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
        if(currentMusic.getIsServer()==1){
            boolean login = sp.getBoolean("login",false);
            if(login){
                String userId=sp.getString("userId","-1");
                int musicId=currentMusic.getId();
                HttpUtil.listenMusic(this,userId,musicId);
            }
        }

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
        if(sp==null){
            sp= PreferenceManager.getDefaultSharedPreferences(this);
        }
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.printLog("onUnbind");
        return super.onUnbind(intent);
    }

    private void playMusic(int position) {
        if(currentPlayPosition==position){
            mp.seekTo(0);
            mp.pause();
            prepared();
            return;
        }

        currentPlayPosition =position;
        currentMusic=playlist.get(position);
        try{
            if(currentMusic.getFilePath()!=null){
                mp.reset();
                if(currentMusic.getIsServer()==1){
                    mp.setDataSource(currentMusic.getFilePath());
                    saveListenCnt();
                }else{
                    File file=new File(currentMusic.getFilePath());
                    if(file.exists()){
                        mp.setDataSource(file.getPath());
                    }else{
                        Toast.makeText(this, "文件不存在，请重新扫描文件", Toast.LENGTH_SHORT).show();
                    }
                }
                mp.prepareAsync();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveListenCnt(){

    }

    private void playNextMusic() {
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
        public void playMusic(Music item){

            boolean f=false;
            int index=0;
            for(int i=0;i<playlist.size();i++){
                if(playlist.get(i).getMd5().equals(item.getMd5())){
                    f=true;
                    index=i;
                    break;
                }
            }
            if(f){
                MusicPlayService.this.playMusic(index);
            }else{
                playlist.add(item);
                MusicPlayService.this.playMusic(playlist.size()-1);
            }
        }

        //下一首播放
        public void nextPlay(Music item){
            if(status==NOT_PREPARE){
                playlist.add(item);
                MusicPlayService.this.playMusic(0);
            }else{

                boolean f=false;
                for(int i=0;i<playlist.size();i++){
                    if(playlist.get(i).getMd5().equals(item.getMd5())){
                        f=true;
                        break;
                    }
                }
                if(!f){
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

            for(Music item:playlist){
                if(item.getMd5().equals(md5)){
                    playlist.remove(item);
                    break;
                }
            }
            currentPlayPosition=playlist.indexOf(currentMusic);
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

        //刷新
        public void refresh(){

            //刷新isServer是待确定的项
            for(Music music:playlist){
                if(music.getIsServer()==2){
                    music=MusicPlayService.this.localMusicDao.selectMusicByMd5(music.getMd5());
                }
            }
        }

        public int getStatus(){
            return status;
        }

        public int getCurrentProgress(){
            return mp.getCurrentPosition();
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

        public List<Music> getPlayList(){
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
        @Override
        public void run() {
            while(true){
                try {
                    //400ms更新一次UI
                    Thread.sleep(400);

                    //音乐暂停的时候，时间更新暂停
                    while(status==IS_PAUSE){
                        Thread.sleep(200);
                    }

                    if(status!=IS_START)
                        return;

                    if(!f)
                        return;

                    //设置UI
                    Message message=new Message();
                    message.what= EVENT_UPDATE_TIME;
                    message.arg1=mp.getCurrentPosition();
                    handler.sendMessage(message);

                    if(mp.getCurrentPosition()==mp.getDuration())
                        break;

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
