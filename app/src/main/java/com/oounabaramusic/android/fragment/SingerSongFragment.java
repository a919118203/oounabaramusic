package com.oounabaramusic.android.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SingerActivity;
import com.oounabaramusic.android.adapter.SingerMusicMenuAdapter;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.bean.Singer;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SingerSongFragment extends BaseFragment {

    private SingerActivity activity;
    private SingerMusicMenuAdapter adapter;
    private Handler handler;

    private boolean end;  //还有没有歌

    public SingerSongFragment(SingerActivity activity){
        this.activity=activity;
        setTitle("歌曲");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_singer_song,container,false);
        init(view);

        Singer data = new Singer();
        data.setId(activity.getSingerId());
        data.setStart(0);

        new S2SHttpUtil(
                activity,
                new Gson().toJson(data),
                MyEnvironment.serverBasePath+"music/searchMusicBySinger",
                handler=new MusicHandler(this))
        .call(BasicCode.GET_CONTENT);
        return view;
    }

    private void getNextContent(){
        if(end){
            return;
        }

        Singer data = new Singer();
        data.setId(activity.getSingerId());
        data.setStart(adapter.getItemCount());

        new S2SHttpUtil(
                activity,
                new Gson().toJson(data),
                MyEnvironment.serverBasePath+"music/searchMusicBySinger",
                handler)
                .call(BasicCode.GET_CONTENT_2);
    }

    private void init(View view) {
        RecyclerView rv= (RecyclerView) view;
        rv.setAdapter(adapter=new SingerMusicMenuAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();

                if(llm.findLastVisibleItemPosition()==adapter.getItemCount()-1){
                    getNextContent();
                }
            }
        });
    }

    static class MusicHandler extends Handler {

        private SingerSongFragment fragment;

        MusicHandler(SingerSongFragment fragment){
            this.fragment=fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            List<String> data = new Gson().fromJson((String) msg.obj,
                    new TypeToken<List<String>>(){}.getType());

            List<Music> dataList= new ArrayList<>();
            for(String str : data){
                dataList.add(new Music(str));
            }

            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    fragment.end=false;
                    fragment.adapter.setDataList(dataList);
                    break;

                case BasicCode.GET_CONTENT_2:
                    if(dataList.isEmpty()){
                        fragment.end=true;
                    }
                    fragment.adapter.addDataList(dataList);
                    break;
            }
        }
    }
}
