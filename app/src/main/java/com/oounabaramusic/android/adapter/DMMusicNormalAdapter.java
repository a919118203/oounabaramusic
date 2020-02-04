package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oounabaramusic.android.DownloadManagementActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyImageView;
import com.oounabaramusic.android.widget.popupwindow.MyBottomSheetDialog;
import com.oounabaramusic.android.widget.popupwindow.PlayListDialog;
import com.oounabaramusic.android.widget.popupwindow.SingerDialog;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DMMusicNormalAdapter extends RecyclerView.Adapter<DMMusicNormalAdapter.ViewHolder> {

    private DownloadManagementActivity activity;
    private MyBottomSheetDialog spw;
    private List<Music> dataList;
    private int popupPosition;

    public DMMusicNormalAdapter(DownloadManagementActivity activity){
        this.activity=activity;
        spw=new MyBottomSheetDialog(activity);
        spw.setContentView(createContentView());
        dataList=activity.getLocalMusicDao().selectAllDownloadEnd();
    }

    public List<Music> getDataList() {
        return dataList;
    }

    public void refresh(){
        dataList=activity.getLocalMusicDao().selectAllDownloadEnd();
        notifyDataSetChanged();
    }

    private MyImageView musicCover;
    private TextView titleMusicName,titleSingerName;
    private TextView singerName;
    private View createContentView() {
        View view=LayoutInflater.from(activity).inflate(R.layout.pw_music_menu,
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
                if(activity.sp.getBoolean("login",false)){
                    String userId=activity.sp.getString("userId","-1");
                    new PlayListDialog(activity,
                            Integer.valueOf(userId),
                            dataList.get(popupPosition).getMd5());
                }else{
                    Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
                }
                spw.dismiss();
            }
        });

        view.findViewById(R.id.music_download).setVisibility(View.GONE);


        view.findViewById(R.id.music_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


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
                spw.dismiss();
                showDeleteMusicDialog(dataList.get(popupPosition));
            }
        });
        return view;
    }

    private AlertDialog dialog;
    private CheckBox cb;
    private void showDeleteMusicDialog(final Music item){
        if(dialog==null){
            View view=LayoutInflater.from(activity).inflate(
                    R.layout.alertdialog_delete_local_music,
                    (ViewGroup) activity.getWindow().getDecorView(),false);
            cb=view.findViewById(R.id.checkbox);


            dialog=new AlertDialog.Builder(activity)
                    .setTitle("确定删除音乐")
                    .setView(view)
                    .setNegativeButton("取消",null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.getLocalMusicDao().deleteMusicByMd5(item.getMd5());

                            File file=new File(item.getFilePath());

                            if(cb.isChecked()){
                                if(file.exists()){
                                    file.delete();
                                }
                            }

                            refresh();
                        }
                    })
                    .create();

            dialog.getWindow().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.layout_card_2));
        }

        dialog.show();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_dm_music,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.musicName.setText(dataList.get(position).getMusicName());
        holder.singerName.setText(dataList.get(position).getSingerName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView musicName,singerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            musicName=itemView.findViewById(R.id.music_name);
            singerName=itemView.findViewById(R.id.music_singer);

            itemView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupPosition=getAdapterPosition();

                    Music item=dataList.get(popupPosition);
                    musicCover.setImageUrl(MyEnvironment.serverBasePath+
                            "music/loadMusicCover?singerId="+item.getSingerId().split("/")[0]);
                    DMMusicNormalAdapter.this.titleMusicName.setText(item.getMusicName());
                    DMMusicNormalAdapter.this.titleSingerName.setText(item.getSingerName().
                            replace("/"," "));
                    DMMusicNormalAdapter.this.singerName.setText(item.getSingerName().
                            replace("/"," "));
                    spw.show();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.getBinder().playMusic(dataList.get(getAdapterPosition()));
                }
            });
        }
    }
}
