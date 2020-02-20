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
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.PlayListActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.fragment.UserInfoMainFragment;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.widget.customview.MyImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserInfoFavoritePlayListAdapter extends RecyclerView.Adapter<UserInfoFavoritePlayListAdapter.ViewHolder> {

    private BaseActivity activity;
    private UserInfoMainFragment fragment;
    private List<PlayList> dataList;
    private int userId;

    public UserInfoFavoritePlayListAdapter(BaseActivity activity,UserInfoMainFragment fragment){
        this.activity=activity;
        this.fragment=fragment;
        dataList=new ArrayList<>();
    }

    public void setUserId(int userId) {
        this.userId = userId;
        Map<String,Integer> data = new HashMap<>();

        new S2SHttpUtil(
                activity,
                userId+"",
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

        holder.cnt.setText(String.valueOf(item.getCnt()));
        holder.cover.setImage(new MyImage(MyImage.TYPE_PLAY_LIST_COVER,item.getId()));
        holder.name.setText(item.getPlayListName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyImageView cover;
        TextView name,cnt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.findViewById(R.id.my_playlist_item_menu).setVisibility(View.GONE);

            cover = itemView.findViewById(R.id.playlist_cover);
            name = itemView.findViewById(R.id.playlist_name);
            cnt = itemView.findViewById(R.id.playlist_cnt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayListActivity.startActivity(activity,
                            dataList.get(getAdapterPosition()).getId());
                }
            });
        }
    }

    static class MyHandler extends Handler{
        UserInfoFavoritePlayListAdapter adapter;
        MyHandler(UserInfoFavoritePlayListAdapter adapter){
            this.adapter=adapter;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    Map<String,String> data =new Gson().fromJson((String) msg.obj,
                            new TypeToken<Map<String,String>>(){}.getType());

                    List<PlayList> dataList = new Gson().fromJson(data.get("playLists"),
                            new TypeToken<List<PlayList>>(){}.getType());

                    int size = Integer.valueOf(data.get("size"));
                    adapter.fragment.setFavoritePlayListCnt(size);
                    adapter.dataList=dataList.subList(0,size>10?10:size);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
