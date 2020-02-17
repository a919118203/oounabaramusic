package com.oounabaramusic.android.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oounabaramusic.android.CommentActivity;
import com.oounabaramusic.android.LocalMusicActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Comment;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.service.MusicPlayService;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyImageView;
import com.oounabaramusic.android.widget.popupwindow.MyBottomSheetDialog;
import com.oounabaramusic.android.widget.popupwindow.PlayListDialog;
import com.oounabaramusic.android.widget.popupwindow.SingerDialog;

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

    private MyBottomSheetDialog spwMusicMenu;
    private MyBottomSheetDialog spwMusicMenuIsServer;
    private MyBottomSheetDialog spwMusicInfo;
    private MyBottomSheetDialog spwAddToPlaylist;
    private AlertDialog spwChangeInfo;
    private List<Music> musicList;
    private boolean[] selected;//多选模式时记录多选的情况
    private int toolBarMode;
    private LocalMusicActivity activity;
    private Handler handler;

    //本地歌曲菜单弹窗
    private TextView musicMenuMusicName;

    //歌曲信息弹窗
    private TextView musicInfoMusicName,musicInfoSingerName,musicInfoFileName,musicInfoPlayLength,musicInfoFileSize,musicInfoFilePath;
    private TextView change;

    //服务器音乐菜单弹窗
    private MyImageView serverMusicCover;
    private TextView serverMusicName,serverMusicSinger,serverMusicSinger2;

    //修改音乐信息
    private EditText musicName;
    private EditText musicSinger;

    //弹窗的下标
    private int popupPosition;

    public LocalMusicAdapter(final LocalMusicActivity activity, List<Music> data, Handler handler){
        this.activity=activity;
        this.handler=handler;
        musicList=data;
        selected=new boolean[data.size()+10];

        //初始化各种弹窗
        spwMusicMenu =new MyBottomSheetDialog(activity);
        spwMusicMenu.setContentView(createContentViewMenu());
        spwMusicInfo =new MyBottomSheetDialog(activity);
        spwMusicInfo.setContentView(createContentViewInfo());
        spwAddToPlaylist =new MyBottomSheetDialog(activity);
        spwAddToPlaylist.setContentView(createContentViewAddToPlaylist());
        spwMusicMenuIsServer=new MyBottomSheetDialog(activity);
        spwMusicMenuIsServer.setContentView(createContentViewIsServer());

        spwChangeInfo=new AlertDialog.Builder(activity)
                .setView(createContentViewChange())
                .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Music item=musicList.get(popupPosition);

                        //更新进数据库
                        activity.getLocalMusicDao().updateMusicNameByMd5(
                                musicName.getText().toString(),
                                musicSinger.getText().toString(),
                                item.getMd5());

                        //更新适配器的数据
                        item.setMusicName(musicName.getText().toString());
                        item.setSingerName(musicSinger.getText().toString());

                        //刷新所有控件
                        activity.refreshPlayBar();

                        //更新音乐信息弹窗的信息
                        musicInfoMusicName.setText(musicName.getText());
                        musicInfoSingerName.setText(musicSinger.getText());
                    }
                })
                .setNegativeButton("取消",null)
                .create();
        spwChangeInfo.getWindow().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.layout_card_2));
    }

    @NonNull
    @Override
    public LocalMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LocalMusicViewHolder vh=new LocalMusicViewHolder(
                LayoutInflater.from(activity).inflate(R.layout.rv_item_local_music,parent,false));
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull LocalMusicViewHolder holder, int position) {
        Music item=musicList.get(position);

        if(toolBarMode==LocalMusicActivity.TOOLBAR_MODE_MULTIPLE_CHOICE){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.menu.setVisibility(View.GONE);
            holder.isPlaying.setVisibility(View.GONE);
        }else{
            holder.checkBox.setVisibility(View.GONE);
            holder.menu.setVisibility(View.VISIBLE);

            if(activity.getBinder().getStatus()== MusicPlayService.IS_START||
                    activity.getBinder().getStatus()== MusicPlayService.IS_PAUSE){
                if(musicList.get(position).getMd5().equals(activity.getBinder().getCurrentMusic().getMd5())){
                    holder.isPlaying.setVisibility(View.VISIBLE);
                }else{
                    holder.isPlaying.setVisibility(View.GONE);
                }
            }else{
                holder.isPlaying.setVisibility(View.GONE);
            }
        }

        holder.musicName.setText(item.getMusicName());
        holder.musicSinger.setText(item.getSingerName());
        holder.checkBox.setChecked(selected[position]);
    }

    private View createContentViewChange(){
        View view=LayoutInflater.from(activity).inflate(R.layout.pw_change_music_info,
                (ViewGroup) activity.getWindow().getDecorView(),false);

        musicName=view.findViewById(R.id.music_name);
        musicSinger=view.findViewById(R.id.music_singer);
        return view;
    }

    /**
     * 点击某一项菜单后，如果这项是服务器音乐就弹这个view
     * @return
     */
    private View createContentViewIsServer(){
        View view=LayoutInflater
                .from(activity)
                .inflate(R.layout.pw_local_music_item_menu_is_server,
                        (ViewGroup) activity.getWindow().getDecorView(),false);

        serverMusicCover=view.findViewById(R.id.music_cover);
        serverMusicName=view.findViewById(R.id.music_name);
        serverMusicSinger=view.findViewById(R.id.music_singer_in_title);
        serverMusicSinger2=view.findViewById(R.id.music_singer);

        //点击下一首播放
        view.findViewById(R.id.music_next_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getBinder().nextPlay(musicList.get(popupPosition));
                spwMusicMenuIsServer.dismiss();
            }
        });

        //点击收藏到歌单
        view.findViewById(R.id.music_add_to_playlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.sp.getBoolean("login",false)){
                    int userId = Integer.valueOf(activity.sp.getString("userId","-1"));
                    new PlayListDialog(activity,userId,musicList.get(popupPosition).getMd5());
                }else{
                    Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
                }
                spwMusicMenuIsServer.dismiss();
            }
        });

        //点击评论
        view.findViewById(R.id.music_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new S2SHttpUtil(
                        activity,
                        musicList.get(popupPosition).getMd5(),
                        MyEnvironment.serverBasePath+"getMusicId",
                        new MyHandler(LocalMusicAdapter.this))
                .call(BasicCode.GET_MUSIC_ID);
                spwMusicMenuIsServer.dismiss();
            }
        });

        //点击歌手
        view.findViewById(R.id.music_search_singer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Music item = musicList.get(popupPosition);
                new SingerDialog(activity,item.getSingerName(),item.getSingerId());
                spwMusicMenuIsServer.dismiss();
            }
        });

        //点击查看歌曲信息
        view.findViewById(R.id.music_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMusicInfoToDialog();
                spwMusicInfo.show();
                spwMusicMenuIsServer.dismiss();
            }
        });

        //点击删除
        view.findViewById(R.id.music_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(popupPosition);
                spwMusicMenuIsServer.dismiss();
            }
        });
        return view;
    }


    /**
     * 创建点击某一项菜单后再继续点击“查看歌曲信息”时弹出的popWindow所需要的contentView
     * @return         spw的contentView
     */
    private View createContentViewInfo() {
        View view=LayoutInflater.from(activity).inflate(R.layout.pw_music_info, (ViewGroup) activity.getWindow().getDecorView(),false);

        //歌曲信息的“修改”被点击时
        view.findViewById(R.id.change_music_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicName.setText(musicInfoMusicName.getText());
                musicSinger.setText(musicInfoSingerName.getText());
                spwChangeInfo.show();
            }
        });

        change=view.findViewById(R.id.change_music_info);
        musicInfoMusicName=view.findViewById(R.id.music_info_music_name);
        musicInfoSingerName=view.findViewById(R.id.music_info_singer_name);
        musicInfoFileName=view.findViewById(R.id.music_info_file_name);
        musicInfoPlayLength=view.findViewById(R.id.music_info_file_play_length);
        musicInfoFileSize=view.findViewById(R.id.music_info_file_size);
        musicInfoFilePath=view.findViewById(R.id.music_info_file_path);

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
        return view;
    }

    /**
     * 创建点击某一项菜单后弹出的popWindow所需要的contentView
     * @return         spw的contentView
     */
    private View createContentViewMenu() {
        View view=LayoutInflater.from(activity).inflate(R.layout.pw_local_music_item_menu, (ViewGroup) activity.getWindow().getDecorView(),false);

        //菜单中下一首播放被点击时
        view.findViewById(R.id.item_menu_next_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getBinder().nextPlay(musicList.get(popupPosition));
                spwMusicMenu.dismiss();
            }
        });

        //菜单中查看歌曲信息被点击时
        view.findViewById(R.id.item_menu_music_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMusicInfoToDialog();
                spwMusicMenu.dismiss();
                spwMusicInfo.show();
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

    private void setMusicInfoToDialog(){
        Music item=musicList.get(popupPosition);

        musicInfoMusicName.setText(item.getMusicName());
        musicInfoSingerName.setText(item.getSingerName());

        int index=item.getFilePath().lastIndexOf("/");
        musicInfoFilePath.setText(item.getFilePath().substring(0,index));

        musicInfoFileName.setText(item.getFilePath().substring(index+1));

        long fileSize=item.getFileSize();
        double format=(double)fileSize/(double)1024/(double)1024;
        musicInfoFileSize.setText(String.format("%.2f",format)+"M");

        int duration=item.getDuration();
        musicInfoPlayLength.setText(FormatUtil.secondToString(duration));

        if(musicList.get(popupPosition).getIsServer()==1){
            change.setVisibility(View.GONE);
        }else{
            change.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示删除音乐dialog
     * @param position 如果长度为0 就说明是多选模式  不然就是普通模式一个一个地删
     */
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
                            musicList.remove(position[0]);
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
                            handler.sendEmptyMessage(LocalMusicActivity.MESSAGE_DELETE_MUSIC_END);
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
        ImageView menu,isPlaying;
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
            isPlaying=itemView.findViewById(R.id.is_playing);

            //点击整个itemView时
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(toolBarMode==LocalMusicActivity.TOOLBAR_MODE_MULTIPLE_CHOICE){
                        checkBox.setChecked(!selected[getAdapterPosition()]);
                    }else{
                        activity.getBinder().playMusic(musicList.get(getAdapterPosition()));
                    }
                }
            });

            //每一项中的菜单被点击时
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupPosition=getAdapterPosition();
                    Music item=musicList.get(popupPosition);

                    if(activity.getLocalMusicDao().isServer(item.getMd5())){
                        serverMusicCover.setImageUrl(
                                MyEnvironment.serverBasePath+"music/loadMusicCover?singerId="+item.getSingerId().split("/")[0]);
                        serverMusicName.setText(item.getMusicName());
                        serverMusicSinger.setText(item.getSingerName());
                        serverMusicSinger2.setText(item.getSingerName());
                        spwMusicMenuIsServer.show();
                    }else {
                        musicMenuMusicName.setText(item.getMusicName());
                        spwMusicMenu.show();
                    }
                }
            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(checkBox.getVisibility()==View.VISIBLE){
                        selected[getAdapterPosition()]=isChecked;
                        activity.invalidateOptionsMenu();
                        activity.setTitle("已选择"+selectedCount()+"项");
                    }
                }
            });
        }
    }

    static class MyHandler extends Handler{
        LocalMusicAdapter adapter;
        MyHandler(LocalMusicAdapter adapter){
            this.adapter=adapter;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_MUSIC_ID:
                    int musicId = Integer.valueOf((String)msg.obj);
                    CommentActivity.startActivity(adapter.activity,musicId,Comment.MUSIC);
                    break;
            }
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
     * 获取处于选中状态的项
     * @return
     */
    public List<Music> getSelected(){
        List<Music> result=new ArrayList<>();
        for(int i=0;i<musicList.size();i++){
            if(selected[i]){
                result.add(musicList.get(i));
            }
        }
        return result;
    }

    /**
     * 重新设置recyclerView的data列表
     * @param musicList
     */
    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
        selected=new boolean[musicList.size()+10];
    }

    /**
     * 返回data列表
     * @return
     */
    public List<Music> getMusicList(){
        return musicList;
    }
}
