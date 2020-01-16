package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.UserInfoPostAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UserInfoPostFragment extends Fragment {

    private Activity activity;
    private View rootView;
    private UserInfoPostAdapter adapter;

    public UserInfoPostFragment(Activity activity){
        this.activity=activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView=LayoutInflater.from(activity).inflate(R.layout.fragment_user_info_post,container,false);
            init(rootView);
        }
        return rootView;
    }

    private void init(View view) {
        RecyclerView recyclerView= (RecyclerView) view;
        recyclerView.setAdapter(adapter=new UserInfoPostAdapter(activity));
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
    }
}
