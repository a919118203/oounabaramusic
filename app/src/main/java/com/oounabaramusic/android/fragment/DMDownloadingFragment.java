package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.DownloadManagementActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.DMDownloadingAdapter;
import com.oounabaramusic.android.bean.Music;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DMDownloadingFragment extends BaseFragment {

    private DownloadManagementActivity activity;
    private View rootView;
    private DMDownloadingAdapter adapter;

    public DMDownloadingFragment(DownloadManagementActivity activity){
        this.activity=activity;
        setTitle("下载中");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(R.layout.fragment_downloading,container,false);
            init(rootView);
        }
        return rootView;
    }

    private void init(View view) {
        RecyclerView rv=view.findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new DMDownloadingAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));

        view.findViewById(R.id.start_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Music> data=activity.getDownloadBinder().getDownloadList();
                for(Music item:data){
                    activity.getDownloadBinder().addTask(item);
                }
            }
        });

        view.findViewById(R.id.delete_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAllDialog();
            }
        });
    }


    private void showDeleteAllDialog(){
        AlertDialog dialog=new AlertDialog.Builder(activity)
                .setTitle("确定要清空所有下载任务吗")
                .setNegativeButton("不",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.getDownloadBinder().deleteAllTask();
                    }
                })
                .create();

        dialog.getWindow().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.layout_card_2));
        dialog.show();
    }
}
