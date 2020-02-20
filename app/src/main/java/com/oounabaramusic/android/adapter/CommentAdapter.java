package com.oounabaramusic.android.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oounabaramusic.android.AllReplyActivity;
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.CommentActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.UserInfoActivity;
import com.oounabaramusic.android.bean.Comment;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.InputMethodUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
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
    private BaseActivity activity;
    private List<Comment> dataList;
    private int selectPosition;
    private Bitmap good,noGood;
    private EditText et;

    public CommentAdapter(BaseActivity activity,EditText et){
        this.et=et;
        this.activity=activity;
        dataList=new ArrayList<>();
        selectPosition=-1;
        good = BitmapFactory.decodeResource(activity.getResources(),R.mipmap.good);
        noGood = BitmapFactory.decodeResource(activity.getResources(),R.mipmap.no_good);
    }

    public void setDataList(List<Comment> dataList) {
        this.dataList = dataList;
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
        holder.commentDate.setText(FormatUtil.DateTimeToString(item.getDate()));
        holder.userName.setText(item.getUserName());
        holder.userHeader.setImage(new MyImage(MyImage.TYPE_USER_HEADER, item.getUserId()));
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
                    replyTo(dataList.get(getAdapterPosition()));
                }
            });

            good.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPosition=getAdapterPosition();
                    Map<String,Integer> data=new HashMap<>();
                    data.put("userId",SharedPreferencesUtil.getUserId(activity.sp));
                    data.put("commentId",dataList.get(getAdapterPosition()).getId());
                    new S2SHttpUtil(activity,activity.gson.toJson(data),
                            MyEnvironment.serverBasePath+"goodToCommentOrReply",
                            new MyHandler(CommentAdapter.this)).call(BasicCode.TO_GOOD_END);
                }
            });

            moreReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPosition=getAdapterPosition();
                    AllReplyActivity.startActivity(activity,
                            dataList.get(getAdapterPosition()).getId(),0);
                }
            });

            userHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserInfoActivity.startActivity(activity,
                            dataList.get(getAdapterPosition()).getUserId());
                }
            });
        }
    }

    private void replyTo(Comment comment){
        et.setHint("回复 @"+comment.getUserName()+"：");
        et.requestFocus();
        InputMethodUtil.showSoftKeyboard(activity,et);
    }

    public void startAllReplyActivity(int position,int replyId){
        AllReplyActivity.startActivity(activity,
                dataList.get(position).getId(),replyId);
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public void cancelSelect(){
        selectPosition=-1;
    }

    static class MyHandler extends Handler{
        CommentAdapter adapter;
        MyHandler(CommentAdapter adapter){
            this.adapter=adapter;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.TO_GOOD_END:

                    String goodCnt= (String) msg.obj;
                    int selectPosition=adapter.getSelectPosition();

                    adapter.getDataList().get(
                            selectPosition).setGooded(Integer.valueOf(goodCnt));

                    if(goodCnt.equals("0")){
                        adapter.getDataList().get(selectPosition).setGoodCnt(
                                adapter.getDataList().get(selectPosition).getGoodCnt()-1
                        );
                    }else{
                        adapter.getDataList().get(selectPosition).setGoodCnt(
                                adapter.getDataList().get(selectPosition).getGoodCnt()+1
                        );
                    }

                    adapter.notifyItemChanged(selectPosition,
                            CommentAdapter.CHANGE_GOOD);
                    break;
            }
        }
    }
}
