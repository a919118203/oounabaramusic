package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.MCVideoAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MCVideoFragment extends BaseFragment {

    private BaseActivity activity;
    private View rootView;
    private MCVideoAdapter adapter;

    private SwipeRefreshLayout srl;
    private boolean f;

    public MCVideoFragment(BaseActivity activity){
        this.activity=activity;
        setTitle("点赞的视频");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_mc_video,container,false);
        init();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!f){
            f=true;
            adapter.getContent();
        }
    }

    private void init() {
        f=false;

        srl= (SwipeRefreshLayout) rootView;
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.getContent();
            }
        });

        RecyclerView rv = rootView.findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new MCVideoAdapter(activity,srl));
        rv.setLayoutManager(new LinearLayoutManager(activity));

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if(llm.findLastVisibleItemPosition()==adapter.getItemCount()-1){
                    adapter.getNextContent();
                }
            }
        });
    }
}
