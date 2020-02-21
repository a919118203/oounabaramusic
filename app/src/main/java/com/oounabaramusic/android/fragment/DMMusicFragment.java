package com.oounabaramusic.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.oounabaramusic.android.DownloadManagementActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.DMMusicChoiceAdapter;
import com.oounabaramusic.android.adapter.DMMusicNormalAdapter;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.widget.popupwindow.PlayListDialog;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class DMMusicFragment extends BaseFragment implements View.OnClickListener{

    private DownloadManagementActivity activity;
    private View rootView;
    private DMMusicNormalAdapter normalAdapter;
    private DMMusicChoiceAdapter choiceAdapter;
    private LinearLayout playAll;
    private LinearLayout bottomTool;
    private RecyclerView rv;
    private SwipeRefreshLayout srl;

    public DMMusicFragment(DownloadManagementActivity activity){
        this.activity=activity;
        setTitle("单曲");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_dm_music,container,false);
        init(rootView);
        return rootView;
    }

    public DMMusicNormalAdapter getNormalAdapter() {
        return normalAdapter;
    }

    public DMMusicChoiceAdapter getChoiceAdapter() {
        return choiceAdapter;
    }

    private void init(View view) {
        rv=view.findViewById(R.id.recycler_view);
        view.findViewById(R.id.multiple_choice).setOnClickListener(this);
        playAll=view.findViewById(R.id.play_all);
        bottomTool=view.findViewById(R.id.bottom_toolbar);

        rv.setAdapter(normalAdapter=new DMMusicNormalAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));
        choiceAdapter=new DMMusicChoiceAdapter(activity);

        playAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getBinder().playMusics(normalAdapter.getDataList());
            }
        });

        view.findViewById(R.id.bottom_tool_next_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Music> data=choiceAdapter.getSelectedData();
                for(Music item:data){
                    activity.getBinder().nextPlay(item);
                }

                activity.switchMode(DownloadManagementActivity.MODE_NORMAL);
            }
        });

        view.findViewById(R.id.bottom_tool_add_to_playlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.sp.getBoolean("login",false)){
                    String userId=activity.sp.getString("userId","-1");

                    List<Music> data=choiceAdapter.getSelectedData();
                    String[] md5s=new String[data.size()];
                    for(int i=0;i<data.size();i++){
                        md5s[i]=data.get(i).getMd5();
                    }

                    new PlayListDialog(activity,
                            Integer.valueOf(userId),
                            md5s);
                }else{
                    Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
                }

                activity.switchMode(DownloadManagementActivity.MODE_NORMAL);
            }
        });

        view.findViewById(R.id.bottom_tool_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceAdapter.showDeleteMusicDialog(choiceAdapter.getSelectedData());
                activity.switchMode(DownloadManagementActivity.MODE_NORMAL);
            }
        });

        srl=view.findViewById(R.id.swipe_refresh);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(rv.getAdapter() instanceof  DMMusicNormalAdapter){
                    normalAdapter.refresh();
                }else{
                    choiceAdapter.refresh();
                }
                srl.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.multiple_choice:
                activity.switchMode(DownloadManagementActivity.MODE_CHOICE);
                break;
        }
    }

    public void switchMode(int mode){
        switch (mode){
            case DownloadManagementActivity.MODE_NORMAL:
                playAll.setVisibility(View.VISIBLE);
                bottomTool.setVisibility(View.GONE);
                rv.setAdapter(normalAdapter);
                break;
            case DownloadManagementActivity.MODE_CHOICE:
                playAll.setVisibility(View.GONE);
                bottomTool.setVisibility(View.VISIBLE);
                rv.setAdapter(choiceAdapter);
                choiceAdapter.clearSelected();
                break;
        }
    }
}
