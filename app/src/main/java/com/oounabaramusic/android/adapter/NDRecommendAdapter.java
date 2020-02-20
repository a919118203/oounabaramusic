package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.widget.customview.MyImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NDRecommendAdapter extends RecyclerView.Adapter<NDRecommendAdapter.ViewHolder> {

    private BaseActivity activity;
    private List<Music> dataList;

    public NDRecommendAdapter(BaseActivity activity){
        this.activity=activity;
        dataList=new ArrayList<>();
    }

    public void setDataList(List<Music> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public List<Music> getDataList() {
        return dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_nd_recommend,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewGroup.MarginLayoutParams mlp= (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if(position==0){
            mlp.setMargins(
                    DensityUtil.dip2px(activity,20),
                    DensityUtil.dip2px(activity,10),
                    DensityUtil.dip2px(activity,10),
                    DensityUtil.dip2px(activity,10)
            );
        }else if(position==dataList.size()-1){
            mlp.setMargins(
                    DensityUtil.dip2px(activity,10),
                    DensityUtil.dip2px(activity,10),
                    DensityUtil.dip2px(activity,20),
                    DensityUtil.dip2px(activity,10)
            );
        }else{
            mlp.setMargins(
                    DensityUtil.dip2px(activity,10),
                    DensityUtil.dip2px(activity,10),
                    DensityUtil.dip2px(activity,10),
                    DensityUtil.dip2px(activity,10)
            );
        }
        holder.itemView.setLayoutParams(mlp);

        Music item = dataList.get(position);

        holder.singerName.setText(item.getSingerName().replace("/" ," "));
        holder.musicName.setText(item.getMusicName());
        holder.cover.setImage(new MyImage(
                MyImage.TYPE_SINGER_COVER,
                Integer.valueOf(item.getSingerId().split("/")[0])));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        View itemView;
        MyImageView cover;
        TextView musicName,singerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView=itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.getBinder().playMusic(dataList.get(getAdapterPosition()));
                }
            });

            cover = itemView.findViewById(R.id.cover);
            musicName = itemView.findViewById(R.id.music_name);
            singerName = itemView.findViewById(R.id.singer_name);
        }
    }
}
