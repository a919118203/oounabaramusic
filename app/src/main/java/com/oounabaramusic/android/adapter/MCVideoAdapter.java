package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.PostActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Post;
import com.oounabaramusic.android.bean.Video;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.util.VideoUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MCVideoAdapter extends RecyclerView.Adapter<MCVideoAdapter.ViewHolder> {

    private BaseActivity activity;
    private List<Post> dataList;

    private SwipeRefreshLayout srl;
    private boolean end;

    private int selectPosition;

    public MCVideoAdapter(BaseActivity activity,SwipeRefreshLayout srl){
        this.activity=activity;
        this.srl=srl;
        dataList=new ArrayList<>();
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
        post.setMainUserId(SharedPreferencesUtil.getUserId(activity.sp));
        post.setStart(0);

        new S2SHttpUtil(
                activity,
                new Gson().toJson(post),
                MyEnvironment.serverBasePath+"getGoodedVideo",
                new MyHandler(this))
        .call(BasicCode.GET_CONTENT);
    }

    public void getNextContent(){
        if(srl.isRefreshing()||end){
            return;
        }

        srl.setRefreshing(true);

        Post post = new Post();
        post.setMainUserId(SharedPreferencesUtil.getUserId(activity.sp));
        post.setStart(dataList.size());

        new S2SHttpUtil(
                activity,
                new Gson().toJson(post),
                MyEnvironment.serverBasePath+"getGoodedVideo",
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT_2);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_mc_video,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post item = dataList.get(position);
        Video video = item.getVideo();

        holder.userName.setText(item.getUserName());
        holder.cover.setImageBitmap(
                VideoUtil.getVideoCover(video.getFilePath()));
        holder.len.setText(FormatUtil.secondToString(
                video.getDuration()/1000));
        holder.title.setText(video.getTitle());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView cover;
        TextView title;
        TextView len;
        TextView userName;
        ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cover=itemView.findViewById(R.id.cover);
            title=itemView.findViewById(R.id.video_name);
            len=itemView.findViewById(R.id.len);
            userName=itemView.findViewById(R.id.user_name);
            delete=itemView.findViewById(R.id.delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostActivity.startActivity(activity,dataList.get(getAdapterPosition()).getId());
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
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
                            new MyHandler(MCVideoAdapter.this))
                            .call(BasicCode.TO_GOOD_END);
                }
            });
        }
    }

    static class MyHandler extends Handler{
        MCVideoAdapter adapter;
        MyHandler(MCVideoAdapter adapter){
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
                    if(gooded==0){
                        Toast.makeText(adapter.activity, "成功删除", Toast.LENGTH_SHORT).show();
                        adapter.dataList.remove(adapter.selectPosition);
                        adapter.notifyItemRemoved(adapter.selectPosition);
                    }
                    break;
            }
            adapter.srl.setRefreshing(false);
        }
    }
}
