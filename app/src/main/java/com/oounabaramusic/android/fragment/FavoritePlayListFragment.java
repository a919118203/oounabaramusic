package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.AllFavoritePlayListAdapter;
import com.oounabaramusic.android.adapter.AllMyPlayListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavoritePlayListFragment extends Fragment {

    private Activity activity;
    private AllFavoritePlayListAdapter adapter;

    public FavoritePlayListFragment(Activity activity){
        this.activity=activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(activity).inflate(R.layout.fragment_my_playlist,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        RecyclerView recyclerView= (RecyclerView) view;
        recyclerView.setAdapter(adapter=new AllFavoritePlayListAdapter(activity));
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
    }
}
