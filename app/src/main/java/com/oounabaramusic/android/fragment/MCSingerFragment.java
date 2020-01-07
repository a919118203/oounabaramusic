package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.MCSingerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MCSingerFragment extends BaseFragment {

    private Activity activity;
    private MCSingerAdapter adapter;

    public MCSingerFragment(Activity activity){
        this.activity=activity;
        setTitle("歌手");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_mc_singer,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        RecyclerView rv= (RecyclerView) view;
        rv.setAdapter(adapter=new MCSingerAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));
    }
}
