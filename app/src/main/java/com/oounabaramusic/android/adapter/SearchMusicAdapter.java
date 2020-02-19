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
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SearchActivity;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SearchMusicAdapter extends RecyclerView.Adapter<SearchMusicAdapter.ViewHolder> {

    private SearchActivity activity;
    private List<Music> dataList;

    private boolean end;
    private SwipeRefreshLayout srl;

    public SearchMusicAdapter(SearchActivity activity,SwipeRefreshLayout srl){
        this.activity=activity;
        this.srl=srl;
        dataList=new ArrayList<>();
    }

    private void setDataList(List<Music> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void addDataList(List<Music> dataList){
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
                MyEnvironment.serverBasePath+"music/searchMusic",
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
                MyEnvironment.serverBasePath+"music/searchMusic",
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT_2);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_search_music,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.musicName.setText(dataList.get(position).getMusicName());
        holder.singerName.setText(dataList.get(position).getSingerName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView musicName,singerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            musicName=itemView.findViewById(R.id.music_name);
            singerName=itemView.findViewById(R.id.music_singer);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.getBinder().playMusic(dataList.get(getAdapterPosition()));
                }
            });
        }
    }

    static class MyHandler extends Handler{
        SearchMusicAdapter adapter;
        MyHandler(SearchMusicAdapter adapter){
            this.adapter=adapter;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    List<String> data = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<String>>(){}.getType());

                    List<Music> dataList = new ArrayList<>();
                    for(String str:data){
                        dataList.add(new Music(str));
                    }

                    adapter.end=false;
                    adapter.setDataList(dataList);
                    break;

                case BasicCode.GET_CONTENT_2:
                    data = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<String>>(){}.getType());

                    dataList = new ArrayList<>();
                    for(String str:data){
                        dataList.add(new Music(str));
                    }

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
