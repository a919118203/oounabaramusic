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

public class ResumePlayListAdapter extends RecyclerView.Adapter<ResumePlayListAdapter.ViewHolder> {

    private Activity activity;

    public ResumePlayListAdapter(Activity activity){
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
            holder.playListInfo.setVisibility(View.GONE);
            holder.playListCover.setVisibility(View.GONE);
        }else{
            holder.header.setVisibility(View.GONE);
            holder.playListInfo.setVisibility(View.VISIBLE);
            holder.playListCover.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView playListCover;
        LinearLayout playListInfo;
        TextView header;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            playListCover=itemView.findViewById(R.id.item_cover);
            playListInfo=itemView.findViewById(R.id.item_info);
            header=itemView.findViewById(R.id.header);
        }
    }
}
