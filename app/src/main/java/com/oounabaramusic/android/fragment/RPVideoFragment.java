package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.RPVideoAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RPVideoFragment extends BaseFragment {

    private Activity activity;
    private RPVideoAdapter adapter;

    public RPVideoFragment(Activity activity){
        this.activity=activity;
        setTitle("视频");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(activity).inflate(R.layout.fragment_rp_video,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        RecyclerView rv= (RecyclerView) view;
        rv.setAdapter(adapter=new RPVideoAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));
    }
}
