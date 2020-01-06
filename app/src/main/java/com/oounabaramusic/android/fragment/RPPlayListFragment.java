package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.RPPlayListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RPPlayListFragment extends BaseFragment {

    private Activity activity;
    private RPPlayListAdapter adapter;

    public RPPlayListFragment(Activity activity){
        this.activity=activity;
        setTitle("歌单");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(activity).inflate(R.layout.fragment_rp_play_list,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        RecyclerView rv= (RecyclerView) view;
        rv.setAdapter(adapter=new RPPlayListAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));
    }
}
