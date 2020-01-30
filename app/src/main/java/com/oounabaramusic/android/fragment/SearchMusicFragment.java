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
import com.oounabaramusic.android.adapter.SearchMusicAdapter;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.service.MusicPlayService;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchMusicFragment extends BaseFragment {

    private BaseActivity activity;
    private View rootView;
    private SearchMusicAdapter adapter;
    private ProgressBar pb;
    private LinearLayout playAll;
    private RecyclerView rv;

    public SearchMusicFragment(BaseActivity activity){
        this.activity=activity;
        setTitle("单曲");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(R.layout.fragment_search_music,container,false);
            init(rootView);
        }
        return rootView;
    }

    private void init(View view) {
        rv=view.findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new SearchMusicAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));

        pb=view.findViewById(R.id.loading);
        playAll=view.findViewById(R.id.play_all);
    }

    public void setDataList(List<Music> dataList){
        adapter.setDataList(dataList);

        pb.setVisibility(View.GONE);
        playAll.setVisibility(View.VISIBLE);
        rv.setVisibility(View.VISIBLE);
    }

    @Override
    public void notifyFragment() {
        if(pb!=null){
            pb.setVisibility(View.VISIBLE);
            playAll.setVisibility(View.GONE);
            rv.setVisibility(View.GONE);
        }
    }
}
