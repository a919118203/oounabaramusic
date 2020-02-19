package com.oounabaramusic.android.adapter;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.CommentActivity;
import com.oounabaramusic.android.MusicPlayActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SingerActivity;
import com.oounabaramusic.android.bean.Comment;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.okhttputil.PlayListHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;
import com.oounabaramusic.android.widget.customview.MyImageView;
import com.oounabaramusic.android.widget.popupwindow.MyBottomSheetDialog;
import com.oounabaramusic.android.widget.popupwindow.PlayListDialog;
import com.oounabaramusic.android.widget.popupwindow.SingerDialog;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class SingerMusicMenuAdapter extends RecyclerView.Adapter<SingerMusicMenuAdapter.ViewHolder> {

    private SingerActivity activity;
    private MyBottomSheetDialog spw;
    private List<Music> dataList;
    private int popupPosition;
    private SharedPreferences sp;

    public SingerMusicMenuAdapter(SingerActivity activity){
        this.activity=activity;
        spw=new MyBottomSheetDialog(activity);
        spw.setContentView(createContentView());
        dataList=new ArrayList<>();
        sp= PreferenceManager.getDefaultSharedPreferences(activity);
    }

    public void setDataList(List<Music> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    private View createContentView() {
        View view=LayoutInflater.from(activity).inflate(R.layout.pw_singer_main_music_menu,
                (ViewGroup) activity.getWindow().getDecorView(),false);

        musicCover=view.findViewById(R.id.music_cover);
        titleMusicName=view.findViewById(R.id.music_name);
        titleSingerName=view.findViewById(R.id.music_singer_in_title);
        singerName=view.findViewById(R.id.music_singer);

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
                    spw.dismiss();
                    new PlayListDialog(activity,
                            Integer.valueOf(userId),
                            dataList.get(popupPosition).getId());
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
                String[] singerNames=dataList.get(popupPosition).getSingerName().split("/");
                if(singerNames.length>1){
                    new SingerDialog(activity,
                            dataList.get(popupPosition).getSingerName(),
                            dataList.get(popupPosition).getSingerId(),
                            activity.getSingerId());
                }else{
                    toSingerActivity(Integer.valueOf(dataList.get(popupPosition).getSingerId()));
                }
                spw.dismiss();
            }
        });
        return view;
    }

    /**
     * 前往SingerActivity
     * @param singerId
     */
    private void toSingerActivity(int singerId){
        if(singerId==activity.getSingerId()){
            return ;
        }

        Intent intent=new Intent(activity, SingerActivity.class);
        intent.putExtra("singerId",singerId);
        activity.startActivity(intent);
    }


    private MyImageView musicCover;
    private TextView titleMusicName,titleSingerName,singerName;
    private void flushPopup(){
        Music item=dataList.get(popupPosition);

        musicCover.setImage(new MyImage(
                MyImage.TYPE_SINGER_COVER,
                Integer.valueOf(item.getSingerId().split("/")[0])));
        titleMusicName.setText(item.getMusicName());
        titleSingerName.setText(item.getSingerName());
        singerName.setText(item.getSingerName());
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
        holder.musicSinger.setText(dataList.get(position).getSingerName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView musicName,musicSinger,index;
        ImageView musicMenu,musicCover;
        TextView MusicSinger,titleMusicName;

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
                    flushPopup();
                    spw.show();
                }
            });
        }
    }
}
