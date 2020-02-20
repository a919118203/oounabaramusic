package com.oounabaramusic.android.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class SingerMainFragment extends BaseFragment {

    private SingerActivity activity;
    private ProgressBar loading;
    private RecyclerView rv;
    private SingerMusicMenuAdapter adapter;
    private Handler handler;
    private TextView singerIntroduction;

    public SingerMainFragment(SingerActivity activity){
        this.activity=activity;
        setTitle("主页");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_singer_main,container,false);
        init(view);

        Singer data = new Singer();
        data.setId(activity.getSingerId());
        data.setStart(0);

        new S2SHttpUtil(
                activity,
                new Gson().toJson(data),
                MyEnvironment.serverBasePath+"music/searchMusicBySinger",
                handler)
        .call(BasicCode.GET_CONTENT);
        return view;
    }

    private void init(View view) {
        handler=new MusicHandler(this);

        singerIntroduction=view.findViewById(R.id.singer_introduction);

        rv=view.findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new SingerMusicMenuAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));

        loading=view.findViewById(R.id.loading);

        view.findViewById(R.id.more_music).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.moreTo(1);
            }
        });

        refresh();
    }

    public void refresh(){
        Singer singer = activity.getSinger();
        if(singer==null){
            return;
        }

        singerIntroduction.setText(singer.getIntroduction());
    }

    static class MusicHandler extends Handler{

        private SingerMainFragment fragment;

        MusicHandler(SingerMainFragment fragment){
            this.fragment=fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    List<String> data = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<String>>(){}.getType());

                    List<Music> dataList= new ArrayList<>();
                    for(String str : data){
                        dataList.add(new Music(str));
                        if(dataList.size()==5){
                            break;
                        }
                    }

                    fragment.loading.setVisibility(View.GONE);
                    fragment.rv.setVisibility(View.VISIBLE);
                    fragment.adapter.setDataList(dataList);
                    break;
            }
        }
    }
}
