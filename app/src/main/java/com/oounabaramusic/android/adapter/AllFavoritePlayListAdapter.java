package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.content.Intent;
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

public class AllFavoritePlayListAdapter extends RecyclerView.Adapter<AllFavoritePlayListAdapter.ViewHolder> {

    private Activity activity;
    private List<PlayList> dataList;
    private int userId;

    public AllFavoritePlayListAdapter(Activity activity){
        this.activity=activity;
        dataList=new ArrayList<>();
    }

    public void setUserId(int userId) {
        this.userId = userId;
        initContent();
    }

    public void initContent(){
        Map<String,Integer> data = new HashMap<>();
        data.put("userId",userId);
        data.put("start",-1);

        new S2SHttpUtil(
                activity,
                new Gson().toJson(data),
                MyEnvironment.serverBasePath+"getCollectPlayList",
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_play_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayList item = dataList.get(position);

        holder.cover.setImage(new MyImage(MyImage.TYPE_PLAY_LIST_COVER,item.getId()));
        holder.name.setText(item.getPlayListName());
        holder.cnt.setText(String.valueOf(item.getCnt()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyImageView cover;
        TextView cnt,name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.findViewById(R.id.my_playlist_item_menu).setVisibility(View.GONE);

            cover=itemView.findViewById(R.id.playlist_cover);
            cnt = itemView.findViewById(R.id.playlist_cnt);
            name = itemView.findViewById(R.id.playlist_name);

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

        AllFavoritePlayListAdapter adapter;
        MyHandler(AllFavoritePlayListAdapter adapter){
            this.adapter=adapter;
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    Map<String,String> data = new Gson().fromJson((String)msg.obj,
                            new TypeToken<Map<String,String>>(){}.getType());

                    List<PlayList> dataList = new Gson().fromJson(data.get("playLists"),
                            new TypeToken<List<PlayList>>(){}.getType());

                    adapter.dataList=dataList;
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
