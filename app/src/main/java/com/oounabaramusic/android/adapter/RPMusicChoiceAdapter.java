package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.RecentlyPlayedActivity;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RPMusicChoiceAdapter extends RecyclerView.Adapter<RPMusicChoiceAdapter.ViewHolder> {

    private RecentlyPlayedActivity activity;
    private MyHandler mHandler;
    private boolean[] selected;
    private List<Music> dataList;
    private int selectedCnt;
    private int userId;

    public RPMusicChoiceAdapter(RecentlyPlayedActivity activity){
        this.activity=activity;
        mHandler=new MyHandler(this);
        dataList=new ArrayList<>();
        selected=new boolean[dataList.size()];
    }

    public void setDataList(List<Music> dataList) {
        this.dataList = dataList;
        selected=new boolean[dataList.size()];
        selectedCnt=0;
        notifyDataSetChanged();
    }

    public List<Music> getDataList() {
        return dataList;
    }

    public void initContent(){
        userId = Integer.valueOf(activity.sp.getString("userId","-1"));

        new S2SHttpUtil(activity,
                new Gson().toJson(userId),
                MyEnvironment.serverBasePath+"music/getRPMusic",
                mHandler).call(BasicCode.GET_RP_MUSIC_END);
    }

    public List<Music> getSelected(){
        List<Music> result=new ArrayList<>();

        for(int i=0;i<dataList.size();i++){
            if(selected[i]){
                result.add(dataList.get(i));
            }
        }

        return result;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_rp_music,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.cb.setChecked(selected[position]);

        Music item = dataList.get(position);

        holder.musicName.setText(item.getMusicName());
        holder.singerName.setText(item.getSingerName().replace("/"," "));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void clearSelected(){
        selected=new boolean[dataList.size()];
        selectedCnt=0;
        activity.setTitle("已选择"+selectedCnt+"项");
        notifyDataSetChanged();
    }

    public void selectAll(){
        for(int i=0;i<dataList.size();i++){
            selected[i]=true;
        }
        selectedCnt=dataList.size();
        activity.setTitle("已选择"+selectedCnt+"项");
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CheckBox cb;
        TextView musicName,singerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cb.setChecked(!cb.isChecked());
                }
            });

            cb=itemView.findViewById(R.id.checkbox);
            cb.setVisibility(View.VISIBLE);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(selected[getAdapterPosition()]==isChecked)
                        return;

                    selected[getAdapterPosition()]=isChecked;
                    if(isChecked){
                        selectedCnt++;
                    }else{
                        selectedCnt--;
                    }

                    if(selectedCnt==dataList.size()){
                        activity.selectAll.setVisible(false);
                        activity.cancelSelectAll.setVisible(true);
                    }else{
                        activity.selectAll.setVisible(true);
                        activity.cancelSelectAll.setVisible(false);
                    }
                    Objects.requireNonNull(activity.getSupportActionBar()).setTitle("已选择"+selectedCnt+"项");
                }
            });
            itemView.findViewById(R.id.menu).setVisibility(View.GONE);

            musicName = itemView.findViewById(R.id.music_name);
            singerName = itemView.findViewById(R.id.music_singer);
        }
    }

    static class MyHandler extends Handler {
        RPMusicChoiceAdapter adapter;
        MyHandler(RPMusicChoiceAdapter adapter){
            this.adapter=adapter;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_RP_MUSIC_END:
                    List<String> data = new Gson().fromJson((String)msg.obj,
                            new TypeToken<List<String>>(){}.getType());
                    List<Music> dataList=new ArrayList<>();

                    for(String item:data){
                        dataList.add(new Music(item));
                    }
                    adapter.setDataList(dataList);
                    break;

                case BasicCode.DELETE_RP_MUSIC_END:
                    adapter.initContent();
                    break;
            }
        }
    }
}
