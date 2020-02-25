package com.oounabaramusic.android.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.Gson;
import com.oounabaramusic.android.MusicPlayActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.dao.LocalMusicDao;
import com.oounabaramusic.android.okhttputil.HttpUtil;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MusicPlayService extends Service {

    static final String PUSH_CHANNEL_ID="1";
    static final String PUSH_CHANNEL_NAME="Mogeko";
    private int notiId = 19971005;

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
    private List<BaseHandler> handlers;
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

    private MyHandler handler;

    //用于解决多次连续点击歌
    private int saveComment = -1;

    private Notification notification;
    private int cnt;       //防止TransactionTooLargeException
    private boolean showing;
    private NotificationManager notificationManager;
    private MusicPlayReceiver receiver;
    private IntentFilter intentFilter;

    private Bitmap start,stop;
    private Bitmap collected,noCollect;
    private boolean isCollection;
    private Bitmap defaultImage;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.printLog("onCreate ");

        initParam();

        initReceiver();

        initContentView();

        initNotificationManager();

        initNotification();
    }

    private void initParam(){
        showing=false;
        mBinder=new MusicPlayBinder();
        mp=new MediaPlayer();
        handler=new MyHandler(this);
        playlist=new LinkedList<>();
        handlers=new ArrayList<>();
        receiver=new MusicPlayReceiver();
        start=BitmapFactory.decodeResource(getResources(),R.mipmap.play_button);
        stop=BitmapFactory.decodeResource(getResources(),R.mipmap.stop_button);
        defaultImage=BitmapFactory.decodeResource(getResources(),R.mipmap.default_image);
        collected=BitmapFactory.decodeResource(getResources(),R.mipmap.collection_after);
        noCollect=BitmapFactory.decodeResource(getResources(),R.mipmap.collection_before);

        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                prepared();
            }
        });

        handlers.add(handler);
    }

    private void initNotificationManager(){
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O&&
                notificationManager.getNotificationChannel(PUSH_CHANNEL_ID)==null) {
            NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void initReceiver(){
        intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayReceiver.CHANGE_PLAY_STATUS);
        intentFilter.addAction(MusicPlayReceiver.NEXT_MUSIC);
        intentFilter.addAction(MusicPlayReceiver.PREVIOUS_MUSIC);
        intentFilter.addAction(MusicPlayReceiver.TO_COLLECTION);
        intentFilter.addAction(MusicPlayReceiver.QUIT_NOTIFICATION);

        receiver = new MusicPlayReceiver();

        registerReceiver(receiver,intentFilter);
    }

    private RemoteViews initContentView(){
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.special_play_notification);

        remoteViews.setOnClickPendingIntent(R.id.collect,
                newPendingIntent(MusicPlayReceiver.TO_COLLECTION));
        remoteViews.setOnClickPendingIntent(R.id.previous,
                newPendingIntent(MusicPlayReceiver.PREVIOUS_MUSIC));
        remoteViews.setOnClickPendingIntent(R.id.next,
                newPendingIntent(MusicPlayReceiver.NEXT_MUSIC));
        remoteViews.setOnClickPendingIntent(R.id.play_controller,
                newPendingIntent(MusicPlayReceiver.CHANGE_PLAY_STATUS));
        remoteViews.setOnClickPendingIntent(R.id.quit,
                newPendingIntent(MusicPlayReceiver.QUIT_NOTIFICATION));

        return remoteViews;
    }

    private PendingIntent newPendingIntent(String action){
        Intent intent = new Intent();
        intent.setAction(action);
        return PendingIntent.getBroadcast(this,0,intent,0);
    }

    private void initNotification(){

        Intent intent = new Intent(this, MusicPlayActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);

        notification=new NotificationCompat.Builder(this,PUSH_CHANNEL_ID)
                .setCustomContentView(initContentView())
                .setSmallIcon(R.mipmap.default_image)
                .setTicker("Mogeko")
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pi)
                .build();
    }

    private void prepared(){

        status=IS_PREPARE;

        if(saveComment !=currentPlayPosition){      //如果最后一首和当前要播放的不一致，就播放这首歌
            playMusic(saveComment);
            return;
        }

        currentMusic.setDuration(mp.getDuration()/1000);

        //通知已经准备完毕
        sendEmptyMessage(IS_PREPARE);

        //播放音乐
        status=IS_START;
        mp.start();

        //新建一个线程更新UI
        new Thread(currentRunnable=new PlayRunnable()).start();
    }

    private void start(){
        status=IS_START;
        mp.start();
        sendEmptyMessage(IS_START);
    }

    private void stop(){
        status=IS_PAUSE;
        mp.pause();
        sendEmptyMessage(IS_PAUSE);
    }

    private void setNotificationImage(Bitmap bitmap){
        if(cnt++==50){
            cnt=0;
            initNotification();
        }

        notification.contentView.setImageViewBitmap(R.id.cover,bitmap);
        notificationManager.notify(notiId,notification);
    }

    private void setPlayButton(Bitmap bitmap){
        if(cnt++==50){
            cnt=0;
            initNotification();
        }

        notification.contentView.setImageViewBitmap(R.id.play_controller,bitmap);
        notificationManager.notify(notiId,notification);
    }

    private void setPlayInfo(Music item){
        if(cnt++==50){
            cnt=0;
            initNotification();
        }

        //封面
        if(item.getIsServer()==1){
            HttpUtil.loadImage(this, new MyImage(MyImage.TYPE_SINGER_COVER,
                    Integer.valueOf(item.getSingerId().split("/")[0])),handler);
        }else{
            notification.contentView.setImageViewBitmap(R.id.cover,defaultImage);
        }

        //文本内容
        notification.contentView.setTextViewText(R.id.content,
                item.getMusicName()+" - "+ item.getSingerName()
                        .replace("/"," "));

        //是否已收藏
        if(item.getIsServer()==1){
            if(SharedPreferencesUtil.isLogin(sp)){
                List<String> data=new ArrayList<>();
                data.add(sp.getString("userId","-1"));
                data.add(item.getMd5());
                String url=MyEnvironment.serverBasePath+"musicIsCollection";

                new S2SHttpUtil(this,new Gson().toJson(data),url,handler)
                        .call(BasicCode.GET_IS_COLLECT_MUSIC_END);
            }
        }

        notificationManager.notify(notiId,notification);
    }

    private void setMusicCollection(Bitmap bitmap){
        if(cnt++==50){
            cnt=0;
            initNotification();
        }

        notification.contentView.setImageViewBitmap(R.id.collect,bitmap);
        notificationManager.notify(notiId,notification);
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

    private void sendMessage(Message msg){
        List<BaseHandler> removeList = new ArrayList<>();

        for(BaseHandler handler:handlers){
            if(handler.isDie()){
                removeList.add(handler);
            }else{
                Message message = new Message();
                message.what=msg.what;
                message.arg1=msg.arg1;
                message.arg2=msg.arg2;
                message.obj=msg.obj;
                handler.sendMessage(message);
            }
        }

        for (BaseHandler handler:removeList){
            handlers.remove(handler);
        }
    }

    private void sendEmptyMessage(int code){
        Message msg = new Message();
        msg.what=code;
        sendMessage(msg);
    }

    private void addHandler(BaseHandler handler){
        if(handlers.contains(handler)){
            return;
        }

        if(handler.isDie()){
            handler.resurrection();
        }
        handlers.add(handler);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.printLog("onUnbind");
        return super.onUnbind(intent);
    }

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

        Message msg=new Message();
        msg.what=EVENT_START_NEW_MUSIC;
        msg.obj=playlist.get(position);
        sendMessage(msg);

        if(status==PREPAREING){          //如果当前还在准备的时候播放下一首，就存起来，不播放，准备完再播放这首歌
            saveComment =position;           //一直覆盖，播放最后一首
            return;
        }
        saveComment =position;

        //如果现在播放的音乐和要播放的音乐相同，不重新加载音乐
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
        LogUtil.printLog("MusicPlayService:   onDestroy ");
        mp.stop();
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public class MusicPlayBinder extends Binder{

        //开始播放
        public void startMusic(){
            start();
        }

        //暂停播放
        public void pauseMusic(){
            stop();
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
            int index=0;
            for(Music item:playlist){
                if(item.getMd5().equals(md5)){
                    exists=true;
                    break;
                }
                index++;
            }

            if(!exists){     //如果存在于播放列表中才删除
                return;
            }

            if(playlist.size()==1){
                deleteAllMusic();
                return;
            }

            //移除音乐
            playlist.remove(index);

            //如果移除的是正在播放的音乐
            if(index==currentPlayPosition){
                if(currentPlayPosition<playlist.size()){
                    MusicPlayService.this.playMusic(currentPlayPosition);
                }else{
                    MusicPlayService.this.playMusic(currentPlayPosition-1);
                }
            }else{
                //维护currentPlayPosition
                if(index<currentPlayPosition)
                    currentPlayPosition--;
            }

            sendEmptyMessage(EVENT_DELETE_MUSIC);
        }

        public void deleteAllMusic(){

            //清空整个播放列表
            playlist.clear();

            //重置播放器
            mp.reset();

            //设置状态
            status=NOT_PREPARE;
            sendEmptyMessage(status);

            currentMusic=null;

            //重置位置
            currentPlayPosition=-1;
            sendEmptyMessage(EVENT_DELETE_MUSIC);
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

        public void addHandler(BaseHandler handler){
            MusicPlayService.this.addHandler(handler);
        }

        public void removeHandler(BaseHandler handler){
            handler.toDie();
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
            sendEmptyMessage(EVENT_CHANGE_LOOP_TYPE);
        }

        public int getSaveComment(){
            return saveComment;
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
                    Message message=new Message();
                    message.what= EVENT_UPDATE_TIME;
                    message.arg1=mp.getCurrentPosition();
                    sendMessage(message);

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

    static class MyHandler extends BaseHandler{
        MusicPlayService service;
        MyHandler(MusicPlayService service){
            this.service=service;
        }

        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap;
            switch (msg.what){
                case MyCircleImageView.NO_NET:
                    Toast.makeText(service, "请检查网络连接", Toast.LENGTH_SHORT).show();
                    bitmap=BitmapFactory.decodeResource(
                            service.getResources(),R.mipmap.default_image);
                    service.setNotificationImage(bitmap);
                    break;

                case MyCircleImageView.LOAD_SUCCESS:
                    bitmap= (Bitmap) msg.obj;
                    service.setNotificationImage(bitmap);
                    break;

                case MyCircleImageView.LOAD_FAILURE:
                    Toast.makeText(service, "图片加载失败", Toast.LENGTH_SHORT).show();

                case MyCircleImageView.NO_COVER:
                    bitmap=BitmapFactory.decodeResource(
                            service.getResources(),R.mipmap.default_image);
                    service.setNotificationImage(bitmap);
                    break;

                case IS_PAUSE:
                    if(service.showing){
                        service.setPlayButton(service.start);
                    }
                    break;

                case IS_START:
                case IS_PREPARE:
                    if(!service.showing){
                        service.showing=true;
                        service.startForeground(service.notiId,service.notification);
                        service.setPlayInfo(service.currentMusic);
                    }
                    service.setPlayButton(service.stop);

                    break;

                case NOT_PREPARE:
                    service.showing=false;
                    service.stopForeground(true);
                    break;

                case EVENT_START_NEW_MUSIC:
                    if(service.showing){
                        service.setPlayInfo(service.currentMusic);
                    }
                    break;

                case BasicCode.GET_IS_COLLECT_MUSIC_END:
                    String isCollect= (String) msg.obj;
                    if(isCollect.equals("1")){
                        service.isCollection=true;
                        service.setMusicCollection(service.collected);
                    }else{
                        service.isCollection=false;
                        service.setMusicCollection(service.noCollect);
                    }
                    break;

                case BasicCode.CANCEL_COLLECTION_MUSIC_END:
                    service.isCollection=false;
                    service.setMusicCollection(service.noCollect);
                    break;

                case BasicCode.COLLECTION_MUSIC_END:
                    service.isCollection=true;
                    service.setMusicCollection(service.collected);
                    break;
            }
        }
    }

    //接收前台服务发出的广播
    class MusicPlayReceiver extends BroadcastReceiver {

        //点击播放键
        public static final String CHANGE_PLAY_STATUS="com.oounabaramusic.android.CHANGE_PLAY_STATUS";
        //前一首音乐
        public static final String PREVIOUS_MUSIC="com.oounabaramusic.android.PREVIOUS_MUSIC";
        //下一首音乐
        public static final String NEXT_MUSIC="com.oounabaramusic.android.NEXT_MUSIC";
        //收藏音乐
        public static final String TO_COLLECTION="com.oounabaramusic.android.TO_COLLECTION";
        //关闭前台服务
        public static final String QUIT_NOTIFICATION="com.oounabaramusic.android.QUIT_NOTIFICATION";

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())){
                case CHANGE_PLAY_STATUS:
                    if(status==IS_START){
                        stop();
                    }else if(status==IS_PAUSE){
                        start();
                    }
                    break;
                case PREVIOUS_MUSIC:
                    playPreviousMusic();
                    break;
                case NEXT_MUSIC:
                    playNextMusic();
                    break;
                case TO_COLLECTION:
                    if(SharedPreferencesUtil.isLogin(sp)){
                        if(currentMusic.getIsServer()==1){
                            String userId=sp.getString("userId","-1");
                            List<String> data=new ArrayList<>();
                            data.add(userId);
                            data.add(currentMusic.getMd5());

                            if(isCollection){
                                String url=MyEnvironment.serverBasePath+"cancelCollectionMusic";
                                new S2SHttpUtil(
                                        MusicPlayService.this,
                                        new Gson().toJson(data),
                                        url,handler)
                                        .call(BasicCode.CANCEL_COLLECTION_MUSIC_END);
                            }else{
                                new S2SHttpUtil(
                                        MusicPlayService.this,
                                        new Gson().toJson(data),
                                        MyEnvironment.serverBasePath+"collectionMusic",
                                        handler)
                                        .call(BasicCode.COLLECTION_MUSIC_END);
                            }
                        }else{
                            Toast.makeText(MusicPlayService.this, "无法收藏本地音乐", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MusicPlayService.this, "请先登录", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case QUIT_NOTIFICATION:
                    showing=false;
                    stopForeground(true);
                    stop();
                    break;
            }
        }
    }
}
