package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.MyMessageCommentAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyMessageCommentFragment extends BaseFragment {

    private Activity activity;
    private MyMessageCommentAdapter adapter;

    public MyMessageCommentFragment(Activity activity){
        this.activity=activity;
        setTitle("评论");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(activity).inflate(R.layout.fragment_my_message_comment,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        RecyclerView rv= (RecyclerView) view;
        rv.setAdapter(adapter=new MyMessageCommentAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));
    }
}
