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
import com.oounabaramusic.android.PlayListActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SearchActivity;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SearchPlayListAdapter extends RecyclerView.Adapter<SearchPlayListAdapter.ViewHolder> {

    private SearchActivity activity;
    private List<PlayList> dataList;

    private boolean end;
    private SwipeRefreshLayout srl;

    public SearchPlayListAdapter(SearchActivity activity,SwipeRefreshLayout srl){
        this.activity=activity;
        this.srl=srl;
        dataList=new ArrayList<>();
    }

    private void setDataList(List<PlayList> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void addDataList(List<PlayList> dataList){
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
                MyEnvironment.serverBasePath+"searchPlayList",
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
                MyEnvironment.serverBasePath+"searchPlayList",
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT_2);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_search_play_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayList item = dataList.get(position);

        holder.name.setText(item.getPlayListName());
        holder.info.setText(item.getIntroduction());
        holder.cover.setImage(new MyImage(MyImage.TYPE_PLAY_LIST_COVER,item.getId()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyImageView cover;
        TextView name,info;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cover=itemView.findViewById(R.id.cover);
            name=itemView.findViewById(R.id.name);
            info=itemView.findViewById(R.id.info);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayListActivity.startActivity(activity,
                            dataList.get(getAdapterPosition()).getId());
                }
            });
        }
    }

    static class MyHandler extends Handler {
        SearchPlayListAdapter adapter;
        MyHandler(SearchPlayListAdapter adapter){
            this.adapter=adapter;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    List<PlayList> dataList = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<PlayList>>(){}.getType());
                    adapter.end=false;
                    adapter.setDataList(dataList);
                    break;

                case BasicCode.GET_CONTENT_2:
                    dataList = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<PlayList>>(){}.getType());
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
