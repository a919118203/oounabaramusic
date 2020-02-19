package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SingerActivity;
import com.oounabaramusic.android.adapter.SingerMusicMenuAdapter;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.okhttputil.SearchHttpUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SingerSongFragment extends BaseFragment {

    private SingerActivity activity;
    private SingerMusicMenuAdapter adapter;
    private Handler handler;

    public SingerSongFragment(SingerActivity activity){
        this.activity=activity;
        setTitle("歌曲");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_singer_song,container,false);
        init(view);

        SearchHttpUtil.searchMusicBySingerId(activity,
                activity.getSingerId()+"","0","50",
                handler=new MusicHandler(this));
        return view;
    }

    private void init(View view) {
        RecyclerView rv= (RecyclerView) view;
        rv.setAdapter(adapter=new SingerMusicMenuAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));
    }

    static class MusicHandler extends Handler {

        private SingerSongFragment fragment;

        MusicHandler(SingerSongFragment fragment){
            this.fragment=fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SingerMainFragment.MESSAGE_SEARCH_END:
                    List<Music> dataList= (List<Music>) msg.obj;
                    fragment.adapter.setDataList(dataList);
                    break;
            }
        }
    }
}
