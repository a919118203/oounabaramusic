package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.FollowedAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FollowedFragment extends Fragment {

    private Activity activity;
    private FollowedAdapter adapter;

    public FollowedFragment(Activity activity){
        this.activity=activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(activity).inflate(R.layout.fragment_followed,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        RecyclerView recyclerView= (RecyclerView) view;
        recyclerView.setAdapter(adapter=new FollowedAdapter(activity));
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
    }
}
