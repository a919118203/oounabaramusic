package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.oounabaramusic.android.LocalMusicActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.ShowPopupWindow;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 *
 * 本地音乐列表的Adapter类
 */
public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.LocalMusicViewHolder>{

    private ShowPopupWindow spwMusicMenu;
    private ShowPopupWindow spwMusicInfo;
    private ShowPopupWindow spwAddToPlaylist;
    private List<Music> musicList;
    private FrameLayout rootView;//用于spw的父View
    private boolean[] selected;//多选模式时记录多选的情况
    private int toolBarMode;
    private Activity activity;

    public LocalMusicAdapter(Activity activity,FrameLayout rootView,List<Music> data){
        this.activity=activity;
        this.rootView=rootView;
        musicList=data;
        selected=new boolean[data.size()+10];
    }

    @NonNull
    @Override
    public LocalMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LocalMusicViewHolder vh=new LocalMusicViewHolder(
                LayoutInflater.from(activity).inflate(R.layout.recyclerview_item_local_music,parent,false));
        spwMusicMenu =new ShowPopupWindow(
                createContextViewMenu(vh.getAdapterPosition(),vh),rootView);
        spwMusicInfo =new ShowPopupWindow(
                createContextViewInfo(vh.getAdapterPosition(),vh),rootView);

        //每一项中的菜单被点击时
        vh.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //给所有的popWindow的textView赋值

                spwMusicMenu.showPopupMenu();
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull LocalMusicViewHolder holder, int position) {
        if(toolBarMode==LocalMusicActivity.TOOLBAR_MODE_MULTIPLE_CHOICE){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.menu.setVisibility(View.GONE);
        }else{
            holder.checkBox.setVisibility(View.GONE);
            holder.menu.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 创建点击某一项菜单后弹出的popWindow所需要的contentView
     * @param position item的位置
     * @param vh       将spw中的需要根据不同的歌曲改成不同的文本的textView放入到vh中
     * @return         spw的contentView
     */
    private View createContextViewInfo(int position,LocalMusicViewHolder vh) {
        View view=LayoutInflater.from(activity).inflate(R.layout.popupwindow_music_info,null);

        //歌曲信息的“修改”被点击时
        view.findViewById(R.id.change_music_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        vh.musicInfoFileName=view.findViewById(R.id.music_info_file_name);
        vh.musicInfoFilePath=view.findViewById(R.id.music_info_file_path);
        vh.musicInfoFileSize=view.findViewById(R.id.music_info_file_size);
        vh.musicInfoMusicName=view.findViewById(R.id.music_info_music_name);
        vh.musicInfoPlayLength=view.findViewById(R.id.music_info_file_play_length);
        return view;
    }

    /**
     * 创建点击某一项菜单后再继续点击“查看歌曲信息”时弹出的popWindow所需要的contentView
     * @param position item的位置
     * @param vh       将spw中的需要根据不同的歌曲改成不同的文本的textView放入到vh中
     * @return         spw的contentView
     */
    private View createContextViewMenu(final int position, final LocalMusicViewHolder vh) {
        View view=LayoutInflater.from(activity).inflate(R.layout.popupwindow_local_music_item_menu,null);

        //菜单中下一首播放被点击时
        view.findViewById(R.id.item_menu_next_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //菜单中收藏到歌单被点击时
        view.findViewById(R.id.item_menu_add_to_playlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spwMusicMenu.dismiss();
                int width= (int) (DensityUtil.getDisplayWidth(activity)*0.9);
                int height= (int) (DensityUtil.getDisplayHeight(activity)*0.6);
                spwAddToPlaylist=new ShowPopupWindow(createContentViewAddToPlaylist(position),rootView,width,height);
                spwAddToPlaylist.showPopupMenu(Gravity.CENTER);
            }
        });

        //菜单中查看歌曲信息被点击时
        view.findViewById(R.id.item_menu_music_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spwMusicMenu.dismiss();
                spwMusicInfo.showPopupMenu();
            }
        });

        //菜单中删除被点击时
        view.findViewById(R.id.item_menu_delete_music).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        vh.musicMenuMusicName=view.findViewById(R.id.music_menu_music_name);
        return view;
    }

    /**
     * 创建弹出收藏到某个歌单时弹出的popWindow的contentView
     * @param position  item的位置
     * @return          contentView
     */
    private View createContentViewAddToPlaylist(int position){
        View view=LayoutInflater.from(activity).inflate(R.layout.popupwindow_add_to_playlist,null);

        RecyclerView playlist=view.findViewById(R.id.add_to_playlist_recycler_view);
        playlist.setLayoutManager(new LinearLayoutManager(activity));
        playlist.setAdapter(new PlayListAdapter(activity));//TODO
        return view;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class LocalMusicViewHolder extends RecyclerView.ViewHolder{

        ImageView isPlaying,menu;
        TextView musicName;
        TextView musicMenuMusicName;
        TextView musicInfoMusicName,musicInfoFileName,musicInfoPlayLength,musicInfoFileSize,musicInfoFilePath;
        CheckBox checkBox;         //多选时的多选框

        LocalMusicViewHolder(@NonNull View itemView) {
            super(itemView);
            isPlaying=itemView.findViewById(R.id.recycler_view_item_is_playing);
            menu=itemView.findViewById(R.id.recycler_view_local_music_menu);
            musicName=itemView.findViewById(R.id.recycler_view_item_music_name);
            checkBox=itemView.findViewById(R.id.local_music_choose);
        }
    }

    public void setToolBarMode(int toolBarMode) {
        this.toolBarMode = toolBarMode;
    }

    /**
     * 检查是否有歌曲的多选框被选中
     * @return true->有 false->没有
     */
    public boolean checkSelected(){
        int len=musicList.size();
        for(int i=0;i<len;i++){
            if(selected[i])
                return true;
        }
        return false;
    }
}