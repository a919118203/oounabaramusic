package com.oounabaramusic.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.RecentlyPlayedActivity;
import com.oounabaramusic.android.adapter.RPMusicChoiceAdapter;
import com.oounabaramusic.android.adapter.RPMusicNormalAdapter;

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
        rv=view.findViewById(R.id.recycler_view);
        view.findViewById(R.id.multiple_choice).setOnClickListener(this);
        playAll=view.findViewById(R.id.play_all);
        bottomTool=view.findViewById(R.id.bottom_toolbar);

        rv.setAdapter(normalAdapter=new RPMusicNormalAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));
        choiceAdapter=new RPMusicChoiceAdapter(activity);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.multiple_choice:
                activity.switchMode(RecentlyPlayedActivity.MODE_CHOICE);
                break;
        }
    }

    public void switchMode(int mode){
        switch (mode){
            case RecentlyPlayedActivity.MODE_NORMAL:
                playAll.setVisibility(View.VISIBLE);
                bottomTool.setVisibility(View.GONE);
                rv.setAdapter(normalAdapter);
                break;
            case RecentlyPlayedActivity.MODE_CHOICE:
                playAll.setVisibility(View.GONE);
                bottomTool.setVisibility(View.VISIBLE);
                rv.setAdapter(choiceAdapter);
                choiceAdapter.clearSelected();
                break;
        }
    }
}
