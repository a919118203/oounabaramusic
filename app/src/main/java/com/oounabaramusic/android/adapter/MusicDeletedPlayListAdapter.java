package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.ResumePlayListActivity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MusicDeletedPlayListAdapter extends RecyclerView.Adapter<MusicDeletedPlayListAdapter.ViewHolder> {

    private ResumePlayListActivity activity;

    public MusicDeletedPlayListAdapter(ResumePlayListActivity activity){
        this.activity=activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_music_deleted_playlist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.switchMode(activity.MODE_RESUME_MUSIC);
                }
            });
        }
    }
}
