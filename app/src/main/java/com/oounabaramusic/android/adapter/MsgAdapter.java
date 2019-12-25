package com.oounabaramusic.android.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Message;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by HuangXiaoyang on 2019/11/02.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{

    private List<Message> messages;

    public MsgAdapter(){

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_msg,parent,false);
        final ViewHolder viewHolder=new ViewHolder(view);
        viewHolder.leftMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),messages.get(viewHolder.getLayoutPosition())
                        .getContent(), Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.rightMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),messages.get(viewHolder.getLayoutPosition())
                        .getContent(), Toast.LENGTH_SHORT).show();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message=messages.get(position);
        if(message.getType()==Message.TYPE_RECEIVED){
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(message.getContent());
        }else if(message.getType()==Message.TYPE_SENT){
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.rightMsg.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout,rightLayout;
        TextView leftMsg,rightMsg;
        public ViewHolder(View itemView) {
            super(itemView);
            leftLayout=itemView.findViewById(R.id.left_layout);
            rightLayout=itemView.findViewById(R.id.right_layout);
            leftMsg=itemView.findViewById(R.id.left_msg);
            rightMsg=itemView.findViewById(R.id.right_msg);
        }
    }
}
