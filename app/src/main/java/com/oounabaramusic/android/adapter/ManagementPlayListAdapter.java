package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.oounabaramusic.android.PlayListManagementActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ManagementPlayListAdapter extends RecyclerView.Adapter<ManagementPlayListAdapter.ViewHolder> {

    private PlayListManagementActivity activity;
    private List<PlayList> dataList;
    private boolean[] selected;
    private int selectCnt;

    public ManagementPlayListAdapter(PlayListManagementActivity activity){
        this.activity=activity;
        this.dataList=new ArrayList<>();
        selected=new boolean[dataList.size()];
        selectCnt=0;
    }

    public void setDataList(List<PlayList> dataList) {
        this.dataList = dataList;
        selected=new boolean[dataList.size()];
        selectCnt=0;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_management_play_list,parent,false);
        return new ViewHolder(view);
    }

    public List<Integer> getSelected(){
        List<Integer> ids=new ArrayList<>();
        for(int i=0;i<dataList.size();i++){
            if(selected[i]){
                ids.add(dataList.get(i).getId());
            }
        }
        return ids;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayList item=dataList.get(position);

        holder.cb.setChecked(selected[position]);
        holder.playListCover.setImage(new MyImage(MyImage.TYPE_PLAY_LIST_COVER,item.getId()));
        holder.playListName.setText(item.getPlayListName());
        holder.musicCnt.setText(String.valueOf(item.getCnt()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void setSelectedCnt(){
        activity.setTitle("已选择"+selectCnt+"项");
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CheckBox cb;
        MyImageView playListCover;
        TextView playListName,musicCnt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cb=itemView.findViewById(R.id.selected);
            playListCover=itemView.findViewById(R.id.playlist_cover);
            playListName=itemView.findViewById(R.id.playlist_name);
            musicCnt=itemView.findViewById(R.id.music_cnt);

            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    selected[getAdapterPosition()]=isChecked;
                    if(isChecked){
                        selectCnt++;
                    }else{
                        selectCnt--;
                    }
                    setSelectedCnt();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected[getAdapterPosition()]=!cb.isChecked();
                    cb.setChecked(!cb.isChecked());
                }
            });
        }
    }
}
