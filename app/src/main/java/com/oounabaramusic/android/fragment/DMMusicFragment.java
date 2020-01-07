package com.oounabaramusic.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.oounabaramusic.android.DownloadManagementActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.DMMusicChoiceAdapter;
import com.oounabaramusic.android.adapter.DMMusicNormalAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DMMusicFragment extends BaseFragment implements View.OnClickListener{

    private DownloadManagementActivity activity;
    private DMMusicNormalAdapter normalAdapter;
    private DMMusicChoiceAdapter choiceAdapter;
    private LinearLayout playAll;
    private LinearLayout bottomTool;
    private RecyclerView rv;

    public DMMusicFragment(DownloadManagementActivity activity){
        this.activity=activity;
        setTitle("单曲");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_dm_music,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        rv=view.findViewById(R.id.recycler_view);
        view.findViewById(R.id.multiple_choice).setOnClickListener(this);
        playAll=view.findViewById(R.id.play_all);
        bottomTool=view.findViewById(R.id.bottom_toolbar);

        rv.setAdapter(normalAdapter=new DMMusicNormalAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));
        choiceAdapter=new DMMusicChoiceAdapter(activity);

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
