package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.UserInfoActivity;
import com.oounabaramusic.android.adapter.UserInfoFavoritePlayListAdapter;
import com.oounabaramusic.android.adapter.UserInfoMyPlayListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UserInfoMainFragment extends Fragment {

    private Activity activity;
    private RecyclerView myPlaylist,favoritePlaylist;
    private UserInfoMyPlayListAdapter myAdapter;
    private UserInfoFavoritePlayListAdapter favoriteAdapter;

    public UserInfoMainFragment(Activity activity){
        this.activity=activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(activity).inflate(R.layout.fragment_user_info_main,container,false);
        init(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private void init(View view) {
        myPlaylist=view.findViewById(R.id.my_playlist_recycler_view);
        myPlaylist.setAdapter(myAdapter=new UserInfoMyPlayListAdapter(activity));
        myPlaylist.setLayoutManager(new LinearLayoutManager(activity));

        favoritePlaylist=view.findViewById(R.id.favorite_playlist_recycler_view);
        favoritePlaylist.setAdapter(favoriteAdapter=new UserInfoFavoritePlayListAdapter(activity));
        favoritePlaylist.setLayoutManager(new LinearLayoutManager(activity));



        //解决进入这个活动时画面的初始位置在奇怪的地方的问题
        ImageView imageView=view.findViewById(R.id.music_play_ranking);
        imageView.setFocusable(true);
        imageView.setFocusableInTouchMode(true);
        imageView.requestFocus();

    }
}
