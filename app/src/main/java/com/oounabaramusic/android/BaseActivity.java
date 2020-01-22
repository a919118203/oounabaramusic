package com.oounabaramusic.android;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.oounabaramusic.android.adapter.MusicPlayListAdapter;
import com.oounabaramusic.android.service.MusicPlayService;
import com.oounabaramusic.android.util.ActivityManager;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.widget.customview.PlayButton;
import com.oounabaramusic.android.widget.popupwindow.MyPopupWindow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BaseActivity extends AppCompatActivity {
    protected static final int CHOOSE_PHOTO=1;     //打开相册选择图片
    private MyPopupWindow musicPlayList;
    private Handler handler=new MusicPlayHandler(this);
    private MusicPlayService.MusicPlayBinder binder;
    private ServiceConnection connection;

    private RelativeLayout musicPlayBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.printLog("onCreate: "+this.toString());
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
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
        LogUtil.printLog("onStop: "+this.toString());
        if(musicPlayBar!=null){
            unbindService(connection);
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        LogUtil.printLog("onStart: "+this.toString());
        super.onStart();
        init();

        LogUtil.printLog("musicPlayBar: "+(musicPlayBar==null?null:musicPlayBar.toString()));
        if(musicPlayBar!=null){
            bindService(new Intent(this, MusicPlayService.class),connection=new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    binder= (MusicPlayService.MusicPlayBinder) service;
                    binder.setHandler(handler);
                    initPlay();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            }, BIND_AUTO_CREATE);
        }
    }

    private PlayButton playButton;
    private void initPlay() {
        musicPlayList=new MyPopupWindow(this,createContentView());
        if(musicPlayBar!=null){

            playButton=findViewById(R.id.music_play_button);
            playButton.setStatus(binder.getStatus()==MusicPlayService.IS_START);
            if(binder.getStatus()!=MusicPlayService.NOT_PREPARE){
                musicPlayBar.setVisibility(View.VISIBLE);
                playButton.setMaxProgress(binder.getCurrentMusicDuration());
            }

            playButton.addOnClickPlayButtonListener(new PlayButton.OnClickPlayButtonListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onStop() {

                }
            });
            findViewById(R.id.music_play_list).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musicPlayList.showPopupWindow();
                }
            });

            findViewById(R.id.tool_current_play_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(BaseActivity.this, MusicPlayActivity.class);
                    BaseActivity.this.startActivity(intent);
                }
            });
        }
    }
    private View createContentView() {
        View view=LayoutInflater.from(this).inflate(R.layout.pw_play_list, (ViewGroup) getWindow().getDecorView(),false);

        RecyclerView recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new MusicPlayListAdapter(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        return view;
    }

    protected void openAlbum(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    static class MusicPlayHandler extends Handler{
        private BaseActivity activity;

        MusicPlayHandler(BaseActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            if(activity.findViewById(R.id.tool_current_play_layout)==null)
                return;

            switch (msg.what){
                case MusicPlayService.IS_PREPARE:
                    activity.playButton.setMaxProgress(activity.binder.getCurrentMusicDuration());
                    activity.playButton.setStatus(true);
                    activity.musicPlayBar.setVisibility(View.VISIBLE);
                    break;
                case MusicPlayService.EVENT_UPDATE_TIME:
                    activity.playButton.setProgress(msg.arg1);
                    break;
            }
        }
    }

    public MusicPlayService.MusicPlayBinder getBinder() {
        return binder;
    }
}
