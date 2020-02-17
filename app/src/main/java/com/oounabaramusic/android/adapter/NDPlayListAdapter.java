package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oounabaramusic.android.PlayListActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NDPlayListAdapter extends RecyclerView.Adapter<NDPlayListAdapter.ViewHolder> {

    private Activity activity;
    private List<PlayList> dataList;

    public NDPlayListAdapter(Activity activity){
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
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_nd_play_list,parent,false);
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
        }else if(position==9){
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

        PlayList item = dataList.get(position);

        holder.cover.setImageUrl(MyEnvironment.serverBasePath+
                "loadPlayListCover?playListId="+item.getId());
        holder.name.setText(item.getPlayListName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CardView itemView;
        MyImageView cover;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView= (CardView) itemView;

            cover=itemView.findViewById(R.id.cover);
            name=itemView.findViewById(R.id.name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity, PlayListActivity.class);
                    intent.putExtra("playList",dataList.get(getAdapterPosition()));
                    activity.startActivity(intent);
                }
            });
        }
    }
}
