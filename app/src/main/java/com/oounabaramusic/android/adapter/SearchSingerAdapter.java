package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SearchActivity;
import com.oounabaramusic.android.SingerActivity;
import com.oounabaramusic.android.bean.Singer;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SearchSingerAdapter extends RecyclerView.Adapter<SearchSingerAdapter.ViewHolder> {

    private SearchActivity activity;
    private List<Singer> dataList;

    private boolean end;
    private SwipeRefreshLayout srl;

    public SearchSingerAdapter(SearchActivity activity,SwipeRefreshLayout srl){
        this.activity=activity;
        this.srl=srl;
        dataList=new ArrayList<>();
    }

    private void setDataList(List<Singer> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void addDataList(List<Singer> dataList){
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
                MyEnvironment.serverBasePath+"music/searchSinger",
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
                MyEnvironment.serverBasePath+"music/searchSinger",
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT_2);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_search_singer,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Singer singer = dataList.get(position);

        holder.name.setText(singer.getSingerName());
        holder.cover.setImageUrl(MyEnvironment.serverBasePath+
                "music/loadSingerCover?singerId="+singer.getId());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyCircleImageView cover;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cover=itemView.findViewById(R.id.cover);
            name=itemView.findViewById(R.id.singer_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SingerActivity.startActivity(activity,
                            dataList.get(getAdapterPosition()).getId());
                }
            });
        }
    }

    static class MyHandler extends Handler {
        SearchSingerAdapter adapter;
        MyHandler(SearchSingerAdapter adapter){
            this.adapter=adapter;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    List<Singer> dataList = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<Singer>>(){}.getType());
                    adapter.end=false;
                    adapter.setDataList(dataList);
                    break;

                case BasicCode.GET_CONTENT_2:
                    dataList = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<Singer>>(){}.getType());
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
