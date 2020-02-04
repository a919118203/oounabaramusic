package com.oounabaramusic.android;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.oounabaramusic.android.adapter.MusicPlayListAdapter;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.dao.LocalMusicDao;
import com.oounabaramusic.android.service.DownloadService;
import com.oounabaramusic.android.service.MusicPlayService;
import com.oounabaramusic.android.util.ActivityManager;
import com.oounabaramusic.android.util.InputMethodUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;
import com.oounabaramusic.android.widget.customview.PlayButton;
import com.oounabaramusic.android.widget.popupwindow.MyBottomSheetDialog;
import com.oounabaramusic.android.widget.popupwindow.PlayListDialog;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BaseActivity extends AppCompatActivity {
    protected static final int CHOOSE_PHOTO=1;     //打开相册选择图片
    private MyBottomSheetDialog musicPlayList;
    private Handler handler=new MusicPlayHandler(this);
    private MusicPlayService.MusicPlayBinder binder;
    private DownloadService.DownloadBinder downloadBinder;
    private ServiceConnection connection,downloadConnection;

    protected RelativeLayout musicPlayBar;
    protected LocalMusicDao localMusicDao;

    public SharedPreferences sp;
    public Gson gson=new Gson();

    private boolean isBind ; //判断是否已绑定音乐播放服务
    private boolean autoCloseInputMethod;  //是否开启点输入框之外的地方关闭输入法
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.printLog("onCreate: "+this.toString());
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
        localMusicDao=new LocalMusicDao(this);
        sp= PreferenceManager.getDefaultSharedPreferences(this);
        autoCloseInputMethod=true;
    }

    private void init(){
        musicPlayBar=findViewById(R.id.tool_current_play_layout);
    }

    @Override
    protected void onDestroy() {
        LogUtil.printLog("onDestroy: "+this.toString());
        ActivityManager.removeActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        LogUtil.printLog("onStop: "+this.toString()+"musicPlayBar==null?:  "+String.valueOf(musicPlayBar==null));
        if(isBind){
            isBind=false;
            unbindService(connection);
        }
        unbindService(downloadConnection);
        super.onStop();
    }

    @Override
    protected void onStart() {
        LogUtil.printLog("onStart: "+this.toString());
        super.onStart();
        init();

        //如果这里需要获取service信息,就绑定MusicPlayService
        bindService(new Intent(this, MusicPlayService.class),connection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                isBind=true;
                binder= (MusicPlayService.MusicPlayBinder) service;
                binder.addHandler(handler);
                binder.setLocalMusicDao(localMusicDao);

                //初始化播放列表弹窗
                musicPlayList=new MyBottomSheetDialog(BaseActivity.this);
                musicPlayList.setContentView(createContentView());

                //初始化播放条
                if(musicPlayBar!=null){
                    initPlay();
                }
                onBindOk();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);


        bindService(new Intent(this, DownloadService.class),downloadConnection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                downloadBinder= (DownloadService.DownloadBinder) service;
                onDownloadBindOk();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        },BIND_AUTO_CREATE);
    }


    private PlayButton playButton;
    private TextView musicName;
    private TextView singerName;
    private MyCircleImageView cover;
    private boolean one=false;       //只进行一次初始化
    private void initPlay() {
        if(one){
            //退回上一个活动时，刷新
            refreshPlayBar();
            return;
        }
        one=true;
        if(musicPlayBar!=null){
            playButton=musicPlayBar.findViewById(R.id.music_play_button);
            musicName=musicPlayBar.findViewById(R.id.music_name);
            singerName=musicPlayBar.findViewById(R.id.singer_name);
            cover=musicPlayBar.findViewById(R.id.music_cover);

            playButton.addOnClickPlayButtonListener(new PlayButton.OnClickPlayButtonListener() {
                @Override
                public void onStart() {
                    binder.startMusic();
                }

                @Override
                public void onStop() {
                    binder.pauseMusic();
                }
            });

            findViewById(R.id.music_play_list).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPlayList();
                }
            });

            musicPlayBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(BaseActivity.this, MusicPlayActivity.class);
                    BaseActivity.this.startActivity(intent);
                }
            });

            refreshPlayBar();
        }
    }

    protected void showPlayList(){
        listAdapter.setDataList(binder.getPlayList());
        musicPlayList.show();
    }

    //刷新所有控件
    public void refreshPlayBar(){
        //刷新音乐service
        binder.refresh();

        //重新获取播放列表
        listAdapter.setDataList(binder.getPlayList());

        if(binder.getStatus()!=MusicPlayService.NOT_PREPARE){
            musicPlayBar.setVisibility(View.VISIBLE);
            playButton.setMaxProgress(binder.getCurrentMusicDuration());
            playButton.setProgress(binder.getCurrentProgress());
        }else{
            musicPlayBar.setVisibility(View.GONE);
            if(musicPlayList.isShowing())
                musicPlayList.dismiss();
            return ;
        }

        playButton.setStatus(binder.getStatus()==MusicPlayService.IS_START);

        Music item=binder.getCurrentMusic();

        musicName.setText(item.getMusicName());
        singerName.setText(item.getSingerName());
        int isServer=item.getIsServer();

        if(isServer==1){
            cover.setImageUrl(MyEnvironment.serverBasePath+"music/loadMusicCover?singerId="+item.getSingerId().split("/")[0]);
        }else{
            LogUtil.printLog("加载图片：本地音乐图片，无封面，设置为默认图片");
            Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.default_image);
            cover.setImageBitmap(bitmap);
        }
    }

    public void refreshPlayList(){
        listAdapter.setDataList(binder.getPlayList());
    }

    private MusicPlayListAdapter listAdapter;
    private View createContentView() {
        View view=LayoutInflater.from(this).inflate(R.layout.pw_play_list, (ViewGroup) getWindow().getDecorView(),false);

        final LinearLayout random=view.findViewById(R.id.random_play);
        final LinearLayout single=view.findViewById(R.id.single_play);
        final LinearLayout list=view.findViewById(R.id.list_loop);

        switch (binder.getCurrentLoopType()){
            case MusicPlayService.LOOP_TYPE_RANDOM:
                random.setVisibility(View.VISIBLE);
                single.setVisibility(View.GONE);
                list.setVisibility(View.GONE);
                break;
            case MusicPlayService.LOOP_TYPE_LIST:
                random.setVisibility(View.GONE);
                single.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
                break;
            case MusicPlayService.LOOP_TYPE_SINGLE:
                random.setVisibility(View.GONE);
                single.setVisibility(View.VISIBLE);
                list.setVisibility(View.GONE);
                break;
        }

        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (binder.getCurrentLoopType()){
                    case MusicPlayService.LOOP_TYPE_RANDOM:
                        random.setVisibility(View.GONE);
                        single.setVisibility(View.GONE);
                        list.setVisibility(View.VISIBLE);
                        binder.setCurrentLoopType(MusicPlayService.LOOP_TYPE_LIST);
                        break;
                    case MusicPlayService.LOOP_TYPE_LIST:
                        random.setVisibility(View.GONE);
                        single.setVisibility(View.VISIBLE);
                        list.setVisibility(View.GONE);
                        binder.setCurrentLoopType(MusicPlayService.LOOP_TYPE_SINGLE);
                        break;
                    case MusicPlayService.LOOP_TYPE_SINGLE:
                        random.setVisibility(View.VISIBLE);
                        single.setVisibility(View.GONE);
                        list.setVisibility(View.GONE);
                        binder.setCurrentLoopType(MusicPlayService.LOOP_TYPE_RANDOM);
                        break;
                }
            }
        };

        random.setOnClickListener(listener);
        single.setOnClickListener(listener);
        list.setOnClickListener(listener);

        RecyclerView recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(listAdapter=new MusicPlayListAdapter(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView deleteAll=view.findViewById(R.id.delete_play_list);
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binder.deleteAllMusic();
            }
        });

        view.findViewById(R.id.collection_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sp.getBoolean("login",false)){
                    String userId=sp.getString("userId","-1");
                    List<Music> playList=binder.getPlayList();
                    int[] musicIds=new int[playList.size()];
                    for(int i=0;i<playList.size();i++){
                        if(playList.get(i).getIsServer()==1){
                            musicIds[i] = playList.get(i).getId();
                        }
                    }

                    new PlayListDialog(BaseActivity.this,Integer.valueOf(userId),musicIds);
                    musicPlayList.dismiss();
                }else{
                    Toast.makeText(BaseActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    protected void openAlbum(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(autoCloseInputMethod&&ev.getAction()==MotionEvent.ACTION_DOWN){
            View v=getCurrentFocus();
            if(!InputMethodUtil.isClickEditText(v,ev)){
                InputMethodUtil.hideSoftKeyboard(this);
                onClosedInputMethod();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    protected void onClosedInputMethod(){}

    static class MusicPlayHandler extends Handler{
        private BaseActivity activity;

        MusicPlayHandler(BaseActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            if(activity.musicPlayBar==null)
                return;

            switch (msg.what){
                case MusicPlayService.IS_PREPARE:   //其实音乐已经要开始了
                    //播放Bar设置相应的信息
                    activity.refreshPlayBar();

                    //回调函数，有的地方需要准备完成后还要做一点事
                    activity.musicIsToStart();
                    break;

                case MusicPlayService.EVENT_UPDATE_TIME:  //设置进度
                    activity.playButton.setProgress(msg.arg1);
                    break;
                case MusicPlayService.EVENT_DELETE_MUSIC:
                    activity.refreshPlayBar();
                    break;
                case MusicPlayService.EVENT_START_NEW_MUSIC:

                    Music item= (Music) msg.obj;
                    activity.musicName.setText(item.getMusicName());
                    activity.singerName.setText(item.getSingerName());
                    int isServer=item.getIsServer();

                    if(isServer==1){
                        activity.cover.setImageUrl(MyEnvironment.serverBasePath+"music/loadMusicCover?singerId="+item.getSingerId().split("/")[0]);
                    }else{
                        LogUtil.printLog("加载图片：本地音乐图片，无封面，设置为默认图片");
                        Bitmap bitmap=BitmapFactory.decodeResource(activity.getResources(),R.mipmap.default_image);
                        activity.cover.setImageBitmap(bitmap);
                    }
                    activity.playButton.setProgress(0);
                    activity.musicPlayBar.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    public MusicPlayService.MusicPlayBinder getBinder() {
        return binder;
    }

    public DownloadService.DownloadBinder getDownloadBinder() {
        return downloadBinder;
    }

    /**
     * 已经和service完成绑定时调用
     */
    protected void onBindOk(){

    }

    protected void onDownloadBindOk(){

    }

    /**
     * 音乐将要开始时调用
     */
    protected void musicIsToStart(){

    }

    public LocalMusicDao getLocalMusicDao() {
        return localMusicDao;
    }

    public void setAutoCloseInputMethod(boolean autoCloseInputMethod) {
        this.autoCloseInputMethod = autoCloseInputMethod;
    }
}
