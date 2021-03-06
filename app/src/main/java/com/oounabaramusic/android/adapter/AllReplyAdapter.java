package com.oounabaramusic.android.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oounabaramusic.android.AllReplyActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.UserInfoActivity;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.Reply;
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
import androidx.recyclerview.widget.RecyclerView;

public class AllReplyAdapter extends RecyclerView.Adapter<AllReplyAdapter.ViewHolder> {

    public static final String CHANGE_GOOD="a";
    private AllReplyActivity activity;
    private List<Reply> dataList;
    private int mainUserId;
    private int selectPosition;
    private int[] heights;

    private Bitmap goodBitmap,noGoodBitmap;
    private int scrollTo ;
    private int scrollHeight;
    private MyHandler mHandler;

    public AllReplyAdapter(AllReplyActivity activity,int mainUserId,int scrollTo){
        this.activity=activity;
        this.scrollTo=scrollTo;
        this.dataList=new ArrayList<>();
        this.mainUserId=mainUserId;
        mHandler=new MyHandler(this);
        heights=new int[0];

        LogUtil.printLog("scrollTo:  "+scrollTo);

        goodBitmap= BitmapFactory.decodeResource(activity.getResources(),R.mipmap.good);
        noGoodBitmap = BitmapFactory.decodeResource(activity.getResources(),R.mipmap.no_good);
    }

    public void setDataList(List<Reply> dataList) {
        this.dataList = dataList;
        heights=new int[dataList.size()];
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_comment,
                (ViewGroup) activity.getWindow().getDecorView(),false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Reply item=dataList.get(position);

        holder.userHeader.setImage(new MyImage(MyImage.TYPE_USER_HEADER,item.getId()));
        holder.userName.setText(item.getUserName());
        holder.commentDate.setText(FormatUtil.DateTimeToString(item.getDate()));
        if(item.getGooded()==0){
            holder.good.setImageBitmap(noGoodBitmap);
        }else{
            holder.good.setImageBitmap(goodBitmap);
        }
        holder.goodCnt.setText(String.valueOf(item.getGoodCnt()));

        if(item.getReplyTo()==0){
            holder.commentContent.setText(item.getContent());
        }else{
            holder.commentContent.setText("回复 ");

            SpannableString ss=new SpannableString(item.getReplyToUserName());
            ss.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    UserInfoActivity.startActivity(activity,item.getReplayToUserId());
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setUnderlineText(false);
                    ds.setColor(activity.getResources().getColor(R.color.colorPrimary));
                }
            },0,ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.commentContent.append(ss);
            holder.commentContent.append("：");
            holder.commentContent.append(item.getContent());
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
                        holder.good.setImageBitmap(noGoodBitmap);
                    }else{
                        holder.good.setImageBitmap(goodBitmap);
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

        private MyCircleImageView userHeader;
        private TextView userName;
        private TextView commentDate;
        private TextView commentContent;
        private ImageView good;
        private TextView goodCnt;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            userHeader=itemView.findViewById(R.id.user_header);
            userName=itemView.findViewById(R.id.user_name);
            commentDate=itemView.findViewById(R.id.comment_date);
            commentContent=itemView.findViewById(R.id.comment_content);
            good=itemView.findViewById(R.id.good);
            goodCnt=itemView.findViewById(R.id.good_cnt);

            commentContent.setMovementMethod(LinkMovementMethod.getInstance());

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
                    data.put("replyId",dataList.get(getAdapterPosition()).getId());
                    new S2SHttpUtil(activity,activity.gson.toJson(data),
                            MyEnvironment.serverBasePath+"goodToCommentOrReply",
                            activity.getHandler()).call(BasicCode.TO_GOOD_END);
                }
            });

            itemView.post(new Runnable() {
                @Override
                public void run() {

                    if(scrollTo!=0){
                        if(dataList.get(getAdapterPosition()).getId()==scrollTo){
                            scrollTo=0;
                            activity.smoothScrollTo(scrollHeight);
                        }else{
                            scrollHeight+=itemView.getHeight();
                        }
                    }
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

    public int getSelectPosition() {
        return selectPosition;
    }

    public List<Reply> getDataList() {
        return dataList;
    }

    private static class MyHandler extends Handler{
        static final int START=0;
        static final int END=1;
        int cnt=0;
        int len=-1;
        int end;
        int index;
        int scroll;
        AllReplyAdapter adapter;
        MyHandler(AllReplyAdapter adapter){
            this.adapter=adapter;
        }

        @Override
        synchronized public void handleMessage(Message msg) {
            switch (msg.what){
                case START:
                    cnt++;
                    break;
                case END:
                    cnt--;
                    len=Math.max(len,msg.arg1);
                    if(cnt==0){

                        //检查前面的所有高度是不是已经有值
                        boolean start=true;
                        for(int i=0;i<len;i++){
                            if(adapter.heights[i]==0){
                                start=false;
                                break;
                            }
                        }

                        //全部有值时开始滚动
                        if(start){
                            do{
                                if(adapter.dataList.get(index).getId()==adapter.scrollTo){
                                    end=index;
                                }else{
                                    scroll+=adapter.heights[index];
                                    index++;
                                }
                            }while(index<end&&index<=len);
                        }

                        //滚动
                        adapter.activity.smoothScrollTo(scroll);
                    }
                    break;
            }
        }
    }
}
