package com.oounabaramusic.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.MyCollectionActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestFragment extends BaseFragment implements View.OnClickListener{

    private MyCollectionActivity activity;
    private View rootView;

    public TestFragment(MyCollectionActivity activity){
        this.activity=activity;
        setTitle("测试用");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(R.layout.fragment_test,container,false);
            init(rootView);
        }
        return rootView;
    }

    private void init(View view) {
        view.findViewById(R.id.start).setOnClickListener(this);
        view.findViewById(R.id.stop).setOnClickListener(this);
        view.findViewById(R.id.bind).setOnClickListener(this);
        view.findViewById(R.id.unbind).setOnClickListener(this);

    }

    public void onStartService(View v){
        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder()
                .url(MyEnvironment.serverBasePath+"test")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtil.printLog("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                LogUtil.printLog("????????????????????????????????????????????????");
            }
        });
    }

    public void onStopService(View v){
    }

    public void onBind(View v){
    }

    public void onUnBind(View v){

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start:
                onStartService(v);
                break;
            case R.id.stop:
                onStopService(v);
                break;
            case R.id.bind:
                onBind(v);
                break;
            case R.id.unbind:
                onUnBind(v);
                break;
        }
    }
}
