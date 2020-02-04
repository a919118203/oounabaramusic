package com.oounabaramusic.android.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oounabaramusic.android.AllReplyActivity;
import com.oounabaramusic.android.CommentActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Comment;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    public static final String CHANGE_GOOD="a";
    private CommentActivity activity;
    private List<Comment> dataList;
    private int selectPosition;
    private Bitmap good,noGood;

    public CommentAdapter(CommentActivity activity){
        this.activity=activity;
        dataList=new ArrayList<>();

        good = BitmapFactory.decodeResource(activity.getResources(),R.mipmap.good);
        noGood = BitmapFactory.decodeResource(activity.getResources(),R.mipmap.no_good);
    }

    public void setDataList(List<Comment> dataList) {
        this.dataList = dataList;
        LogUtil.printLog("有多少条评论： "+dataList.size());
        notifyDataSetChanged();
    }

    public List<Comment> getDataList() {
        return dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_comment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment item=dataList.get(position);

        holder.goodCnt.setText(String.valueOf(item.getGoodCnt()));
        holder.commentContent.setText(item.getContent());
        holder.commentDate.setText(FormatUtil.DateToString(item.getDate()));
        holder.userName.setText(item.getUserName());
        holder.userHeader.setImageUrl(MyEnvironment.serverBasePath+
                "loadUserHeader?userId="+item.getUserId());
        if(item.getGooded()==0){
            holder.good.setImageBitmap(noGood);
        }else{
            holder.good.setImageBitmap(good);
        }

        if(item.getReplyCnt()!=0){
            holder.replyLayout.setVisibility(View.VISIBLE);
            holder.replyCnt.setText(String.valueOf(item.getReplyCnt()));
            holder.adapter.setDataList(item.getReplies(),item.getUserId(),position);
            if(item.getReplyCnt()>=3){
                holder.moreReply.setVisibility(View.VISIBLE);
            }else{
                holder.moreReply.setVisibility(View.GONE);
            }
        }else{
            holder.replyLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()){
            onBindViewHolder(holder,position);
        }else{
            String option= (String) payloads.get(0);

            switch (option){
                case CHANGE_GOOD:
                    if(dataList.get(position).getGooded()==0){
                        holder.good.setImageBitmap(noGood);
                    }else{
                        holder.good.setImageBitmap(good);
                    }
                    holder.goodCnt.setText(String.valueOf(dataList.get(position).getGoodCnt()));
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyCircleImageView userHeader;
        TextView userName,commentDate,commentContent;
        ImageView good;
        TextView goodCnt;
        TextView replyCnt;
        ReplyAdapter adapter;
        RecyclerView replyRv;
        CardView replyLayout;

        LinearLayout moreReply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userHeader=itemView.findViewById(R.id.user_header);
            userName=itemView.findViewById(R.id.user_name);
            commentDate=itemView.findViewById(R.id.comment_date);
            commentContent=itemView.findViewById(R.id.comment_content);
            good=itemView.findViewById(R.id.good);
            goodCnt=itemView.findViewById(R.id.good_cnt);
            replyRv=itemView.findViewById(R.id.reply_rv);
            replyRv.setAdapter(adapter=new ReplyAdapter(activity,CommentAdapter.this));
            replyRv.setLayoutManager(new LinearLayoutManager(activity));
            replyCnt=itemView.findViewById(R.id.reply_cnt);
            moreReply=itemView.findViewById(R.id.more_reply);
            replyLayout=itemView.findViewById(R.id.reply_layout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPosition=getAdapterPosition();
                    activity.replyTo(dataList.get(getAdapterPosition()));
                }
            });

            good.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPosition=getAdapterPosition();
                    Map<String,Integer> data=new HashMap<>();
                    data.put("userId",Integer.valueOf(activity.getUserId()));
                    data.put("commentId",dataList.get(getAdapterPosition()).getId());
                    new S2SHttpUtil(activity,activity.gson.toJson(data),
                            MyEnvironment.serverBasePath+"goodToCommentOrReply",
                            activity.getHandler()).call(BasicCode.TO_GOOD_END);
                }
            });

            moreReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPosition=getAdapterPosition();
                    Intent intent=new Intent(activity, AllReplyActivity.class);
                    intent.putExtra("comment",dataList.get(getAdapterPosition()));
                    activity.startActivity(intent);
                }
            });
        }
    }

    public void startAllReplyActivity(int position,int replyId){
        Intent intent=new Intent(activity, AllReplyActivity.class);
        intent.putExtra("comment",dataList.get(position));
        intent.putExtra("replyId",replyId);
        activity.startActivity(intent);
    }

    public int getSelectPosition() {
        return selectPosition;
    }
}
