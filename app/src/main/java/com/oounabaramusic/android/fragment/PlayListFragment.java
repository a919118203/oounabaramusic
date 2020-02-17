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
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.PlayListFragmentAdapter;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.bean.PlayListSmallTag;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PlayListFragment extends BaseFragment {

    private Activity activity;
    private PlayListFragmentAdapter adapter;
    private PlayListSmallTag tag;
    private int start,len;

    public PlayListFragment(Activity activity,PlayListSmallTag tag){
        this.activity=activity;
        this.tag=tag;
        setTitle(tag.getName());
    }

    public void setTag(PlayListSmallTag tag) {
        this.tag = tag;
        setTitle(tag.getName());
    }

    @Nullable
    public PlayListSmallTag getMyTag() {
        return tag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_play_list,container,false);
        init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initContent();
    }

    private void init(View view) {
        RecyclerView rv= (RecyclerView) view;
        rv.setAdapter(adapter=new PlayListFragmentAdapter(activity));
        rv.setLayoutManager(new GridLayoutManager(activity,3));

        start=0;
        len=30;
    }

    public void initContent(){

        List<Integer> data = new ArrayList<>();
        data.add(tag.getId());
        data.add(start);
        data.add(len);

        new S2SHttpUtil(
                activity,
                new Gson().toJson(data),
                MyEnvironment.serverBasePath+"getPlayListByTag",
                new MyHandler(this))
        .call(BasicCode.GET_CONTENT);
    }

    static class MyHandler extends Handler{

        PlayListFragment fragment;

        MyHandler(PlayListFragment fragment){
            this.fragment=fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    List<PlayList> dataList = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<PlayList>>(){}.getType());

                    fragment.adapter.setDataList(dataList);
                    break;
            }
        }
    }
}
