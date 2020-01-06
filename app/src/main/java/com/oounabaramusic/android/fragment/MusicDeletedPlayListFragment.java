package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.ResumePlayListActivity;
import com.oounabaramusic.android.adapter.MusicDeletedPlayListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MusicDeletedPlayListFragment extends BaseFragment {

    private ResumePlayListActivity activity;
    private MusicDeletedPlayListAdapter adapter;

    public MusicDeletedPlayListFragment(ResumePlayListActivity activity){
        this.activity=activity;
        setTitle("歌单内单曲");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(activity).inflate(R.layout.fragment_music_deleted_playlist,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        RecyclerView rv= view.findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new MusicDeletedPlayListAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));
    }
}
