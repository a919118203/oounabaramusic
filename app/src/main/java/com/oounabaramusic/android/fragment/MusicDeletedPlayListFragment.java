package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.ResumePlayListActivity;
import com.oounabaramusic.android.adapter.MusicDeletedPlayListAdapter;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MusicDeletedPlayListFragment extends BaseFragment {

    private ResumePlayListActivity activity;
    private View rootView;
    private MusicDeletedPlayListAdapter adapter;

    public MusicDeletedPlayListFragment(ResumePlayListActivity activity){
        this.activity=activity;
        setTitle("歌单内单曲");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView=LayoutInflater.from(activity).inflate(R.layout.fragment_music_deleted_playlist,container,false);
            init(rootView);
        }
        return rootView;
    }

    private void init(View view) {
        RecyclerView rv= view.findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new MusicDeletedPlayListAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));

        initContent();
    }

    private void initContent(){
        String userId=activity.sp.getString("userId","-1");
        int a=Integer.valueOf(userId);
        new S2SHttpUtil(activity,
                new Gson().toJson(a),
                MyEnvironment.serverBasePath+"getMusicDeletedPlayList",
                new MyHandler(this))
        .call(BasicCode.GET_DELETE_MUSIC_PLAY_LIST_END);
    }

    static class MyHandler extends Handler{
        MusicDeletedPlayListFragment fragment;

        MyHandler(MusicDeletedPlayListFragment fragment){
            this.fragment=fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_DELETE_MUSIC_PLAY_LIST_END:
                    List<PlayList> data = new Gson().fromJson((String)msg.obj,
                            new TypeToken<List<PlayList>>(){}.getType());
                    fragment.adapter.setDataList(data);
                    break;
            }
        }
    }
}
