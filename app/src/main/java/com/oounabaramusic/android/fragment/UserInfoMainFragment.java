package com.oounabaramusic.android.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.oounabaramusic.android.ListenRankActivity;
import com.oounabaramusic.android.MorePlayListActivity;
import com.oounabaramusic.android.PlayListActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.UserInfoActivity;
import com.oounabaramusic.android.adapter.UserInfoFavoritePlayListAdapter;
import com.oounabaramusic.android.adapter.UserInfoMyPlayListAdapter;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.bean.User;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UserInfoMainFragment extends BaseFragment {

    private UserInfoActivity activity;
    private View rootView;
    private RecyclerView myPlaylist,favoritePlaylist;
    private UserInfoMyPlayListAdapter myAdapter;
    private UserInfoFavoritePlayListAdapter favoriteAdapter;
    private ImageView reSetFocus;
    private TextView listenCnt,collectCnt;

    private User user;
    private boolean initOk;

    private TextView myPlayListCnt;
    private TextView moreMyPlayList;
    private TextView favoritePlayListCnt;
    private TextView moreFavoritePlayList;

    private TextView createYearCnt;
    private TextView createYear,createMonth;
    private TextView yearSuffix;
    private TextView sex;
    private TextView introduction;

    private TextView otherListenRank;
    private TextView who;

    public UserInfoMainFragment(UserInfoActivity activity){
        this.activity=activity;
        setTitle("主页");
    }

    public void setUser(User user) {
        this.user = user;
        initContent();
    }

    public int getUserId() {
        return user.getId();
    }

    @SuppressLint("SetTextI18n")
    private void initContent(){
        if(user==null||!initOk)
            return;

        //设置听歌数量和收藏数量
        listenCnt.setText(FormatUtil.numberToString(user.getListenMusicCnt()));
        collectCnt.setText(FormatUtil.numberToString(user.getCollectionCnt()));

        //设置乐龄
        String[] create = FormatUtil.DateToString(user.getCreateTime()).split("-");
        String[] current = FormatUtil.DateToString(new Date()).split("-");

        createYear.setText(String.valueOf(create[0]));
        createMonth.setText(String.valueOf(create[1]));

        int month = Integer.valueOf(current[1])-Integer.valueOf(create[1]);
        int year = Integer.valueOf(current[0])-Integer.valueOf(create[0]);

        if(month<0){
            year--;
            month+=12;
        }

        if(month>6){
            year++;
        }

        if(year>0){
            createYearCnt.setText(String.valueOf(year));
        }else{
            float f = (float) month/12f;
            createYearCnt.setText(String.format("%.1f",f));
        }

        //设置年龄
        if(user.getBirthday()!=null){
            yearSuffix.setText(FormatUtil.DateToString(user.getBirthday()).split("-")[0].substring(2,3)+"后");
        }

        //设置性别
        sex.setText(user.getSex());

        //设置个人介绍
        if(user.getIntroduction()!=null){
            introduction.setText(user.getIntroduction());
        }else{
            introduction.setText("这个人很懒什么都没有写");
        }

        who.setText("我");

        if(SharedPreferencesUtil.getUserId(activity.sp)!=user.getId()){
            initOther();
        }

        //加载创建的歌单
        myAdapter.setUserId(user.getId());

        //加载收藏的歌单
        favoriteAdapter.setUserId(user.getId());
    }

    @SuppressLint("SetTextI18n")
    private void initOther(){
        otherListenRank.setText(user.getUserName()+"的");
        who.setText(user.getUserName());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=LayoutInflater
                .from(activity)
                .inflate(R.layout.fragment_user_info_main,container,false);
        init();
        return rootView;
    }

    private void init() {
        myPlaylist=rootView.findViewById(R.id.my_playlist_recycler_view);
        myPlaylist.setAdapter(myAdapter=new UserInfoMyPlayListAdapter(activity,this));
        myPlaylist.setLayoutManager(new LinearLayoutManager(activity));

        favoritePlaylist=rootView.findViewById(R.id.favorite_playlist_recycler_view);
        favoritePlaylist.setAdapter(favoriteAdapter=new UserInfoFavoritePlayListAdapter(activity,this));
        favoritePlaylist.setLayoutManager(new LinearLayoutManager(activity));

        listenCnt=rootView.findViewById(R.id.listen_cnt);
        collectCnt=rootView.findViewById(R.id.collect_cnt);
        myPlayListCnt=rootView.findViewById(R.id.my_playlist_cnt);
        favoritePlayListCnt=rootView.findViewById(R.id.favorite_playlist_cnt);
        createYearCnt=rootView.findViewById(R.id.year);
        createYear=rootView.findViewById(R.id.sign_up_year);
        createMonth=rootView.findViewById(R.id.sign_up_month);
        yearSuffix=rootView.findViewById(R.id.year_suffix);
        sex=rootView.findViewById(R.id.sex);
        introduction=rootView.findViewById(R.id.introduction);
        otherListenRank=rootView.findViewById(R.id.other_listen_rank);
        who=rootView.findViewById(R.id.who);

        //解决进入这个活动时画面的初始位置在奇怪的地方的问题
        reSetFocus =rootView.findViewById(R.id.music_play_ranking);
        reSetFocus.setFocusable(true);
        reSetFocus.setFocusableInTouchMode(true);
        resizeFocus();

        //点击听歌排行时
        rootView.findViewById(R.id.click_music_play_ranking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, ListenRankActivity.class);
                intent.putExtra("userId",user.getId());
                activity.startActivity(intent);
            }
        });

        //点击我喜欢的音乐
        rootView.findViewById(R.id.my_collection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new S2SHttpUtil(
                        activity,
                        user.getId()+"",
                        MyEnvironment.serverBasePath+"getMyCollection",
                        new MyHandler(UserInfoMainFragment.this))
                .call(BasicCode.GET_CONTENT);
            }
        });


        //创建的歌单下面的更多歌单时
        moreMyPlayList=rootView.findViewById(R.id.more_my_playlist);
        moreMyPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, MorePlayListActivity.class);
                intent.putExtra("userId",user.getId());
                activity.startActivity(intent);
            }
        });

        moreFavoritePlayList=rootView.findViewById(R.id.more_favorite_playlist);
        moreFavoritePlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, MorePlayListActivity.class);
                intent.putExtra("userId",user.getId());
                activity.startActivity(intent);
            }
        });

        initOk=true;
        initContent();
    }

    public void setMyPlayListCnt(int cnt){
        myPlayListCnt.setText(String.valueOf(cnt));
        if(cnt>10){
            moreMyPlayList.setVisibility(View.VISIBLE);
        }else{
            moreMyPlayList.setVisibility(View.GONE);
        }
    }

    public void setFavoritePlayListCnt(int cnt) {
        favoritePlayListCnt.setText(String.valueOf(cnt));
        if(cnt>10){
            moreFavoritePlayList.setVisibility(View.VISIBLE);
        }else{
            moreFavoritePlayList.setVisibility(View.GONE);
        }
    }

    public void resizeFocus(){
        reSetFocus.requestFocus();
    }

    static class MyHandler extends Handler{
        UserInfoMainFragment fragment;
        MyHandler(UserInfoMainFragment fragment){
            this.fragment=fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    PlayList playList = new Gson()
                            .fromJson((String)msg.obj,PlayList.class);

                    PlayListActivity.startActivity(fragment.activity,playList.getId());
                    break;
            }
        }
    }
}
