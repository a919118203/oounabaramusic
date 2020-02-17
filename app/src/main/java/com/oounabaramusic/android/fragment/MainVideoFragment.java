package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SendVideoPostActivity;
import com.oounabaramusic.android.adapter.MainVideoAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainVideoFragment extends BaseFragment implements View.OnClickListener{

    private BaseActivity activity;
    private View rootView;
    private MainVideoAdapter adapter;

    private FloatingActionButton button;
    private SwipeRefreshLayout srl;

    private boolean f;

    public MainVideoFragment(BaseActivity activity){
        this.activity=activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_main_video,container,false);
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

        srl = rootView.findViewById(R.id.swipe_refresh);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.getContent();
            }
        });

        RecyclerView rv= rootView.findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new MainVideoAdapter(activity,srl));
        rv.setLayoutManager(new LinearLayoutManager(activity));

        button=rootView.findViewById(R.id.send_video);
        button.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_video:
                SendVideoPostActivity.startActivity(activity);
                break;
        }
    }
}
