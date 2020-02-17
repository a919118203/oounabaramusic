package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.UserInfoActivity;
import com.oounabaramusic.android.bean.User;
import com.oounabaramusic.android.fragment.FollowedFragment;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FollowedAdapter extends RecyclerView.Adapter<FollowedAdapter.ViewHolder> {

    private static final String CHANGE_FOLLOW="a";
    private BaseActivity activity;
    private FollowedFragment fragment;
    private List<User> dataList;

    private Bitmap m,f;

    public FollowedAdapter(BaseActivity activity,FollowedFragment fragment){
        this.activity=activity;
        this.fragment=fragment;
        dataList=new ArrayList<>();
        m= BitmapFactory.decodeResource(activity.getResources(),R.mipmap.sex_m);
        f= BitmapFactory.decodeResource(activity.getResources(),R.mipmap.sex_f);
    }

    public void setDataList(List<User> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_follow,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User item = dataList.get(position);

        holder.name.setText(item.getUserName());
        holder.introduction.setText(item.getIntroduction());
        if(item.getSex().equals("男")){
            holder.sex.setImageBitmap(m);
        }else{
            holder.sex.setImageBitmap(f);
        }
        holder.header.setImageUrl(MyEnvironment.serverBasePath+
                "loadUserHeader?userId="+item.getId());
        if(item.getFollowed()){
            holder.cancelFollow.setVisibility(View.VISIBLE);
            holder.toFollow.setVisibility(View.GONE);
        }else{
            holder.cancelFollow.setVisibility(View.GONE);
            holder.toFollow.setVisibility(View.VISIBLE);
        }

        //不能自己关注自己
        if(item.getId()== SharedPreferencesUtil.getUserId(activity.sp)){
            holder.cancelFollow.setVisibility(View.GONE);
            holder.toFollow.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()){
            onBindViewHolder(holder, position);
        }else{
            String option = (String) payloads.get(0);
            User item = dataList.get(position);

            switch (option){
                case CHANGE_FOLLOW:
                    if(item.getFollowed()){
                        holder.cancelFollow.setVisibility(View.VISIBLE);
                        holder.toFollow.setVisibility(View.GONE);
                    }else{
                        holder.cancelFollow.setVisibility(View.GONE);
                        holder.toFollow.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyCircleImageView header;
        TextView name,introduction;
        ImageView sex;
        TextView toFollow,cancelFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity, UserInfoActivity.class);
                    intent.putExtra("userId",
                            dataList.get(getAdapterPosition()).getId());
                    activity.startActivity(intent);
                }
            });

            header=itemView.findViewById(R.id.user_header);
            name=itemView.findViewById(R.id.friend_name);
            introduction=itemView.findViewById(R.id.friend_introduction);
            sex=itemView.findViewById(R.id.friend_sex);
            toFollow=itemView.findViewById(R.id.friend_follow);
            cancelFollow=itemView.findViewById(R.id.cancel_follow);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String,Integer> data = new HashMap<>();
                    data.put("from",SharedPreferencesUtil.getUserId(activity.sp));
                    data.put("to",dataList.get(getAdapterPosition()).getId());

                    new S2SHttpUtil(
                            activity,
                            new Gson().toJson(data),
                            MyEnvironment.serverBasePath+"toFollowUser",
                            new MyHandler(FollowedAdapter.this))
                    .call(getAdapterPosition());
                }
            };

            toFollow.setOnClickListener(listener);
            cancelFollow.setOnClickListener(listener);
        }
    }

    static class MyHandler extends Handler{
        FollowedAdapter adapter;
        MyHandler(FollowedAdapter adapter){
            this.adapter=adapter;
        }

        @Override
        public void handleMessage(Message msg) {
            int index = msg.what;
            Map<String,Integer> data = new Gson().fromJson((String)msg.obj,
                    new TypeToken<Map<String,Integer>>(){}.getType());
            int followed = data.get("followed");
            adapter.dataList.get(index).setFollowed(followed==1);
            adapter.notifyItemChanged(index,FollowedAdapter.CHANGE_FOLLOW);
        }
    }
}
