package com.oounabaramusic.android.adapter;


import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oounabaramusic.android.MessageActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.UserInfoActivity;
import com.oounabaramusic.android.bean.Message;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{

    private List<Message> dataList;
    private int userId;      //对方ID
    private Activity activity;

    public MsgAdapter(Activity activity,int userId){
        this.activity=activity;
        this.userId=userId;
        dataList =new ArrayList<>();
    }

    public void addDataList(List<Message> dataList) {
        int len=dataList.size();
        for(Message msg : dataList){
            this.dataList.add(0,msg);
        }
        notifyItemRangeInserted(0,len);
    }

    public void addMessage(Message msg){
        dataList.add(msg);
        notifyItemInserted(dataList.size()-1);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_item_msg,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        Message item= dataList.get(position);

        if(item.getFromId()!=userId){
            holder.leftLayout.setVisibility(View.GONE);

            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.rightMsg.setText(item.getContent());
            holder.rightHeader.setImageUrl(MyEnvironment.serverBasePath+"loadUserHeader?userId="+item.getFromId());

        }else{
            holder.rightLayout.setVisibility(View.GONE);

            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.leftMsg.setText(item.getContent());
            holder.leftHeader.setImageUrl(MyEnvironment.serverBasePath+"loadUserHeader?userId="+item.getFromId());
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout,rightLayout;
        TextView leftMsg,rightMsg;
        MyCircleImageView leftHeader,rightHeader;
        public ViewHolder(View itemView) {
            super(itemView);
            leftLayout=itemView.findViewById(R.id.left_layout);
            rightLayout=itemView.findViewById(R.id.right_layout);
            leftMsg=itemView.findViewById(R.id.left_msg);
            rightMsg=itemView.findViewById(R.id.right_msg);
            leftHeader=itemView.findViewById(R.id.left_header);
            rightHeader=itemView.findViewById(R.id.right_header);

            rightHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, UserInfoActivity.class);
                    intent.putExtra("userId",dataList.get(getAdapterPosition()).getFromId());
                    activity.startActivity(intent);
                }
            });

            leftHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, UserInfoActivity.class);
                    intent.putExtra("userId",dataList.get(getAdapterPosition()).getFromId());
                    activity.startActivity(intent);
                }
            });
        }
    }
}
