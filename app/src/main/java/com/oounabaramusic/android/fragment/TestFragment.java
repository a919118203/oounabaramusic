package com.oounabaramusic.android.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.oounabaramusic.android.MyCollectionActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Video;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.VideoUtil;
import com.oounabaramusic.android.widget.customview.MyVideoPlayer;
import com.oounabaramusic.android.widget.textview.TextViewCell;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestFragment extends BaseFragment implements View.OnClickListener{

    private MyCollectionActivity activity;
    private View rootView;
    private VideoView videoView;

    private ImageView imageView;

    private MyVideoPlayer videoPlayer;
    public TestFragment(MyCollectionActivity activity){
        this.activity=activity;
        setTitle("测试用");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(R.layout.fragment_test,container,false);
            init();
        }
        return rootView;
    }


    private void init(){


//        imageView=rootView.findViewById(R.id.image);
//        imageView.setImageBitmap(VideoUtil.getVideoCover("http://192.168.1.7:8080/OounabaraMusic/video/2.mp4"));


//        rootView.findViewById(R.id.dianji).setOnClickListener(this);
//        videoView=rootView.findViewById(R.id.video);
//        videoView.setVideoURI(Uri.parse("http://192.168.1.7:8080/OounabaraMusic/video/2.mp4"));
//        videoView.requestFocus();
//        videoView.start();

//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url("http://192.168.1.7:8080/OounabaraMusic/video/2.mp4")
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                InputStream is = response.body().byteStream();
//                byte[] lins = new byte[1024];
//                int len;
//                while((len=is.read(lins))!=-1){
//                    LogUtil.printLog("len:  "+len);
//                }
//                LogUtil.printLog(response.body().string());
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dianji:

                break;
        }
    }
}
