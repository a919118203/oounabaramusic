package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.DMDownloadingAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DMDownloadingFragment extends BaseFragment {

    private Activity activity;
    private View rootView;
    private DMDownloadingAdapter adapter;

    public DMDownloadingFragment(Activity activity){
        this.activity=activity;
        setTitle("下载中");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(R.layout.fragment_downloading,container,false);
            init(rootView);
        }
        return rootView;
    }

    private void init(View view) {
        RecyclerView rv=view.findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new DMDownloadingAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));
    }
}
