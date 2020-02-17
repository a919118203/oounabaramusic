package com.oounabaramusic.android.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oounabaramusic.android.ChooseMusicActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseMusicAdapter extends RecyclerView.Adapter<ChooseMusicAdapter.ViewHolder> {

    private ChooseMusicActivity activity;
    private List<Music> dataList;

    public ChooseMusicAdapter(ChooseMusicActivity activit){
        this.activity=activit;
        dataList=new ArrayList<>();
    }

    public void setDataList(List<Music> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_item_choose_music,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Music item = dataList.get(position);
        holder.cover.setImageUrl(MyEnvironment.serverBasePath+
                "music/loadMusicCover?singerId="+item.getSingerId().split("/")[0]);
        holder.name.setText(item.getMusicName());
        holder.singer.setText(item.getSingerName().replace("/"," "));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyImageView cover;
        TextView name,singer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cover=itemView.findViewById(R.id.cover);
            name=itemView.findViewById(R.id.name);
            singer=itemView.findViewById(R.id.singer);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent data = new Intent();
                    data.putExtra("music",dataList.get(getAdapterPosition()));
                    activity.setResult(0,data);
                    activity.finish();
                }
            });
        }
    }
}
