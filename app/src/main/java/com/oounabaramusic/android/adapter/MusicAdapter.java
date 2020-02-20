package com.oounabaramusic.android.adapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.oounabaramusic.android.CommentActivity;
import com.oounabaramusic.android.PlayListActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SingerActivity;
import com.oounabaramusic.android.bean.Comment;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyImageView;
import com.oounabaramusic.android.widget.popupwindow.MyBottomSheetDialog;
import com.oounabaramusic.android.widget.popupwindow.PlayListDialog;
import com.oounabaramusic.android.widget.popupwindow.SingerDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private PlayListActivity activity;
    private MyBottomSheetDialog spw;
    private List<Music> dataList;
    private int popupPosition;
    private SharedPreferences sp;

    public MusicAdapter(PlayListActivity activity){
        this.activity=activity;
        sp= PreferenceManager.getDefaultSharedPreferences(activity);
        spw=new MyBottomSheetDialog(activity);
        spw.setContentView(createContentView());
        dataList=new ArrayList<>();
    }

    public void setDataList(List<Music> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public List<Music> getDataList() {
        return dataList;
    }

    private MyImageView musicCover;
    private TextView titleMusicName,titleMusicSinger;
    private TextView musicSinger;
    private View createContentView() {
        View view=LayoutInflater.from(activity).inflate(R.layout.pw_music_menu,
                (ViewGroup) activity.getWindow().getDecorView(),false);

        musicCover=view.findViewById(R.id.music_cover);
        titleMusicName=view.findViewById(R.id.music_name);
        titleMusicSinger=view.findViewById(R.id.music_singer_in_title);
        musicSinger=view.findViewById(R.id.music_singer);

        view.findViewById(R.id.music_next_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getBinder().nextPlay(dataList.get(popupPosition));
                spw.dismiss();
            }
        });

        view.findViewById(R.id.music_add_to_playlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sp.getBoolean("login",false)){
                    String userId=sp.getString("userId","-1");
                    new PlayListDialog(activity,
                            Integer.valueOf(userId),
                            dataList.get(popupPosition).getId());
                    spw.dismiss();
                }else{
                    Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.music_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getDownloadBinder().addTask(dataList.get(popupPosition));
                spw.dismiss();
            }
        });

        view.findViewById(R.id.music_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentActivity.startActivity(activity,
                        dataList.get(popupPosition).getId(),Comment.MUSIC);
                spw.dismiss();
            }
        });

        view.findViewById(R.id.music_search_singer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String singerNames = dataList.get(popupPosition).getSingerName(),
                        singerIds = dataList.get(popupPosition).getSingerId();
                if(singerIds.contains("/")){
                    new SingerDialog(activity,
                            singerNames,
                            singerIds);
                }else{
                    Intent intent=new Intent(activity, SingerActivity.class);
                    intent.putExtra("singerId",Integer.valueOf(singerIds));
                    activity.startActivity(intent);
                }
                spw.dismiss();
            }
        });

        boolean vis=false;

        if(sp.getBoolean("login",false)){
            String userId=sp.getString("userId","-1");
            if(activity.getPlayList().getCreateUserId()==Integer.valueOf(userId)){
                vis=true;
            }
        }

        LinearLayout delete=view.findViewById(R.id.music_delete);
        if(vis){
            delete.setVisibility(View.VISIBLE);
        }else{
            delete.setVisibility(View.GONE);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Integer> data = new HashMap<>();
                data.put("playListId",activity.getPlayList().getId());
                data.put("musicId",dataList.get(popupPosition).getId());

                new S2SHttpUtil(
                        activity,
                        new Gson().toJson(data),
                        MyEnvironment.serverBasePath+"deletePlayListMusic",
                        activity.getHandler())
                .call(BasicCode.DELETE_MUSIC_END);
                spw.dismiss();
            }
        });
        return view;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_music,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.index.setText(String.valueOf(position+1));
        holder.musicName.setText(dataList.get(position).getMusicName());
        holder.musicSinger.setText(dataList.get(position).getSingerName().replace("/"," "));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView musicName,musicSinger,index;
        ImageView musicMenu;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.getBinder().playMusic(dataList.get(getAdapterPosition()));
                }
            });

            musicName=itemView.findViewById(R.id.music_name);
            musicSinger=itemView.findViewById(R.id.music_singer);
            index=itemView.findViewById(R.id.index);
            musicMenu=itemView.findViewById(R.id.music_menu);

            musicMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupPosition=getAdapterPosition();
                    Music item = dataList.get(popupPosition);
                    musicCover.setImage(new MyImage(
                            MyImage.TYPE_SINGER_COVER,
                            Integer.valueOf(item.getSingerId().split("/")[0])));
                    titleMusicName.setText(item.getMusicName());
                    titleMusicSinger.setText(item.
                            getSingerName().replace("/"," "));
                    MusicAdapter.this.musicSinger.setText(item.
                            getSingerName().replace("/"," "));
                    spw.show();
                }
            });
        }
    }
}
