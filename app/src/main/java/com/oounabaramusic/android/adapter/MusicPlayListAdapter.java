package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Music;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MusicPlayListAdapter extends RecyclerView.Adapter<MusicPlayListAdapter.ViewHolder> {

    private Activity activity;
    private List<Music> dataList;
    private int currentPlay;

    public MusicPlayListAdapter(Activity activity){
        this.activity=activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_music_play_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.singerName.setText(dataList.get(position).getSingerName());
        holder.musicName.setText(dataList.get(position).getMusicName());
        if(position==currentPlay){
            holder.isPlaying.setVisibility(View.VISIBLE);
        }else{
            holder.isPlaying.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setDataList(List<Music> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void setCurrentPlay(int currentPlay) {
        this.currentPlay = currentPlay;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView musicName;
        TextView singerName;
        ImageView isPlaying;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            musicName=itemView.findViewById(R.id.music_name);
            singerName=itemView.findViewById(R.id.singer_name);
            isPlaying=itemView.findViewById(R.id.is_playing);
        }
    }
}
