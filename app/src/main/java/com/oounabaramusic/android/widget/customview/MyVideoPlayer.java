package com.oounabaramusic.android.widget.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
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
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.Video;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.VideoUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyVideoPlayer extends FrameLayout {

    public static final int LOAD_IMAGE_END=1;
    private VideoView videoView;
    private MyImageView cover;
    private ImageView playIcon;

    private MediaController controller;
    private ProgressBar pb;

    private View controllerView;

    private Video video;

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

    public void setVideo(Video video) {
        this.video = video;

        cover.setImageBitmap(null);
        cover.setImage(new MyImage(MyImage.TYPE_VIDEO_COVER,video.getId()));
        cover.setVisibility(VISIBLE);
        playIcon.setVisibility(VISIBLE);

        pb.setVisibility(GONE);
        controllerView.setVisibility(GONE);
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
                //将封面隐藏
                cover.setVisibility(GONE);
                playIcon.setVisibility(GONE);

                //显示正在加载
                pb.setVisibility(VISIBLE);

                //用户将整个View挡住  好控制mediaController显示的时机
                controllerView.setVisibility(VISIBLE);

                //开始
                videoView.start();
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if(pb.getVisibility()==VISIBLE){
                    pb.setVisibility(GONE);
                }else{
                    mp.stop();
                }
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                pb.setVisibility(GONE);
                cover.setVisibility(VISIBLE);
                playIcon.setVisibility(VISIBLE);
                controllerView.setVisibility(GONE);
                Toast.makeText(getContext(), "加载失败,请稍后再试", Toast.LENGTH_SHORT).show();
                return true;
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
        if(video==null||video.getFilePath()==null){
            Toast.makeText(getContext(), "视频路径不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        LogUtil.printLog("视频路径："+video.getFilePath());
        String filePath = video.getFilePath();

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
}
