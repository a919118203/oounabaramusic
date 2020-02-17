package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.MessageActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Message;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PrivateMessageAdapter extends RecyclerView.Adapter<PrivateMessageAdapter.ViewHolder> {

    private BaseActivity activity;
    private List<Message> dataList;
    private int selfId;

    public PrivateMessageAdapter(BaseActivity activity){
        this.activity=activity;
        dataList=new ArrayList<>();
        selfId= SharedPreferencesUtil.getUserId(activity.sp);
    }

    public void setDataList(List<Message> dataList) {
        this.dataList = dataList;
        Collections.sort(dataList, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o2.getSendDate().compareTo(o1.getSendDate());
            }
        });
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_private_message,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message item = dataList.get(position);

        if(item.getFromId()==selfId){
            holder.header.setImageUrl(MyEnvironment.serverBasePath+
                    "loadUserHeader?userId="+item.getToId());
            holder.name.setText(item.getToUserName());
        }else{
            holder.header.setImageUrl(MyEnvironment.serverBasePath+
                    "loadUserHeader?userId="+item.getFromId());
            holder.name.setText(item.getFromUserName());
        }

        holder.newMessage.setText(item.getContent());
        holder.messageDate.setText(FormatUtil.DateToString(item.getSendDate()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyCircleImageView header;
        TextView name,newMessage,messageDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            header=itemView.findViewById(R.id.user_header);
            name=itemView.findViewById(R.id.user_name);
            newMessage=itemView.findViewById(R.id.new_message);
            messageDate=itemView.findViewById(R.id.new_message_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message item = dataList.get(getAdapterPosition());
                    int userId;
                    String userName;
                    if(item.getToId()==selfId){
                        userId = item.getFromId();
                        userName = item.getFromUserName();
                    }else{
                        userId = item.getToId();
                        userName = item.getToUserName();
                    }
                    MessageActivity.startActivity(activity,userId,userName);
                }
            });
        }
    }
}
