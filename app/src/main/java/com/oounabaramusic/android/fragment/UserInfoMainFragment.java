package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.oounabaramusic.android.CommentActivity;
import com.oounabaramusic.android.ListenRankActivity;
import com.oounabaramusic.android.MoreInfoActivity;
import com.oounabaramusic.android.MorePlayListActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.UserCommentActivity;
import com.oounabaramusic.android.adapter.UserInfoFavoritePlayListAdapter;
import com.oounabaramusic.android.adapter.UserInfoMyPlayListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UserInfoMainFragment extends Fragment {

    private Activity activity;
    private RecyclerView myPlaylist,favoritePlaylist;
    private UserInfoMyPlayListAdapter myAdapter;
    private UserInfoFavoritePlayListAdapter favoriteAdapter;
    private ImageView reSetFocus;

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
        reSetFocus =view.findViewById(R.id.music_play_ranking);
        reSetFocus.setFocusable(true);
        reSetFocus.setFocusableInTouchMode(true);
        resizeFocus();

        //点击听歌排行时
        view.findViewById(R.id.click_music_play_ranking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, ListenRankActivity.class);
                activity.startActivity(intent);
            }
        });

        //创建的歌单下面的更多歌单时
        view.findViewById(R.id.more_my_playlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, MorePlayListActivity.class);
                activity.startActivity(intent);
            }
        });

        //点击我的评论下面的评论时
        view.findViewById(R.id.comment_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, CommentActivity.class);
                activity.startActivity(intent);
            }
        });

        //点击更多评论时
        view.findViewById(R.id.more_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, UserCommentActivity.class);
                activity.startActivity(intent);
            }
        });

        view.findViewById(R.id.more_user_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, MoreInfoActivity.class);
                activity.startActivity(intent);
            }
        });

        view.findViewById(R.id.comment_layout).setBackground(activity.getResources().getDrawable(R.drawable.view_ripple_rectangle));
    }

    public void resizeFocus(){
        reSetFocus.requestFocus();
    }
}
