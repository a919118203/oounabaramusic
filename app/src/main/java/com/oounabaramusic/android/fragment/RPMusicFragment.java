package com.oounabaramusic.android.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.RecentlyPlayedActivity;
import com.oounabaramusic.android.adapter.RPMusicChoiceAdapter;
import com.oounabaramusic.android.adapter.RPMusicNormalAdapter;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.service.MusicPlayService;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.popupwindow.PlayListDialog;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RPMusicFragment extends BaseFragment implements View.OnClickListener{

    private RecentlyPlayedActivity activity;
    private View rootView;
    private RPMusicNormalAdapter normalAdapter;
    private RPMusicChoiceAdapter choiceAdapter;
    private LinearLayout playAll;
    private LinearLayout bottomTool;
    private RecyclerView rv;
    private RelativeLayout musicPlayBar;

    public RPMusicFragment(RecentlyPlayedActivity activity){
        this.activity=activity;
        setTitle("歌曲");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView=LayoutInflater.from(activity).inflate(R.layout.fragment_rp_music,container,false);
            init(rootView);
        }
        return rootView;
    }

    private void init(View view) {
        musicPlayBar = activity.findViewById(R.id.tool_current_play_layout);

        rv=view.findViewById(R.id.recycler_view);
        view.findViewById(R.id.multiple_choice).setOnClickListener(this);
        playAll=view.findViewById(R.id.play_all);
        bottomTool=view.findViewById(R.id.bottom_toolbar);

        rv.setAdapter(normalAdapter=new RPMusicNormalAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));
        choiceAdapter=new RPMusicChoiceAdapter(activity);

        view.findViewById(R.id.play_all).setOnClickListener(this);
        view.findViewById(R.id.bottom_tool_next_play).setOnClickListener(this);
        view.findViewById(R.id.bottom_tool_add_to_playlist).setOnClickListener(this);
        view.findViewById(R.id.bottom_tool_download).setOnClickListener(this);
        view.findViewById(R.id.bottom_tool_delete).setOnClickListener(this);

        normalAdapter.initContent();
    }

    public RPMusicNormalAdapter getNormalAdapter() {
        return normalAdapter;
    }

    public RPMusicChoiceAdapter getChoiceAdapter() {
        return choiceAdapter;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.multiple_choice:
                activity.switchMode(RecentlyPlayedActivity.MODE_CHOICE);
                break;

            case R.id.play_all:
                activity.getBinder().playMusics(normalAdapter.getDataList());
                break;

            case R.id.bottom_tool_next_play:
                List<Music> data = choiceAdapter.getSelected();
                MusicPlayService.MusicPlayBinder binder = activity.getBinder();
                for(Music item:data){
                    binder.nextPlay(item);
                }
                activity.switchMode(RecentlyPlayedActivity.MODE_NORMAL);
                break;

            case R.id.bottom_tool_add_to_playlist:
                data = choiceAdapter.getSelected();
                int[] musicIds=new int[data.size()];
                for(int i=0;i<data.size();i++){
                    musicIds[i]=data.get(i).getId();
                }
                new PlayListDialog(activity,activity.getUserId(),musicIds);
                activity.switchMode(RecentlyPlayedActivity.MODE_NORMAL);
                break;

            case R.id.bottom_tool_download:
                data = choiceAdapter.getSelected();
                for(Music item:data){
                    activity.getDownloadBinder().addTask(item);
                }
                activity.switchMode(RecentlyPlayedActivity.MODE_NORMAL);
                break;

            case R.id.bottom_tool_delete:
                data = choiceAdapter.getSelected();
                List<Integer> jsonData = new ArrayList<>();
                jsonData.add(activity.getUserId());
                for(Music item:data){
                    jsonData.add(item.getId());
                }
                new S2SHttpUtil(activity,
                        new Gson().toJson(jsonData),
                        MyEnvironment.serverBasePath+"music/deleteRPMusic",
                        new MyHandler(this)).call(BasicCode.DELETE_RP_MUSIC_END);
                break;
        }
    }

    public void switchMode(int mode){
        switch (mode){
            case RecentlyPlayedActivity.MODE_NORMAL:
                playAll.setVisibility(View.VISIBLE);
                bottomTool.setVisibility(View.GONE);
                rv.setAdapter(normalAdapter);
                int status = activity.getBinder().getStatus();
                if(status == MusicPlayService.IS_START||status == MusicPlayService.IS_PAUSE){
                    musicPlayBar.setVisibility(View.VISIBLE);
                }
                normalAdapter.initContent();
                break;
            case RecentlyPlayedActivity.MODE_CHOICE:
                playAll.setVisibility(View.GONE);
                bottomTool.setVisibility(View.VISIBLE);
                rv.setAdapter(choiceAdapter);
                choiceAdapter.clearSelected();
                musicPlayBar.setVisibility(View.GONE);
                choiceAdapter.initContent();
                break;
        }
    }

    static class MyHandler extends Handler{
        RPMusicFragment fragment;
        MyHandler(RPMusicFragment fragment){
            this.fragment=fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.DELETE_RP_MUSIC_END:
                    fragment.activity.switchMode(RecentlyPlayedActivity.MODE_NORMAL);
                    break;
            }
        }
    }
}
