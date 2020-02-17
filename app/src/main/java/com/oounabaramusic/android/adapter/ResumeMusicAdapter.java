package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ResumeMusicAdapter extends RecyclerView.Adapter<ResumeMusicAdapter.ViewHolder> {

    private Activity activity;
    private List<Music> dataList;
    private boolean[] selected;

    public ResumeMusicAdapter(Activity activity){
        this.activity=activity;
        dataList=new ArrayList<>();
        selected=new boolean[dataList.size()];
    }

    public void setDataList(List<Music> dataList) {
        this.dataList = dataList;
        selected=new boolean[dataList.size()];
        notifyDataSetChanged();
    }

    public List<Integer> getSelect(){
        List<Integer> result=new ArrayList<>();

        for(int i=0;i<dataList.size();i++){
            if(selected[i]){
                result.add(dataList.get(i).getId());
            }
        }

        return result;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_resume_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Music item=dataList.get(position);

        holder.cb.setChecked(selected[position]);
        holder.musicName.setText(item.getMusicName());
        holder.singerName.setText(item.getSingerName());
        holder.musicCover.setImageUrl(MyEnvironment.serverBasePath+
                "music/loadMusicCover?singerId="+item.getSingerId().split("/")[0]);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyImageView musicCover;
        TextView musicName,singerName;
        CheckBox cb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            musicCover=itemView.findViewById(R.id.item_cover);
            musicName=itemView.findViewById(R.id.item_name);
            singerName=itemView.findViewById(R.id.item_content);
            cb=itemView.findViewById(R.id.item_check_box);

            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    selected[getAdapterPosition()]=isChecked;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cb.setChecked(!cb.isChecked());
                }
            });
        }
    }
}
