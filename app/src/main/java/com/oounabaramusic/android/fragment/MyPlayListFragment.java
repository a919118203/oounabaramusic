package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.AllMyPlayListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyPlayListFragment extends BaseFragment {

    private Activity activity;
    private View rootView;
    private AllMyPlayListAdapter adapter;
    private int userId;
    private boolean initOK;

    public MyPlayListFragment(Activity activity){
        this.activity=activity;
        initOK=false;
        setTitle("创建的歌单");
    }

    public void setUserId(int userId) {
        this.userId = userId;
        initContent();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=LayoutInflater.from(activity).inflate(R.layout.fragment_my_playlist,container,false);
        init();
        return rootView;
    }

    private void init() {
        RecyclerView recyclerView= (RecyclerView) rootView;
        recyclerView.setAdapter(adapter=new AllMyPlayListAdapter(activity));
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        initOK=true;
        initContent();
    }

    private void initContent(){
        if(userId==0||!initOK){
            return;
        }

        adapter.setUserId(userId);
    }
}
