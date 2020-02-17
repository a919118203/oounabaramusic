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

import com.google.gson.Gson;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.dao.LocalMusicDao;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.annotation.Nullable;

public class MusicPlayService extends Service {

    public static final int NOT_PREPARE=0;
    public static final int PREPAREING=5;
    public static final int IS_PREPARE=1;
    public static final int IS_STOP=2;
    public static final int IS_START=3;
    public static final int IS_PAUSE=4;
    public static final int EVENT_UPDATE_TIME =10387;
    public static final int EVENT_DELETE_MUSIC=937;
    public static final int EVENT_START_NEW_MUSIC=8156;
    public static final int EVENT_CHANGE_LOOP_TYPE=11378;
    private int status=NOT_PREPARE;
    private MusicPlayBinder mBinder;
    private MediaPlayer mp;
    private List<Music> playlist;    //播放列表 存放音乐的md5值
    private List<Handler> handlers;
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

    private Handler handler=new Handler();
    @Override
    public void onCreate() {
        LogUtil.printLog("onCreate ");
        mBinder=new MusicPlayBinder();
        mp=new MediaPlayer();
        playlist=new LinkedList<>();
        handlers=new ArrayList<>();

        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                prepared();
            }
        });
        super.onCreate();
    }

    private void prepared(){

        status=IS_PREPARE;

        if(saveComment !=currentPlayPosition){      //如果最后一首和当前要播放的不一致，就播放这首歌
            playMusic(saveComment);
            return;
        }

        currentMusic.setDuration(mp.getDuration()/1000);

        //通知已经准备完毕
        for(Handler handler:handlers){
            handler.sendEmptyMessage(IS_PREPARE);
        }

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

    private int saveComment = -1;
    private void playMusic(int position) {

        synchronized (this){
            if(playlist.get(position).getIsServer()==1){
                //更新这首歌的最后听歌时间
                Map<String,String> data=new HashMap<>();
                data.put("userId",sp.getString("userId","-1"));
                data.put("md5",playlist.get(position).getMd5());
                new S2SHttpUtil(this,
                        new Gson().toJson(data),
                        MyEnvironment.serverBasePath+"music/listenMusic",handler
                ).call(-1);
            }
        }

        for(Handler handler:handlers){
            Message msg=new Message();
            msg.what=EVENT_START_NEW_MUSIC;
            msg.obj=playlist.get(position);
            handler.sendMessage(msg);
        }

        if(status==PREPAREING){          //如果当前还在准备的时候播放下一首，就存起来，不播放，准备完再播放这首歌
            saveComment =position;           //一直覆盖，播放最后一首
            return;
        }
        saveComment =position;

        if(currentMusic!=null&&currentMusic.getMd5().equals(playlist.get(position).getMd5())){
            if(status==IS_START||status==IS_PAUSE){
                mp.seekTo(0);
                mp.pause();
                prepared();
            }
            return;
        }

        //开始准备
        status=PREPAREING;
        currentPlayPosition =position;
        currentMusic=playlist.get(position);
        try{
            if(currentMusic.getFilePath()!=null){
                mp.reset();
                if(currentMusic.getIsServer()==1){

                    if(currentMusic.getDownloadStatus()==0||currentMusic.getDownloadStatus()==1){

                        File f=new File(currentMusic.getFilePath());
                        if(f.exists()){
                            mp.setDataSource(f.getPath());
                        }else{
                            Toast.makeText(this, "文件不存在，请重新扫描文件", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }else if(currentMusic.getDownloadStatus()==3){

                        Music i=localMusicDao.selectMusicByMd5(currentMusic.getMd5());
                        if(i!=null&&i.getFilePath()!=null&&i.getDownloadStatus()!=2){
                            File file=new File(i.getFilePath());
                            if(file.exists()){
                                mp.setDataSource(file.getPath());
                            }else{
                                mp.setDataSource(currentMusic.getFilePath());
                            }
                        }else{
                            mp.setDataSource(currentMusic.getFilePath());
                        }
                    }
                }else{

                    File file=new File(currentMusic.getFilePath());
                    if(file.exists()){

                        mp.setDataSource(file.getPath());

                    }else{
                        Toast.makeText(this, "文件不存在，请重新扫描文件", Toast.LENGTH_SHORT).show();
                        return;
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

    private void playPreviousMusic(){
        switch (currentLoopType){
            case LOOP_TYPE_LIST:
                if(currentPlayPosition-1<0){
                    playMusic(playlist.size()-1);
                }else{
                    playMusic(currentPlayPosition-1);
                }
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

        public void playMusics(List<Music> musics){
            for(int i=0;i<musics.size();i++){
                if(i==0){
                    playMusic(musics.get(i));
                }else{
                    nextPlay(musics.get(i));
                }
            }
        }

        //下一首播放
        public void nextPlay(Music item){

            if(status==NOT_PREPARE){
                playlist.add(item);
                MusicPlayService.this.playMusic(0);
            }else if(status==IS_PAUSE){
                playMusic(item);
            }else{
                if(!playlist.contains(item)){
                    playlist.add(currentPlayPosition +1,item);
                }
            }
        }

        //从播放列表中删除一首音乐
        public void deleteMusic(String md5){
            boolean exists=false;
            for(Music item:playlist){
                if(item.getMd5().equals(md5)){
                    exists=true;
                    break;
                }
            }

            if(!exists){     //如果存在于播放列表中才删除
                return;
            }

            if(playlist.size()==1){
                deleteAllMusic();
                return;
            }


            playlist.remove(currentPlayPosition);
            if(currentPlayPosition<playlist.size()){
                MusicPlayService.this.playMusic(currentPlayPosition);
            }else{
                MusicPlayService.this.playMusic(currentPlayPosition-1);
            }

            for(Handler handler:handlers){
                handler.sendEmptyMessage(EVENT_DELETE_MUSIC);
            }
        }

        public void deleteAllMusic(){

            //清空整个播放列表
            playlist.clear();

            //重置播放器
            mp.reset();

            //设置状态
            status=NOT_PREPARE;

            currentMusic=null;

            //重置位置
            currentPlayPosition=-1;
            for(Handler handler:handlers){
                handler.sendEmptyMessage(EVENT_DELETE_MUSIC);
            }
        }

        //刷新
        public void refresh(){

        }

        public void seekTo(int t){
            mp.seekTo(t);
        }

        public void playNext(){
            playNextMusic();
        }

        public void playPrevious(){
            playPreviousMusic();
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

        public void addHandler(Handler handler){
            handlers.add(handler);
        }

        public void removeHandler(Handler handler){
            handlers.remove(handler);
        }

        public void setLocalMusicDao(LocalMusicDao dao){
            localMusicDao=dao;
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

            for(Handler handler:handlers){
                handler.sendEmptyMessage(EVENT_CHANGE_LOOP_TYPE);
            }
        }
    }

    private class PlayRunnable implements Runnable{
        Date startDate;
        Date endDate;
        int musicLen;
        String md5;
        @Override
        public void run() {
            //获取开始时间
            if(currentMusic.getIsServer()==1){
                startDate=new Date();
                md5=currentMusic.getMd5();
                musicLen=mp.getDuration();
            }

            while(true){
                try {
                    //400ms更新一次UI
                    Thread.sleep(250);

                    //音乐暂停的时候，时间更新暂停
                    while(status==IS_PAUSE){
                        Thread.sleep(200);
                    }

                    if(status!=IS_START)
                        return;

                    if(!this.equals(currentRunnable))
                        return;

                    //设置UI
                    for(Handler handler:handlers){
                        Message message=new Message();
                        message.what= EVENT_UPDATE_TIME;
                        message.arg1=mp.getCurrentPosition();
                        handler.sendMessage(message);
                    }

                    if(mp.getCurrentPosition()/1000>=mp.getDuration()/1000)
                        break;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //记录听歌次数
            if(md5!=null){
                endDate=new Date();
                long t=endDate.getTime()-startDate.getTime();
                if(10*t>9*musicLen){
                    Map<String,String> data=new HashMap<>();
                    data.put("userId",sp.getString("userId","-1"));
                    data.put("md5",md5);
                    data.put("cnt","1");
                    new S2SHttpUtil(MusicPlayService.this,
                            new Gson().toJson(data),
                            MyEnvironment.serverBasePath+"music/listenMusic",
                            handler).call(-1);
                }
            }

            //自然结束时，根据循环类型选择下一首
            playNextMusic();
        }
    }
}
