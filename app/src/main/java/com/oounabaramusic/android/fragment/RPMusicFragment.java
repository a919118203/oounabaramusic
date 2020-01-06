package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.RPMusicAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RPMusicFragment extends BaseFragment {

    private Activity activity;
    private RPMusicAdapter adapter;

    public RPMusicFragment(Activity activity){
        this.activity=activity;
        setTitle("歌曲");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(activity).inflate(R.layout.fragment_rp_music,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        RecyclerView rv=view.findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new RPMusicAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));
    }
}
