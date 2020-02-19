package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SearchActivity;
import com.oounabaramusic.android.adapter.SearchMusicAdapter;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.service.MusicPlayService;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SearchMusicFragment extends BaseFragment {

    private SearchActivity activity;
    private View rootView;
    private SearchMusicAdapter adapter;
    private RecyclerView rv;
    private SwipeRefreshLayout srl;
    private boolean f;

    public SearchMusicFragment(SearchActivity activity){
        this.activity=activity;
        setTitle("单曲");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_search_music,container,false);
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
                f=false;
                adapter.getContent();
            }
        });

        rv=rootView.findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new SearchMusicAdapter(activity,srl));
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

    @Override
    public void notifyFragment() {
        f=false;
    }
}
