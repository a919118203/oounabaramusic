package com.oounabaramusic.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Reply;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    private CommentAdapter rootAdapter;
    private Activity activity;
    private List<Reply> dataList;
    private int position;
    private int commentUser;

    public ReplyAdapter(Activity activity,CommentAdapter rootAdapter){
        this.activity=activity;
        this.rootAdapter=rootAdapter;
        dataList=new ArrayList<>();
    }

    public void setDataList(List<Reply> dataList,int commentUser,int position) {
        this.dataList = dataList;
        this.position=position;
        this.commentUser=commentUser;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(new TextView(activity));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reply item=dataList.get(position);
        SpannableString ss;

        if(item.getReplyTo()==commentUser){
            String text=item.getUserName()+": "+item.getContent();

            ss=new SpannableString(text);
            ss.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    LogUtil.printLog("回复人的名字");
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setUnderlineText(false);
                    ds.setColor(activity.getResources().getColor(R.color.colorPrimary));
                }

            },0,item.getUserName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.itemView.setText(ss);
        }else{
            ss=new SpannableString(item.getUserName());
            ss.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    LogUtil.printLog("回复人的名字");
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setUnderlineText(false);
                    ds.setColor(activity.getResources().getColor(R.color.colorPrimary));
                }
            },0,ss.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.itemView.setText(ss);
            holder.itemView.append(" 回复 ");

            ss=new SpannableString(item.getReplyToUserName());
            ss.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    LogUtil.printLog("被回复人的名字");
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setUnderlineText(false);
                    ds.setColor(activity.getResources().getColor(R.color.colorPrimary));
                }
            },0,ss.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.itemView.append(ss);
            holder.itemView.append(": ");
            holder.itemView.append(item.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView= (TextView) itemView;
            this.itemView.setTextSize(TypedValue.COMPLEX_UNIT_PX,activity.
                    getResources().getDimension(R.dimen.font_medium_size));
            this.itemView.setMaxLines(2);
            this.itemView.setEllipsize(TextUtils.TruncateAt.END);
            this.itemView.setTextColor(activity.getResources().getColor(R.color.positive));
            this.itemView.setMovementMethod(LinkMovementMethod.getInstance());

            ViewGroup.MarginLayoutParams mlp=new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mlp.setMargins(
                    0,
                    DensityUtil.dip2px(activity,5),
                    0,
                    DensityUtil.dip2px(activity,5));
            this.itemView.setLayoutParams(mlp);
            this.itemView.setBackground(
                    activity.getResources().getDrawable(R.drawable.view_ripple_rectangle));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootAdapter.startAllReplyActivity(
                            position,dataList.get(getAdapterPosition()).getId());
                }
            });
        }
    }
}
