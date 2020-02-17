package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SingerClassificationActivity;
import com.oounabaramusic.android.adapter.MCSingerAdapter;
import com.oounabaramusic.android.bean.Singer;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MCSingerFragment extends BaseFragment {

    private BaseActivity activity;
    private View rootView;
    private MCSingerAdapter adapter;
    private MyHandler mHandler;

    public MCSingerFragment(BaseActivity activity){
        this.activity=activity;
        mHandler=new MyHandler(this);
        setTitle("歌手");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(R.layout.fragment_mc_singer,container,false);
            init(rootView);
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initContent();
    }

    public MyHandler getHandler() {
        return mHandler;
    }

    private void init(View view) {
        RecyclerView rv= (RecyclerView) view;
        rv.setAdapter(adapter=new MCSingerAdapter(activity,this));
        rv.setLayoutManager(new LinearLayoutManager(activity));

        initContent();
    }

    private void initContent(){
        new S2SHttpUtil(activity,
                activity.sp.getString("userId","-1"),
                MyEnvironment.serverBasePath+"music/getFollowedSinger",
                mHandler)
        .call(BasicCode.GET_FOLLOWED_SINGER_END);
    }

    static class MyHandler extends Handler{
        MCSingerFragment fragment;
        MyHandler(MCSingerFragment fragment){
            this.fragment=fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_FOLLOWED_SINGER_END:

                    List<Singer> singers = new Gson().fromJson((String)msg.obj,
                            new TypeToken<List<Singer>>(){}.getType());
                    fragment.adapter.setDataList(singers);
                    break;

                case SingerClassificationActivity.MESSAGE_CANCEL_FOLLOW_END:
                    fragment.initContent();
                    break;
            }
        }
    }
}
