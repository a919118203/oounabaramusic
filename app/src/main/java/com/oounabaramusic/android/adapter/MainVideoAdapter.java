package com.oounabaramusic.android.adapter;

import android.app.Activity;
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
import com.oounabaramusic.android.PostActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.UserInfoActivity;
import com.oounabaramusic.android.bean.Post;
import com.oounabaramusic.android.bean.Video;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;
import com.oounabaramusic.android.widget.customview.MyImageView;
import com.oounabaramusic.android.widget.customview.MyVideoPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainVideoAdapter extends RecyclerView.Adapter<MainVideoAdapter.ViewHolder> {

    public static final String CHANGE_GOOD="a";
    private BaseActivity activity;
    private List<Post> dataList;

    private boolean end;
    private SwipeRefreshLayout srl;

    private Bitmap good,noGood;

    //动作作用的位置
    private int selectPosition;

    public MainVideoAdapter(BaseActivity activity,SwipeRefreshLayout srl){
        this.activity=activity;
        this.srl=srl;
        dataList=new ArrayList<>();

        good = BitmapFactory.decodeResource(activity.getResources(),R.mipmap.good);
        noGood = BitmapFactory.decodeResource(activity.getResources(),R.mipmap.no_good);
    }

    public void setDataList(List<Post> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void addDataList(List<Post> dataList){
        if(dataList==null||dataList.isEmpty()){
            return;
        }
        int start = this.dataList.size();
        int len = dataList.size();
        this.dataList.addAll(dataList);
        notifyItemRangeInserted(start,len);
    }

    public void getContent(){
        srl.setRefreshing(true);

        Post post = new Post();
        post.setStart(0);
        post.setMainUserId(SharedPreferencesUtil.getUserId(activity.sp));

        new S2SHttpUtil(
                activity,
                new Gson().toJson(post),
                MyEnvironment.serverBasePath+"getVideoPost",
                new MyHandler(this))
        .call(BasicCode.GET_CONTENT);
    }

    public void getNextContent(){
        if(srl.isRefreshing()||end){
            return;
        }

        srl.setRefreshing(true);

        Post post = new Post();
        post.setStart(dataList.size());
        post.setMainUserId(SharedPreferencesUtil.getUserId(activity.sp));

        new S2SHttpUtil(
                activity,
                new Gson().toJson(post),
                MyEnvironment.serverBasePath+"getVideoPost",
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT_2);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_main_video,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post item  = dataList.get(position);
        Video video = item.getVideo();

        holder.title.setText(video.getTitle());
        holder.videoPlayer.setFilePath(video.getFilePath());
        holder.header.setImageUrl(MyEnvironment.serverBasePath+
                "loadUserHeader?userId="+item.getUserId());
        holder.userName.setText(item.getUserName());
        if(item.getGooded()>0){
            holder.toGood.setImageBitmap(good);
        }else{
            holder.toGood.setImageBitmap(noGood);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()){
            onBindViewHolder(holder,position);
        }else{

            String option = (String) payloads.get(0);

            switch (option){
                case CHANGE_GOOD:
                    Post item = dataList.get(position);
                    if(item.getGooded()>0){
                        holder.toGood.setImageBitmap(good);
                    }else{
                        holder.toGood.setImageBitmap(noGood);
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

        MyVideoPlayer videoPlayer;
        TextView title;
        MyCircleImageView header;
        TextView userName;
        ImageView toGood,toComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            videoPlayer=itemView.findViewById(R.id.video);
            title=itemView.findViewById(R.id.video_name);
            header=itemView.findViewById(R.id.user_cover);
            userName=itemView.findViewById(R.id.user_name);
            toGood=itemView.findViewById(R.id.good);
            toComment=itemView.findViewById(R.id.comment);

            int width = DensityUtil.getDisplayWidth(activity)-DensityUtil.dip2px(activity,40);
            videoPlayer.getLayoutParams().height= (int)(9f/16f*(double)width);
            videoPlayer.getLayoutParams().width=width;
            videoPlayer.requestLayout();

            toGood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPosition=getAdapterPosition();

                    Map<String,Integer> data = new HashMap<>();
                    data.put("userId",SharedPreferencesUtil.getUserId(activity.sp));
                    data.put("postId",dataList.get(selectPosition).getId());

                    new S2SHttpUtil(
                            activity,
                            new Gson().toJson(data),
                            MyEnvironment.serverBasePath+"postToGood",
                            new MyHandler(MainVideoAdapter.this))
                            .call(BasicCode.TO_GOOD_END);
                }
            });

            toComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostActivity.startActivity(activity,
                            dataList.get(getAdapterPosition()).getId());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostActivity.startActivity(activity,
                            dataList.get(getAdapterPosition()).getId());
                }
            });

            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserInfoActivity.startActivity(activity,
                            dataList.get(getAdapterPosition()).getUserId());
                }
            });
        }
    }

    static class MyHandler extends Handler{
        MainVideoAdapter adapter;
        MyHandler(MainVideoAdapter adapter){
            this.adapter=adapter;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    List<Post> dataList = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<Post>>(){}.getType());
                    adapter.end=false;
                    adapter.setDataList(dataList);
                    break;

                case BasicCode.GET_CONTENT_2:
                    dataList = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<Post>>(){}.getType());
                    if(dataList.isEmpty()){
                        adapter.end=true;
                    }
                    adapter.addDataList(dataList);
                    break;

                case BasicCode.TO_GOOD_END:
                    Map<String,Integer> result = new Gson().fromJson((String) msg.obj,
                            new TypeToken<Map<String,Integer>>(){}.getType());
                    int gooded = result.get("gooded");
                    adapter.dataList.get(adapter.selectPosition).setGooded(gooded);
                    adapter.notifyItemChanged(adapter.selectPosition,CHANGE_GOOD);
                    break;
            }
            adapter.srl.setRefreshing(false);
        }
    }
}
