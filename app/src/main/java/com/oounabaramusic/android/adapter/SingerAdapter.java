package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SingerActivity;
import com.oounabaramusic.android.bean.Singer;
import com.oounabaramusic.android.okhttputil.SingerClassificationHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SingerAdapter extends RecyclerView.Adapter<SingerAdapter.ViewHolder> {

    private Activity activity;
    private List<Singer> dataList;
    private List<Integer> followed;
    private SharedPreferences sp;
    private Handler handler;

    public SingerAdapter(Activity activity,Handler handler){
        this.activity=activity;
        this.handler=handler;
        dataList=new ArrayList<>();
        sp=PreferenceManager.getDefaultSharedPreferences(activity);
    }

    public void setDataList(List<Singer> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void setFollowed(List<Integer> followed) {
        this.followed = followed;
    }

    public void followSingerEnd(String singerId){
        followed.add(Integer.valueOf(singerId));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_singer,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.singerCover.setImageUrl(MyEnvironment.serverBasePath+
                "music/loadSingerCover?singerId="+dataList.get(position).getId());
        holder.singerName.setText(dataList.get(position).getSingerName());

        boolean login=sp.getBoolean("login",false);;

        if(login){
            for(int singerId:followed){
                if(dataList.get(position).getId()==singerId){
                    holder.followed.setVisibility(View.VISIBLE);
                    holder.toFollow.setVisibility(View.GONE);
                    return ;
                }
            }

            holder.followed.setVisibility(View.GONE);
            holder.toFollow.setVisibility(View.VISIBLE);
        }else{
            holder.followed.setVisibility(View.GONE);
            holder.toFollow.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private MyCircleImageView singerCover;
        private TextView singerName,toFollow,followed;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity, SingerActivity.class);
                    intent.putExtra("singer",dataList.get(getAdapterPosition()));
                    intent.putExtra("followed",
                            SingerAdapter.this.followed.contains(
                                    dataList.get(getAdapterPosition()).getId()));
                    activity.startActivity(intent);
                }
            });

            singerCover=itemView.findViewById(R.id.singer_cover);
            singerName=itemView.findViewById(R.id.singer_name);
            toFollow=itemView.findViewById(R.id.to_follow);
            followed=itemView.findViewById(R.id.followed);

            toFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SingerClassificationHttpUtil.toFollowSinger(activity,
                            sp.getString("userId","-1"),
                            dataList.get(getAdapterPosition()).getId(),
                            handler);
                }
            });
        }
    }
}
