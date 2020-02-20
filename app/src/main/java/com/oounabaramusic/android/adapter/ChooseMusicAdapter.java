package com.oounabaramusic.android.adapter;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.ChooseMusicActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.bean.MyImage;
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

public class ChooseMusicAdapter extends RecyclerView.Adapter<ChooseMusicAdapter.ViewHolder> {

    private ChooseMusicActivity activity;
    private List<Music> dataList;

    private SwipeRefreshLayout srl;
    private boolean end;

    public ChooseMusicAdapter(ChooseMusicActivity activity,SwipeRefreshLayout srl){
        this.activity=activity;
        this.srl=srl;
        dataList=new ArrayList<>();
    }

    public void setDataList(List<Music> dataList) {
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_item_choose_music,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Music item = dataList.get(position);

        holder.cover.setImage(new MyImage(
                MyImage.TYPE_SINGER_COVER,
                Integer.valueOf(item.getSingerId().split("/")[0])));
        holder.name.setText(item.getMusicName());
        holder.singer.setText(item.getSingerName().replace("/"," "));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyImageView cover;
        TextView name,singer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cover=itemView.findViewById(R.id.cover);
            name=itemView.findViewById(R.id.name);
            singer=itemView.findViewById(R.id.singer);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent data = new Intent();
                    data.putExtra("music",dataList.get(getAdapterPosition()));
                    activity.setResult(0,data);
                    activity.finish();
                }
            });
        }
    }

    static class MyHandler extends Handler{
        ChooseMusicAdapter adapter;
        MyHandler(ChooseMusicAdapter adapter){
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
