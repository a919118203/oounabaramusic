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

import com.google.gson.Gson;
import com.oounabaramusic.android.MyCollectionActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.MyImage;
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
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
        rootView=inflater.inflate(R.layout.fragment_test,container,false);
        init();
        return rootView;
    }


    private void init(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = MyEnvironment.serverBasePath+"loadImage";

                OkHttpClient client = new OkHttpClient();

                MyImage image = new MyImage();
                image.setContentType(0);
                image.setContentId(5);

                RequestBody body = new FormBody.Builder()
                        .add("json",new Gson().toJson(image))
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        LogUtil.printLog("test:  "+response.header("md5"));
                        response.close();
                    }
                });

            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dianji:

                break;
        }
    }
}
