package com.oounabaramusic.android.widget.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.VideoUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyVideoPlayer extends FrameLayout {

    private VideoView videoView;
    private ImageView cover;
    private ImageView playIcon;
    private String filePath;

    private MediaController controller;
    private ProgressBar pb;

    private View controllerView;

    public MyVideoPlayer(@NonNull Context context) {
        super(context);
    }

    public MyVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.special_video_player,this);
        init();
    }

    public MyVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setCover(Bitmap cover) {
        this.cover.setImageBitmap(cover);
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        setCover(VideoUtil.getVideoCover(filePath));
    }

    private void init(){
        videoView=findViewById(R.id.video_view);
        cover=findViewById(R.id.cover);
        playIcon=findViewById(R.id.play_icon);
        pb=findViewById(R.id.pb);
        controllerView=findViewById(R.id.controller);

        controller=new MediaController(getContext());
        controller.setMediaPlayer(videoView);
        videoView.setMediaController(controller);

        cover.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                prepare();
                cover.setVisibility(GONE);
                playIcon.setVisibility(GONE);
                pb.setVisibility(VISIBLE);
                controllerView.setVisibility(VISIBLE);
                videoView.start();
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                pb.setVisibility(GONE);
            }
        });

        controllerView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(controller.isShowing()){
                    controller.hide();
                }else{
                    controller.show(5000);
                }
            }
        });


    }

    public void prepare(){
        if(filePath==null){
            Toast.makeText(getContext(), "视频路径不正确", Toast.LENGTH_SHORT).show();
            return;
        }

        if(filePath.contains("http")){
            videoView.setVideoURI(Uri.parse(filePath));
        }else{
            videoView.setVideoPath(filePath);
        }
    }

    public void start(){
        videoView.start();
    }

    public void pause(){
        videoView.pause();
    }

    private class ReplayListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            videoView.resume();
        }
    }
}
