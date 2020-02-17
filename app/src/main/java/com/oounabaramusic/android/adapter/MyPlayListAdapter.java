package com.oounabaramusic.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.EditPlayListInfoActivity;
import com.oounabaramusic.android.PlayListActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.fragment.MainMyFragment;
import com.oounabaramusic.android.okhttputil.PlayListHttpUtil;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.widget.customview.MyImageView;
import com.oounabaramusic.android.widget.popupwindow.MyBottomSheetDialog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyPlayListAdapter extends RecyclerView.Adapter<MyPlayListAdapter.ViewHolder> {

    private BaseActivity activity;
    private MainMyFragment fragment;
    private MyBottomSheetDialog spw;
    private LinkedList<PlayList> dataList;
    private int popupPosition;

    private TextView popWindowTitle;

    public MyPlayListAdapter(BaseActivity activity, MainMyFragment fragment){
        this.activity=activity;
        this.fragment=fragment;
        spw=new MyBottomSheetDialog(activity);
        spw.setContentView(createContentView());
        dataList=new LinkedList<>();
    }

    public void setDataList(LinkedList<PlayList> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public LinkedList<PlayList> getDataList() {
        return dataList;
    }

    public void addData(PlayList item){

        //第一个为“我喜欢的音乐”，所以新的插在第二个
        dataList.add(1,item);
        notifyDataSetChanged();
    }

    private View createContentView() {
        View view=LayoutInflater.from(activity).inflate(R.layout.pw_my_playlist_item_menu, (ViewGroup) activity.getWindow().getDecorView(),false);

        //下载
        view.findViewById(R.id.item_menu_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                spw.dismiss();
            }
        });

        //编辑歌单信息
        view.findViewById(R.id.item_menu_edit_playlist_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, EditPlayListInfoActivity.class);
                intent.putExtra("playList",dataList.get(popupPosition));
                activity.startActivity(intent);
                spw.dismiss();
            }
        });

        //删除
        view.findViewById(R.id.item_menu_delete_playlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
                spw.dismiss();
            }
        });

        popWindowTitle=view.findViewById(R.id.menu_title);
        return view;
    }

    private void showDialog(){
        AlertDialog dialog=new AlertDialog.Builder(activity)
                .setTitle("确定要下载歌单中全部歌曲吗？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PlayListHttpUtil.findMusicByPlayList(activity,
                                dataList.get(popupPosition).getId()+"",
                                new MyHandler(MyPlayListAdapter.this));
                    }
                })
                .create();

        dialog.getWindow().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.layout_card_2));
        dialog.show();
    }

    private void showDeleteDialog(){
        AlertDialog dialog=new AlertDialog.Builder(activity)
                .setTitle("确定要删除歌单吗？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<Integer> data=new ArrayList<>();
                        data.add(dataList.get(popupPosition).getId());

                        new S2SHttpUtil(activity,
                                activity.gson.toJson(data),
                                MyEnvironment.serverBasePath+"deletePlayList",
                                new MyHandler(MyPlayListAdapter.this)).call(BasicCode.DELETE_PLAY_LIST_END);
                    }
                })
                .create();

        dialog.getWindow().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.layout_card_2));
        dialog.show();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_play_list,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayList item=dataList.get(position);

        holder.playListCover.setImageUrl(MyEnvironment.serverBasePath+
                "loadPlayListCover?playListId="+
                item.getId());
        holder.playListName.setText(item.getPlayListName());
        holder.playListCnt.setText(String.valueOf(item.getCnt()));

        if(position==0){
            holder.playListItemMenu.setVisibility(View.GONE);
        }else{
            holder.playListItemMenu.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        MyImageView playListCover;
        TextView playListName;
        TextView playListCnt;
        ImageView playListItemMenu;
        ViewHolder(@NonNull View itemView) {
            super(itemView);


            playListCover=itemView.findViewById(R.id.playlist_cover);
            playListName=itemView.findViewById(R.id.playlist_name);
            playListCnt=itemView.findViewById(R.id.playlist_cnt);
            playListItemMenu=itemView.findViewById(R.id.my_playlist_item_menu);
            playListItemMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupPosition=getAdapterPosition();
                    popWindowTitle.setText(dataList.get(popupPosition).getPlayListName());
                    spw.show();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity, PlayListActivity.class);
                    intent.putExtra("playList",dataList.get(getAdapterPosition()));

                    if(getAdapterPosition()==0){
                        intent.putExtra("isMyLove",true);
                    }

                    activity.startActivity(intent);
                }
            });
        }
    }

    static class MyHandler extends Handler{
        MyPlayListAdapter adapter;
        MyHandler(MyPlayListAdapter adapter){
            this.adapter=adapter;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PlayListHttpUtil.MESSAGE_FIND_PLAY_LIST_MUSIC_END:
                    List<String> data=new Gson().fromJson((String)msg.obj,
                            new TypeToken<List<String>>(){}.getType());
                    for(String item:data){
                        adapter.activity.
                                getDownloadBinder().
                                addTask(new Music(item));
                    }
                    break;
                case BasicCode.DELETE_PLAY_LIST_END:
                    adapter.fragment.loadMyPlayList(SharedPreferencesUtil.getUserId(adapter.activity.sp)+"");
                    break;
            }
        }
    }
}
