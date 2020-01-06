package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oounabaramusic.android.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ResumeMusicAdapter extends RecyclerView.Adapter<ResumeMusicAdapter.ViewHolder> {

    private Activity activity;

    public ResumeMusicAdapter(Activity activity){
        this.activity=activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_resume_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position==0){
            holder.header.setVisibility(View.VISIBLE);
            holder.musicInfo.setVisibility(View.GONE);
            holder.musicCover.setVisibility(View.GONE);
        }else{
            holder.header.setVisibility(View.GONE);
            holder.musicInfo.setVisibility(View.VISIBLE);
            holder.musicCover.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView musicCover;
        LinearLayout musicInfo;
        TextView header;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            musicCover=itemView.findViewById(R.id.item_cover);
            musicInfo=itemView.findViewById(R.id.item_info);
            header=itemView.findViewById(R.id.header);
        }
    }
}
