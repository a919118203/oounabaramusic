package com.oounabaramusic.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oounabaramusic.android.AllReplyActivity;
import com.oounabaramusic.android.CommentActivity;
import com.oounabaramusic.android.PostActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Comment;
import com.oounabaramusic.android.bean.Reply;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyMessageCommentAdapter extends RecyclerView.Adapter<MyMessageCommentAdapter.ViewHolder> {

    private Activity activity;
    private List<Reply> dataList;

    public MyMessageCommentAdapter(Activity activity){
        this.activity=activity;
        dataList=new ArrayList<>();
    }

    public void setDataList(List<Reply> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_my_message_comment,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reply item = dataList.get(position);

        holder.header.setImageUrl(MyEnvironment.serverBasePath+
                "loadUserHeader?userId="+item.getUserId());
        holder.replyContent.setText("回复我："+item.getContent());
        holder.name.setText(item.getUserName());
        holder.date.setText(FormatUtil.DateToString(item.getDate()));

        if(item.getReplyTo()==0){
            holder.myComment.setText("我的评论："+item.getComment().getContent());
        }else{
            holder.myComment.setText("我的评论："+item.getReply().getContent());
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyCircleImageView header;
        TextView name,replyContent,myComment,date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            header=itemView.findViewById(R.id.user_header);
            name=itemView.findViewById(R.id.user_name);
            replyContent=itemView.findViewById(R.id.comment_content);
            myComment=itemView.findViewById(R.id.my_comment);
            date=itemView.findViewById(R.id.comment_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Reply item = dataList.get(getAdapterPosition());
                    Comment c = item.getComment();
                    if(item.getReplyTo()==0){
                        int targetId = c.getTargetId();
                        int targetType = c.getTargetType();
                        if(targetType==Comment.POST){
                            PostActivity.startActivity(activity,targetId);
                        }else{
                            CommentActivity.startActivity(activity,targetId,targetType);
                        }
                    }else{
                        AllReplyActivity.startActivity(activity,c.getId(),item.getId());
                    }
                }
            });
        }
    }
}
