package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.CommentActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Comment;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyImageView;
import com.oounabaramusic.android.widget.popupwindow.MyBottomSheetDialog;
import com.oounabaramusic.android.widget.popupwindow.PlayListDialog;
import com.oounabaramusic.android.widget.popupwindow.SingerDialog;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RPMusicNormalAdapter extends RecyclerView.Adapter<RPMusicNormalAdapter.ViewHolder> {

    private BaseActivity activity;
    private MyBottomSheetDialog spw;
    private List<Music> dataList;
    private int popupPosition;
    private int userId;
    private MyHandler mHandler;

    private MyImageView musicCover;
    private TextView titleMusicName,titleSingerName,singerName;

    public RPMusicNormalAdapter(BaseActivity activity){
        this.activity=activity;
        dataList=new ArrayList<>();
        spw=new MyBottomSheetDialog(activity);
        spw.setContentView(createContentView());
        mHandler=new MyHandler(this);
    }

    public void initContent(){
        userId = Integer.valueOf(activity.sp.getString("userId","-1"));

        new S2SHttpUtil(activity,
                new Gson().toJson(userId),
                MyEnvironment.serverBasePath+"music/getRPMusic",
                mHandler).call(BasicCode.GET_RP_MUSIC_END);
    }

    public void setDataList(List<Music> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public List<Music> getDataList() {
        return dataList;
    }

    private View createContentView() {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.pw_music_menu,
                        (ViewGroup) activity.getWindow().getDecorView(),
                        false);

        musicCover = view.findViewById(R.id.music_cover);
        titleMusicName = view.findViewById(R.id.music_name);
        titleSingerName = view.findViewById(R.id.music_singer_in_title);
        singerName = view.findViewById(R.id.music_singer);

        //下一首播放
        view.findViewById(R.id.music_next_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getBinder().nextPlay(dataList.get(popupPosition));
                spw.dismiss();
            }
        });

        //收藏到歌单
        view.findViewById(R.id.music_add_to_playlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userId = Integer.valueOf(activity.sp.getString("userId","-1"));
                new PlayListDialog(activity,userId,dataList.get(popupPosition).getId());
                spw.dismiss();
            }
        });

        //下载
        view.findViewById(R.id.music_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getDownloadBinder().addTask(dataList.get(popupPosition));
                spw.dismiss();
            }
        });

        //评论
        view.findViewById(R.id.music_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentActivity.startActivity(activity,dataList.get(popupPosition).getId(),Comment.MUSIC);
                spw.dismiss();
            }
        });

        //歌手
        view.findViewById(R.id.music_search_singer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SingerDialog(activity,
                        dataList.get(popupPosition).getSingerName(),
                        dataList.get(popupPosition).getSingerId());
                spw.dismiss();
            }
        });

        view.findViewById(R.id.music_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userId = Integer.valueOf(activity.sp.getString("userId","-1"));
                List<Integer> data = new ArrayList<>();
                data.add(userId);
                data.add(dataList.get(popupPosition).getId());

                new S2SHttpUtil(activity,
                        new Gson().toJson(data),
                        MyEnvironment.serverBasePath+"music/deleteRPMusic",
                        mHandler).call(BasicCode.DELETE_RP_MUSIC_END);

                spw.dismiss();
            }
        });

        return view;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_rp_music,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Music item = dataList.get(position);

        holder.musicName.setText(item.getMusicName());
        holder.singerName.setText(item.getSingerName().replace("/"," "));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView menu;
        TextView musicName,singerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupPosition=getAdapterPosition();
                    Music item=dataList.get(popupPosition);

                    musicCover.setImageUrl(MyEnvironment.serverBasePath
                            +"music/loadMusicCover?singerId="
                            +item.getSingerId().split("/")[0]);
                    titleMusicName.setText(item.getMusicName());
                    titleSingerName.setText(item.getSingerName().replace("/"," "));
                    RPMusicNormalAdapter.this.singerName.setText(item.getSingerName().replace("/"," "));
                    spw.show();
                }
            });

            musicName = itemView.findViewById(R.id.music_name);
            singerName = itemView.findViewById(R.id.music_singer);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.getBinder().playMusic(dataList.get(getAdapterPosition()));
                }
            });
        }
    }

    static class MyHandler extends Handler{
        RPMusicNormalAdapter adapter;
        MyHandler(RPMusicNormalAdapter adapter){
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
