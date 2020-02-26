package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SingerActivity;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.Singer;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SingerAdapter extends RecyclerView.Adapter<SingerAdapter.ViewHolder> {

    private static final String TO_FOLLOW="a";

    private Activity activity;
    private List<Singer> dataList;
    private SharedPreferences sp;
    private Handler handler;

    private int selectPosition;

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

    public void addDataList(List<Singer> dataList){
        if(dataList==null||dataList.isEmpty()){
            return;
        }

        int start = this.dataList.size();
        int len = dataList.size();
        this.dataList.addAll(dataList);
        notifyItemRangeInserted(start,len);
    }

    public void followSingerEnd(){
        dataList.get(selectPosition).setFollowed(true);
        notifyItemChanged(selectPosition,TO_FOLLOW);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_singer,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.singerCover.setImage(new MyImage(
                MyImage.TYPE_SINGER_COVER,
                dataList.get(position).getId()));
        holder.singerName.setText(dataList.get(position).getSingerName());

        boolean login=sp.getBoolean("login",false);;

        if(login){

            if(dataList.get(position).getFollowed()){
                holder.followed.setVisibility(View.VISIBLE);
                holder.toFollow.setVisibility(View.GONE);
                return ;
            }

            holder.followed.setVisibility(View.GONE);
            holder.toFollow.setVisibility(View.VISIBLE);
        }else{
            holder.followed.setVisibility(View.GONE);
            holder.toFollow.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()){
            onBindViewHolder(holder, position);
        }else{
            String option = (String) payloads.get(0);

            switch (option){
                case TO_FOLLOW:
                    holder.followed.setVisibility(View.VISIBLE);
                    holder.toFollow.setVisibility(View.GONE);

                    //姑且恢复
                    holder.toFollow.setEnabled(true);
                    break;
            }
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

                    SingerActivity.startActivity(activity,
                            dataList.get(getAdapterPosition()).getId());
                }
            });

            singerCover=itemView.findViewById(R.id.singer_cover);
            singerName=itemView.findViewById(R.id.singer_name);
            toFollow=itemView.findViewById(R.id.to_follow);
            followed=itemView.findViewById(R.id.followed);

            toFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toFollow.setEnabled(false);
                    selectPosition=getAdapterPosition();
                    Singer data = new Singer();
                    data.setId(dataList.get(getAdapterPosition()).getId());
                    data.setMainUserId(SharedPreferencesUtil.getUserId(sp));
                    new S2SHttpUtil(
                            activity,
                            new Gson().toJson(data),
                            MyEnvironment.serverBasePath+"toFollowSinger",
                            handler)
                    .call(BasicCode.TO_FOLLOW_SINGER);
                }
            });
        }
    }
}
