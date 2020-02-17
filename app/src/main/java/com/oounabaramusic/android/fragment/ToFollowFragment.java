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
import com.oounabaramusic.android.adapter.ToFollowAdapter;
import com.oounabaramusic.android.bean.User;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ToFollowFragment extends BaseFragment {

    private BaseActivity activity;
    private View rootView;
    private ToFollowAdapter adapter;
    private int userId;

    public ToFollowFragment(BaseActivity activity,int userId){
        this.activity=activity;
        this.userId=userId;
        setTitle("关注");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=LayoutInflater.from(activity).inflate(R.layout.fragment_to_follow,container,false);
        init();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initContent();
    }

    public int getUserId() {
        return userId;
    }

    private void init() {
        RecyclerView recyclerView= (RecyclerView) rootView;
        recyclerView.setAdapter(adapter=new ToFollowAdapter(activity,this));
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
    }

    private void initContent(){
        LogUtil.printLog("initContent");
        new S2SHttpUtil(
                activity,
                userId+"",
                MyEnvironment.serverBasePath+"getToFollow",
                new MyHandler(this))
        .call(BasicCode.GET_CONTENT);
    }

    static class MyHandler extends Handler{
        ToFollowFragment fragment;
        MyHandler(ToFollowFragment fragment){
            this.fragment=fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    List<User> dataList = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<User>>(){}.getType());
                    fragment.adapter.setDataList(dataList);
                    break;
            }
        }
    }
}
