package com.oounabaramusic.android.adapter;

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
import com.oounabaramusic.android.UserInfoActivity;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.fragment.UserInfoMainFragment;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserInfoMyPlayListAdapter extends RecyclerView.Adapter<UserInfoMyPlayListAdapter.ViewHolder> {

    private UserInfoActivity activity;
    private List<PlayList> dataList;
    private UserInfoMainFragment fragment;
    private int userId;

    public UserInfoMyPlayListAdapter(UserInfoActivity activity,UserInfoMainFragment fragment){
        this.activity=activity;
        this.fragment=fragment;
        dataList=new ArrayList<>();
    }

    public void setUserId(int userId) {
        this.userId = userId;

        new S2SHttpUtil(
                activity,
                userId+"",
                MyEnvironment.serverBasePath+"findPlayListByUser",
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
                    PlayListActivity.startActivity(activity,dataList.get(getAdapterPosition()).getId());
                }
            });
        }
    }

    static class MyHandler extends Handler{
        UserInfoMyPlayListAdapter adapter;
        MyHandler(UserInfoMyPlayListAdapter adapter){
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

                    int size = Integer.valueOf(data.get("size"));
                    adapter.fragment.setMyPlayListCnt(size);
                    adapter.dataList=dataList.subList(0,size>10?10:size);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
