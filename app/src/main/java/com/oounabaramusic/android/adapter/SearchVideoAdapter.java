package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.media.MediaPlayer;
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
import com.oounabaramusic.android.PostActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SearchActivity;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.Post;
import com.oounabaramusic.android.bean.Video;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.VideoUtil;
import com.oounabaramusic.android.widget.customview.MyImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SearchVideoAdapter extends RecyclerView.Adapter<SearchVideoAdapter.ViewHolder> {

    private SearchActivity activity;
    private List<Video> dataList;

    private boolean end;
    private SwipeRefreshLayout srl;

    public SearchVideoAdapter(SearchActivity activity,SwipeRefreshLayout srl){
        this.activity=activity;
        this.srl=srl;
        dataList=new ArrayList<>();
    }

    private void setDataList(List<Video> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void addDataList(List<Video> dataList){
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

        Map<String,String> data = new HashMap<>();
        data.put("searchText",activity.getSearchText());
        data.put("start","0");

        new S2SHttpUtil(
                activity,
                new Gson().toJson(data),
                MyEnvironment.serverBasePath+"searchVideo",
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT);
    }

    public void getNextContent(){
        if(srl.isRefreshing()||end){
            return;
        }

        srl.setRefreshing(true);

        Map<String,String> data = new HashMap<>();
        data.put("searchText",activity.getSearchText());
        data.put("start",dataList.size()+"");

        new S2SHttpUtil(
                activity,
                new Gson().toJson(data),
                MyEnvironment.serverBasePath+"searchVideo",
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT_2);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_search_video,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video video = dataList.get(position);
        holder.duration.setText(FormatUtil.secondToString(video.getDuration()/1000));
        holder.userName.setText(video.getUserName());
        holder.videoName.setText(video.getTitle());
        holder.date.setText(FormatUtil.DateToString(video.getDate()));
        holder.cover.setImage(new MyImage(MyImage.TYPE_VIDEO_COVER,video.getId()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyImageView cover;
        TextView videoName,userName,duration,date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cover=itemView.findViewById(R.id.cover);
            videoName=itemView.findViewById(R.id.video_name);
            userName=itemView.findViewById(R.id.user_name);
            duration=itemView.findViewById(R.id.duration);
            date=itemView.findViewById(R.id.date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostActivity.startActivity(activity,
                            dataList.get(getAdapterPosition()).getPostId());
                }
            });
        }
    }

    static class MyHandler extends Handler {
        SearchVideoAdapter adapter;
        MyHandler(SearchVideoAdapter adapter){
            this.adapter=adapter;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    List<Video> dataList = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<Video>>(){}.getType());
                    adapter.end=false;
                    adapter.setDataList(dataList);
                    break;

                case BasicCode.GET_CONTENT_2:
                    dataList = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<Video>>(){}.getType());
                    if(dataList.isEmpty()){
                        adapter.end=true;
                    }
                    adapter.addDataList(dataList);
                    break;
            }
            adapter.srl.setRefreshing(false);
        }
    }
}
