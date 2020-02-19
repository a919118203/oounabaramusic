package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.ResumePlayListActivity;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MusicDeletedPlayListAdapter extends RecyclerView.Adapter<MusicDeletedPlayListAdapter.ViewHolder> {

    private ResumePlayListActivity activity;
    private List<PlayList> dataList;

    public MusicDeletedPlayListAdapter(ResumePlayListActivity activity){
        this.activity=activity;
        dataList=new ArrayList<>();
    }

    public void setDataList(List<PlayList> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_music_deleted_playlist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayList item=dataList.get(position);

        holder.cover.setImage(new MyImage(MyImage.TYPE_PLAY_LIST_COVER,item.getId()));
        holder.name.setText(item.getPlayListName());
        holder.cnt.setText(String.valueOf(item.getCnt()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyImageView cover;
        TextView name,cnt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.setPlayList(dataList.get(getAdapterPosition()));
                    activity.switchMode(activity.MODE_RESUME_MUSIC);
                }
            });

            cover=itemView.findViewById(R.id.cover);
            name=itemView.findViewById(R.id.name);
            cnt=itemView.findViewById(R.id.cnt);
        }
    }
}
