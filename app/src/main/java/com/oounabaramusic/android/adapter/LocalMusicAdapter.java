package com.oounabaramusic.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oounabaramusic.android.LocalMusicActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.widget.popupwindow.MyPopupWindow;

import java.io.File;
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

    private MyPopupWindow spwMusicMenu;
    private int popupPosition;
    private MyPopupWindow spwMusicInfo;
    private MyPopupWindow spwAddToPlaylist;
    private List<Music> musicList;
    private boolean[] selected;//多选模式时记录多选的情况
    private int toolBarMode;
    private LocalMusicActivity activity;
    private Handler handler;

    private TextView musicMenuMusicName;
    private TextView musicInfoMusicName,musicInfoSingerName,musicInfoFileName,musicInfoPlayLength,musicInfoFileSize,musicInfoFilePath;

    public LocalMusicAdapter(LocalMusicActivity activity,List<Music> data,Handler handler){
        this.activity=activity;
        this.handler=handler;
        musicList=data;
        selected=new boolean[data.size()+10];
    }

    @NonNull
    @Override
    public LocalMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LocalMusicViewHolder vh=new LocalMusicViewHolder(
                LayoutInflater.from(activity).inflate(R.layout.rv_item_local_music,parent,false));
        return vh;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull LocalMusicViewHolder holder) {
        spwMusicMenu =new MyPopupWindow(activity,
                createContextViewMenu());
        spwMusicInfo =new MyPopupWindow(activity,
                createContextViewInfo());
        spwAddToPlaylist =new MyPopupWindow(activity,
                createContentViewAddToPlaylist(),
                Gravity.CENTER);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalMusicViewHolder holder, int position) {
        Music item=musicList.get(position);

        if(toolBarMode==LocalMusicActivity.TOOLBAR_MODE_MULTIPLE_CHOICE){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.menu.setVisibility(View.GONE);
        }else{
            holder.checkBox.setVisibility(View.GONE);
            holder.menu.setVisibility(View.VISIBLE);
        }

        holder.musicName.setText(item.getMusicName());
        holder.musicSinger.setText(item.getSingerName());
        holder.checkBox.setChecked(selected[position]);
    }

    /**
     * 创建点击某一项菜单后弹出的popWindow所需要的contentView
     * @return         spw的contentView
     */
    private View createContextViewInfo() {
        View view=LayoutInflater.from(activity).inflate(R.layout.pw_music_info, (ViewGroup) activity.getWindow().getDecorView(),false);

        //歌曲信息的“修改”被点击时
        view.findViewById(R.id.change_music_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        musicInfoMusicName=view.findViewById(R.id.music_info_music_name);
        musicInfoSingerName=view.findViewById(R.id.music_info_singer_name);
        musicInfoFileName=view.findViewById(R.id.music_info_file_name);
        musicInfoPlayLength=view.findViewById(R.id.music_info_file_play_length);
        musicInfoFileSize=view.findViewById(R.id.music_info_file_size);
        musicInfoFilePath=view.findViewById(R.id.music_info_file_path);
        return view;
    }

    /**
     * 创建点击某一项菜单后再继续点击“查看歌曲信息”时弹出的popWindow所需要的contentView
     * @return         spw的contentView
     */
    private View createContextViewMenu() {
        View view=LayoutInflater.from(activity).inflate(R.layout.pw_local_music_item_menu, (ViewGroup) activity.getWindow().getDecorView(),false);

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
                spwAddToPlaylist.showPopupWindow();
            }
        });

        //菜单中查看歌曲信息被点击时
        view.findViewById(R.id.item_menu_music_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Music item=musicList.get(popupPosition);

                musicInfoMusicName.setText(item.getMusicName());
                musicInfoSingerName.setText(item.getSingerName());

                LogUtil.printLog(item.getFilePath());
                int index=item.getFilePath().lastIndexOf("/");
                musicInfoFilePath.setText(item.getFilePath().substring(0,index));

                musicInfoFileName.setText(item.getFilePath().substring(index+1));

                long fileSize=item.getFileSize();
                double format=(double)fileSize/(double)1024/(double)1024;
                musicInfoFileSize.setText(String.format("%.2f",format)+"M");

                int duration=item.getDuration();
                musicInfoPlayLength.setText(FormatUtil.secondToString(duration));
                spwMusicMenu.dismiss();
                spwMusicInfo.showPopupWindow();

                //有可能位置不够，长按显示全部内容
                View.OnLongClickListener listener=new View.OnLongClickListener(){
                    @Override
                    public boolean onLongClick(View v) {
                        TextView tv= (TextView) v;
                        Toast.makeText(activity, tv.getText().toString(), Toast.LENGTH_LONG).show();
                        return true;
                    }
                };
                musicInfoMusicName.setOnLongClickListener(listener);
                musicInfoSingerName.setOnLongClickListener(listener);
                musicInfoFilePath.setOnLongClickListener(listener);
                musicInfoFileName.setOnLongClickListener(listener);
                musicInfoFileSize.setOnLongClickListener(listener);
                musicInfoPlayLength.setOnLongClickListener(listener);
            }
        });

        //菜单中删除被点击时
        view.findViewById(R.id.item_menu_delete_music).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(popupPosition);
                spwMusicMenu.dismiss();
            }
        });

        musicMenuMusicName=view.findViewById(R.id.music_menu_music_name);
        return view;
    }

    public void showDeleteDialog(final int... position) {
        View view=LayoutInflater.from(activity).inflate(R.layout.alertdialog_delete_local_music, (ViewGroup) activity.getWindow().getDecorView(),false);
        final CheckBox cb=view.findViewById(R.id.checkbox);

        AlertDialog dialog=new AlertDialog.Builder(activity)
                .setTitle("确定从本地音乐列表中删除本音乐吗")
                .setView(view)
                .setNegativeButton("取消",null)
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(position.length>0){
                            Message msg=new Message();
                            msg.what=LocalMusicActivity.MESSAGE_DELETE_MUSIC;
                            msg.obj=musicList.get(position[0]).getMd5();
                            handler.sendMessage(msg);

                            if(cb.isChecked()){
                                File file=new File(musicList.get(position[0]).getFilePath());
                                if(file.exists()){
                                    file.delete();
                                }else{
                                    Toast.makeText(activity, "文件不存在，所以仅删除本地音乐列表中的这首歌", Toast.LENGTH_SHORT).show();
                                }
                            }
                            musicList.remove(position);
                            notifyDataSetChanged();
                        }else{
                            List<Music> newData=new ArrayList<>();
                            for(int i=0;i<musicList.size();i++){
                                if(selected[i]){
                                    Music item=musicList.get(i);

                                    Message msg=new Message();
                                    msg.what=LocalMusicActivity.MESSAGE_DELETE_MUSIC;
                                    msg.obj=item.getMd5();
                                    handler.sendMessage(msg);

                                    if(cb.isChecked()){
                                        File file=new File(item.getFilePath());
                                        if(file.exists()){
                                            file.delete();
                                        }else{
                                            Toast.makeText(activity, "文件不存在，所以仅删除本地音乐列表中的这首歌", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }else{
                                    newData.add(musicList.get(i));
                                }
                            }
                            setMusicList(newData);
                            notifyDataSetChanged();
                            activity.setTitle("已选择"+selectedCount()+"项");
                        }
                    }
                })
                .create();

        dialog.getWindow().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.layout_card_2));
        dialog.show();
    }

    /**
     * 创建弹出收藏到某个歌单时弹出的popWindow的contentView
     * @return          contentView
     */
    private View createContentViewAddToPlaylist(){
        View view=LayoutInflater.from(activity).inflate(R.layout.pw_add_to_playlist, (ViewGroup) activity.getWindow().getDecorView(),false);
        view.getLayoutParams().height= (int) (DensityUtil.getDisplayHeight(activity)*0.6);
        view.getLayoutParams().width=(int) (DensityUtil.getDisplayWidth(activity)*0.9);

        RecyclerView playlist=view.findViewById(R.id.add_to_playlist_recycler_view);
        playlist.setLayoutManager(new LinearLayoutManager(activity));
        playlist.setAdapter(new PlayListAdapter(activity));
        return view;
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    class LocalMusicViewHolder extends RecyclerView.ViewHolder{

        View itemView;
        ImageView menu;
        TextView musicName;
        TextView musicSinger;
        CheckBox checkBox;         //多选时的多选框

        LocalMusicViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView=itemView;
            menu=itemView.findViewById(R.id.recycler_view_local_music_menu);
            musicName=itemView.findViewById(R.id.recycler_view_item_music_name);
            musicSinger=itemView.findViewById(R.id.music_singer);
            checkBox=itemView.findViewById(R.id.local_music_choose);

            //点击整个itemView时
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(toolBarMode==LocalMusicActivity.TOOLBAR_MODE_MULTIPLE_CHOICE){
                        checkBox.setChecked(!selected[getAdapterPosition()]);
                    }else{
                        activity.getBinder().playMusic(musicList.get(getAdapterPosition()).getMd5());
                    }
                }
            });

            //每一项中的菜单被点击时
            menu.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    //给所有的popWindow的textView赋值
                    musicMenuMusicName.setText("歌曲："+musicList.get(getAdapterPosition()).getMusicName());
                    spwMusicMenu.showPopupWindow();
                    popupPosition=getAdapterPosition();
                }
            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    selected[getAdapterPosition()]=isChecked;
                    activity.invalidateOptionsMenu();
                    activity.setTitle("已选择"+selectedCount()+"项");
                }
            });
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

    /**
     * 检查是否全部选中
     * @return true->全部选中  false->没有全部选中
     */
    public boolean checkAllSelect(){
        int len=musicList.size();
        for(int i=0;i<len;i++){
            if(!selected[i])
                return false;
        }
        return true;
    }

    /**
     * 选中全部
     */
    public void selectAll(){
        int len=musicList.size();
        for(int i=0;i<len;i++){
            selected[i]=true;
        }
    }

    /**
     * 全部未选中
     */
    public void selectAllCancel(){
        int len=musicList.size();
        for(int i=0;i<len;i++){
            selected[i]=false;
        }
    }

    /**
     * 处于选择状态的项的数量
     * @return
     */
    public int selectedCount(){
        int cnt=0;
        for(boolean b:selected){
            if(b)
                cnt++;
        }
        return cnt;
    }

    /**
     * 重新设置recyclerView的data列表
     * @param musicList
     */
    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
        selected=new boolean[musicList.size()+10];
    }
}
