package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Music;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchMusicAdapter extends RecyclerView.Adapter<SearchMusicAdapter.ViewHolder> {

    private BaseActivity activity;
    private List<Music> dataList;

    public SearchMusicAdapter(BaseActivity activity){
        this.activity=activity;
        dataList=new ArrayList<>();
    }

    public void setDataList(List<Music> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_search_music,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.musicName.setText(dataList.get(position).getMusicName());
        holder.singerName.setText(dataList.get(position).getSingerName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView musicName,singerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            musicName=itemView.findViewById(R.id.music_name);
            singerName=itemView.findViewById(R.id.music_singer);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.getBinder().playMusic(dataList.get(getAdapterPosition()));
                }
            });
        }
    }
}
