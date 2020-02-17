package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.PrivateMessageAdapter;
import com.oounabaramusic.android.bean.Message;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class PrivateMessageFragment extends BaseFragment {

    private BaseActivity activity;
    private View rootView;
    private PrivateMessageAdapter adapter;
    private SwipeRefreshLayout srl;

    public PrivateMessageFragment(BaseActivity activity){
        this.activity=activity;
        setTitle("私信");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=LayoutInflater.from(activity).inflate(R.layout.fragment_private_message,container,false);
        init();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initContent();
    }

    private void init() {
        RecyclerView rv= rootView.findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new PrivateMessageAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));

        srl= (SwipeRefreshLayout) rootView;
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initContent();
            }
        });
    }

    private void initContent(){
        srl.setRefreshing(true);

        int userId = SharedPreferencesUtil.getUserId(activity.sp);
        new S2SHttpUtil(
                activity,
                userId+"",
                MyEnvironment.serverBasePath+"getMyMessage",
                new MyHandler(this))
        .call(BasicCode.GET_CONTENT);
    }

    static class MyHandler extends Handler{

        PrivateMessageFragment fragment;
        MyHandler(PrivateMessageFragment fragment){
            this.fragment=fragment;
        }
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    List<Message> dataList = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<Message>>(){}.getType());
                    LogUtil.printLog("服务器传过来的数据："+(String) msg.obj);
                    fragment.adapter.setDataList(dataList);
                    fragment.srl.setRefreshing(false);
                    break;
            }
        }
    }
}
