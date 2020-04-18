package com.oounabaramusic.android.adapter;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.ListenRankActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.bean.MusicListen;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListenRankAdapter extends RecyclerView.Adapter<ListenRankAdapter.ViewHolder>{

    private ListenRankActivity activity;
    private List<Music> dataList;

    public ListenRankAdapter(ListenRankActivity activity){
        this.activity=activity;
        dataList=new ArrayList<>();
    }

    public void setDataList(List<Music> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void refresh(){
        MusicListen ml=new MusicListen();
        ml.setUserId(activity.getUserId());

        new S2SHttpUtil(
                activity,
                activity.gson.toJson(ml),
                MyEnvironment.serverBasePath+"music/getListenInfo",
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_music,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Music item = dataList.get(position);

        holder.index.setText(String.valueOf(position+1));
        holder.cnt.setText(FormatUtil.numberToString(item.getListenCnt()));
        holder.musicName.setText(item.getMusicName());
        holder.singerName.setText(item.getSingerName().replace("/"," "));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView cnt,index;
        TextView musicName,singerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.findViewById(R.id.music_menu).setVisibility(View.GONE);
            itemView.findViewById(R.id.play_cnt).setVisibility(View.VISIBLE);

            cnt=itemView.findViewById(R.id.cnt);
            index=itemView.findViewById(R.id.index);
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

    static class MyHandler extends Handler {
        private ListenRankAdapter adapter;
        MyHandler(ListenRankAdapter adapter){
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
                    adapter.setDataList(dataList);
                    break;
            }
        }
    }
}
