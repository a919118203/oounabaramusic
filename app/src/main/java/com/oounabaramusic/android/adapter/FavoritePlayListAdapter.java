package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.oounabaramusic.android.PlayListActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.bean.MyImage;
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
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FavoritePlayListAdapter extends RecyclerView.Adapter<FavoritePlayListAdapter.ViewHolder> {

    private BaseActivity activity;
    private MainMyFragment fragment;
    private List<PlayList> dataList;
    private int popupPosition;

    //弹窗
    private MyBottomSheetDialog spw;
    private AlertDialog dialog;

    //弹窗内容
    TextView popWindowTitle;

    public FavoritePlayListAdapter(BaseActivity activity,MainMyFragment fragment){
        this.activity=activity;
        this.fragment=fragment;
        dataList=new ArrayList<>();
        spw=new MyBottomSheetDialog(activity);
        spw.setContentView(createContentView());
    }

    public void setDataList(List<PlayList> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public List<PlayList> getDataList() {
        return dataList;
    }

    private View createContentView() {
        View view=LayoutInflater
                .from(activity)
                .inflate(
                        R.layout.pw_favorite_playlist_item_menu,
                        (ViewGroup) activity.getWindow().getDecorView(),
                        false);

        //下载
        view.findViewById(R.id.item_menu_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                spw.dismiss();
            }
        });

        //删除
        view.findViewById(R.id.item_menu_delete_playlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userId = SharedPreferencesUtil.getUserId(activity.sp);
                int playListId = dataList.get(popupPosition).getId();

                List<Integer> data = new ArrayList<>();
                data.add(userId);
                data.add(playListId);

                new S2SHttpUtil(
                        activity,
                        new Gson().toJson(data),
                        MyEnvironment.serverBasePath+"cancelCollectPlayList",
                        new MyHandler(FavoritePlayListAdapter.this))
                .call(BasicCode.CANCEL_COLLECTION_PLAY_LIST);
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
                                new MyHandler(FavoritePlayListAdapter.this));
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
        return new FavoritePlayListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayList item = dataList.get(position);

        holder.playListCnt.setText(String.valueOf(item.getCnt()));
        holder.playListCover.setImage(new MyImage(MyImage.TYPE_PLAY_LIST_COVER,item.getId()));
        holder.playListName.setText(item.getPlayListName());
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
                    PlayListActivity.startActivity(activity,
                            dataList.get(getAdapterPosition()).getId());
                }
            });
        }
    }

    static class MyHandler extends Handler{

        FavoritePlayListAdapter adapter;

        MyHandler(FavoritePlayListAdapter adapter){
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

                case BasicCode.CANCEL_COLLECTION_PLAY_LIST:
                    adapter.fragment.loadFavoritePlaylist(
                            SharedPreferencesUtil.getUserId(adapter.activity.sp));
                    break;
            }
        }
    }
}
