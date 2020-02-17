package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.PostActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.UserInfoActivity;
import com.oounabaramusic.android.bean.Post;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class GoodAdapter extends RecyclerView.Adapter<GoodAdapter.ViewHolder> {

    private PostActivity activity;
    private List<Post> dataList;

    private SwipeRefreshLayout srl;
    private boolean end;

    public GoodAdapter(PostActivity activity,SwipeRefreshLayout srl){
        this.activity=activity;
        this.srl=srl;
        dataList=new ArrayList<>();
    }

    private void setDataList(List<Post> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    private void addDataList(List<Post> dataList){
        if(dataList==null||dataList.isEmpty()){
            return;
        }

        int start = this.dataList.size();
        int len = dataList.size();
        this.dataList.addAll(dataList);
        notifyItemRangeInserted(start,len);
    }

    public void initContent(){

        srl.setRefreshing(true);

        Post post = new Post();
        post.setId(activity.getPostId());
        post.setStart(0);

        new S2SHttpUtil(
                activity,
                new Gson().toJson(post),
                MyEnvironment.serverBasePath+"getPostGood",
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT);
    }

    public void getNextContent(){
        if(srl.isRefreshing()||end){
            return;
        }
        srl.setRefreshing(true);

        Post post = new Post();
        post.setId(activity.getPostId());
        post.setStart(dataList.size());

        new S2SHttpUtil(
                activity,
                new Gson().toJson(post),
                MyEnvironment.serverBasePath+"getPostGood",
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT_2);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_good,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post item = dataList.get(position);

        holder.header.setImageUrl(MyEnvironment.serverBasePath+
                "loadUserHeader?userId="+item.getUserId());
        holder.name.setText(item.getUserName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyCircleImageView header;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            header=itemView.findViewById(R.id.user_header);
            name=itemView.findViewById(R.id.user_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserInfoActivity.startActivity(activity,
                            dataList.get(getAdapterPosition()).getUserId());
                }
            });
        }
    }

    static class MyHandler extends Handler {

        GoodAdapter adapter;

        MyHandler(GoodAdapter adapter){
            this.adapter=adapter;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    List<Post> dataList= new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<Post>>(){}.getType());
                    adapter.setDataList(dataList);
                    adapter.end=false;
                    break;

                case BasicCode.GET_CONTENT_2:
                    dataList= new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<Post>>(){}.getType());
                    if(dataList.isEmpty()){
                        adapter.end=true;
                    }
                    adapter.addDataList(dataList);
                    break;
            }
            if(adapter.srl!=null){
                adapter.srl.setRefreshing(false);
            }
        }
    }
}
