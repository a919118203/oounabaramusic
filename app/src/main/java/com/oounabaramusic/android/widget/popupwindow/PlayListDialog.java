package com.oounabaramusic.android.widget.popupwindow;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.okhttputil.PlayListHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PlayListDialog {
    private int musicId;
    private AlertDialog dialog;
    private Context context;
    private List<PlayList> dataList;

    public PlayListDialog(Context context, int musicId,int userId){
        this.context=context;
        this.musicId=musicId;
        PlayListHttpUtil.findPlayListByUser(context,userId+"",new MHandler(this));
    }

    private void initDialog() {
        if(dataList.size()==0){
            Toast.makeText(context, "请先创建歌单", Toast.LENGTH_SHORT).show();
            return;
        }

        RecyclerView rv=new RecyclerView(context);
        rv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

        rv.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {


            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(context).inflate(R.layout.rv_item_play_list,parent,false);
                return new MyViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                holder.playListCnt.setText(dataList.get(position).getCnt()+"");
                holder.playListName.setText(dataList.get(position).getPlayListName());
                holder.playListCover.setImageUrl(MyEnvironment.serverBasePath+
                        "loadPlayListCover?playListId="+dataList.get(position).getId());
            }

            @Override
            public int getItemCount() {
                return dataList.size();
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(context));

        dialog=new AlertDialog.Builder(context)
                .setTitle("选择歌单")
                .setView(rv)
                .create();

        dialog.show();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        MyImageView playListCover;
        TextView playListName,playListCnt;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.findViewById(R.id.my_playlist_item_menu).setVisibility(View.GONE);
            playListCover=itemView.findViewById(R.id.playlist_cover);
            playListName=itemView.findViewById(R.id.playlist_name);
            playListCnt=itemView.findViewById(R.id.playlist_cnt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Integer> data=new ArrayList<>();
                    data.add(musicId);
                    data.add(dataList.get(getAdapterPosition()).getId());
                    PlayListHttpUtil.addToPlayList(context,new Gson().toJson(data),new Handler());//可以加 添加失败的代码
                    dialog.dismiss();
                }
            });
        }
    }

    static class MHandler extends Handler{
        PlayListDialog dialog;
        MHandler(PlayListDialog dialog){
            this.dialog=dialog;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PlayListHttpUtil.MESSAGE_FIND_MY_PLAY_LIST_END:
                    dialog.dataList=new Gson().fromJson((String)msg.obj,
                            new TypeToken<List<PlayList>>(){}.getType());
                    dialog.initDialog();
                    break;
            }
        }
    }
}
