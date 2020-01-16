package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.SearchMusicAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchMusicFragment extends BaseFragment {

    private Activity activity;
    private View rootView;
    private SearchMusicAdapter adapter;

    public SearchMusicFragment(Activity activity){
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
        RecyclerView rv=view.findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new SearchMusicAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));
    }
}
