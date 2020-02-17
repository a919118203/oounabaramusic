package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.FollowedAdapter;
import com.oounabaramusic.android.bean.User;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FollowedFragment extends BaseFragment {

    private BaseActivity activity;
    private View rootView;
    private FollowedAdapter adapter;
    private int userId;

    public FollowedFragment(BaseActivity activity,int userId){
        this.activity=activity;
        this.userId=userId;
        setTitle("粉丝");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=LayoutInflater.from(activity).inflate(R.layout.fragment_followed,container,false);
        init();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initContent();
    }

    private void init() {
        RecyclerView recyclerView= (RecyclerView) rootView;
        recyclerView.setAdapter(adapter=new FollowedAdapter(activity,this));
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
    }

    private void initContent(){
        Map<String,Integer> data =new HashMap<>();
        data.put("userId",userId);
        data.put("mainUserId", SharedPreferencesUtil.getUserId(activity.sp));

        new S2SHttpUtil(
                activity,
                new Gson().toJson(data),
                MyEnvironment.serverBasePath+"getFollowed",
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT);
    }

    static class MyHandler extends Handler {
        FollowedFragment fragment;
        MyHandler(FollowedFragment fragment){
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
