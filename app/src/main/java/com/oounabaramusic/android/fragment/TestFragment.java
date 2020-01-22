package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oounabaramusic.android.MyCollectionActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.anim.TextSizeChangeAnimation;
import com.oounabaramusic.android.okhttputil.HttpUtil;
import com.oounabaramusic.android.service.MusicPlayService;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.MyImageView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TestFragment extends BaseFragment implements View.OnClickListener{

    private MyCollectionActivity activity;
    private View rootView;
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

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
        Intent intent=new Intent(activity, MusicPlayService.class);
        activity.startService(intent);
    }

    public void onStopService(View v){
        Intent intent=new Intent(activity, MusicPlayService.class);
        activity.stopService(intent);
    }

    public void onBind(View v){
        Intent intent=new Intent(activity,MusicPlayService.class);
        activity.bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }

    public void onUnBind(View v){
        activity.unbindService(connection);
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
