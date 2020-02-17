package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.oounabaramusic.android.adapter.LyricsAdapter;
import com.oounabaramusic.android.bean.Comment;
import com.oounabaramusic.android.bean.Lrc;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.LrcHttpUtil;
import com.oounabaramusic.android.okhttputil.PlayListHttpUtil;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.service.MusicPlayService;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.ImageFilter;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.LrcUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;
import com.oounabaramusic.android.widget.customview.MyImageView;
import com.oounabaramusic.android.widget.popupwindow.MyBottomSheetDialog;
import com.oounabaramusic.android.widget.popupwindow.PlayListDialog;
import com.oounabaramusic.android.widget.popupwindow.SingerDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MusicPlayActivity extends BaseActivity implements View.OnClickListener{

    private LyricsAdapter adapter;
    private RecyclerView rv;
    private LinearLayout index;
    private TextView indexTime;
    private ImageView lrcRevision;
    private FrameLayout musicCoverLayout,musicLyricsLayout;
    private MusicPlayService.MusicPlayBinder binder;
    private Handler mHandler;
    private Music music;

    private MyCircleImageView musicCover;
    private ImageView background;
    private TextView currentProgress,maxProgress;
    private AppCompatSeekBar seekBar;
    private TextView musicName,singerName;
    private ImageView playMode;
    private ImageView previousMusic;
    private ImageView controlPlay;
    private ImageView nextMusic;
    private ImageView playList;

    private ImageView collection;
    private ImageView download;
    private ImageView comment;
    private ImageView info;

    private MyBottomSheetDialog musicMenu;
    private MyImageView serverMusicCover;
    private TextView serverMusicName;
    private TextView serverMusicSinger;
    private TextView serverMusicSinger2;

    private PopupWindow lrcMenu;


    private Bitmap list,random,single;
    private Bitmap start,stop;
    private Bitmap noCollection, collectioned;

    private boolean isCollection;   //这首歌是否被收藏
    private boolean tracking;       //seekBar  是否被拖动
    private boolean dragging =false;         //歌词是否被拖动
    private boolean activityMode=false;  //true->歌词   false->封面
    private int sum=0;      //用于判断线所处的歌词是哪一句
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.moveDownStatusBar(this);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onBindOk() {
        super.onBindOk();

        mHandler=new MyHandler(this);
        binder=getBinder();
        binder.addHandler(mHandler);

        init();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        music=binder.getCurrentMusic();
        initContent();
        loadLyrics();
        refreshPlayList();
    }

    @Override
    protected void onDestroy() {
        binder.removeHandler(mHandler);
        super.onDestroy();
    }

    private void init() {
        musicCover=findViewById(R.id.music_cover);
        background=findViewById(R.id.background);


        musicLyricsLayout=findViewById(R.id.content_2);
        musicLyricsLayout.post(new Runnable() {
            @Override
            public void run() {
                initLyrics(musicLyricsLayout.getHeight()/2-DensityUtil.dip2px(MusicPlayActivity.this,30));
                musicCoverLayout.setVisibility(View.VISIBLE);
                musicLyricsLayout.setVisibility(View.GONE);
            }
        });

        musicName=findViewById(R.id.music_name);
        singerName=findViewById(R.id.singer_name);
        musicCoverLayout=findViewById(R.id.content_1);
        currentProgress=findViewById(R.id.current_time);
        maxProgress=findViewById(R.id.total_time);
        seekBar=findViewById(R.id.seek_bar);
        playMode=findViewById(R.id.play_mode);
        previousMusic=findViewById(R.id.previous_music);
        controlPlay=findViewById(R.id.control_play);
        nextMusic=findViewById(R.id.next_music);
        playList=findViewById(R.id.play_list);
        rv=findViewById(R.id.lyrics);
        collection=findViewById(R.id.collection);
        download=findViewById(R.id.download);
        comment=findViewById(R.id.comment);
        info=findViewById(R.id.info);
        index=findViewById(R.id.index);
        indexTime=findViewById(R.id.index_time);
        lrcRevision=findViewById(R.id.lrc_revision);

        musicCoverLayout.setOnClickListener(this);
        musicLyricsLayout.setOnClickListener(this);
        playList.setOnClickListener(this);
        playMode.setOnClickListener(this);
        previousMusic.setOnClickListener(this);
        nextMusic.setOnClickListener(this);
        controlPlay.setOnClickListener(this);
        collection.setOnClickListener(this);
        download.setOnClickListener(this);
        comment.setOnClickListener(this);
        info.setOnClickListener(this);
        lrcRevision.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(tracking){
                    currentProgress.setText(FormatUtil.secondToString(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                tracking=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                binder.seekTo(seekBar.getProgress()*1000);
                tracking=false;
            }
        });

        list=BitmapFactory.decodeResource(getResources(),R.mipmap.list_loop_light);
        random=BitmapFactory.decodeResource(getResources(),R.mipmap.random_play_light);
        single=BitmapFactory.decodeResource(getResources(),R.mipmap.single_loop_light);
        start=BitmapFactory.decodeResource(getResources(),R.mipmap.play_button);
        stop=BitmapFactory.decodeResource(getResources(),R.mipmap.stop_button);
        noCollection=BitmapFactory.decodeResource(getResources(),R.mipmap.collection_before);
        collectioned =BitmapFactory.decodeResource(getResources(),R.mipmap.collection_after);

        initMusicMenu();

        initContent();

        initLrcMenu();
    }

    private void initLrcMenu() {
        View view=LayoutInflater.from(this).inflate(R.layout.pw_lrc_revision,
                (ViewGroup) getWindow().getDecorView(),false);

        view.findViewById(R.id.less).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MusicPlayActivity.this, "延迟歌词0.5S", Toast.LENGTH_SHORT).show();
                adapter.getLrc().setOffset(adapter.getLrc().getOffset()-500);
            }
        });

        view.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MusicPlayActivity.this, "提前歌词0.5S", Toast.LENGTH_SHORT).show();
                adapter.getLrc().setOffset(adapter.getLrc().getOffset()+500);
            }
        });

        view.findViewById(R.id.reduction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MusicPlayActivity.this, "已还原", Toast.LENGTH_SHORT).show();
                adapter.getLrc().setOffset(0);
            }
        });

        lrcMenu=new PopupWindow(view,ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lrcMenu.setFocusable(true);
        lrcMenu.setTouchable(true);
    }

    private void initMusicMenu(){
        musicMenu=new MyBottomSheetDialog(this);

        View view= LayoutInflater
                .from(this)
                .inflate(R.layout.pw_local_music_item_menu_is_server,
                        (ViewGroup) getWindow().getDecorView(),false);

        serverMusicCover=view.findViewById(R.id.music_cover);
        serverMusicName=view.findViewById(R.id.music_name);
        serverMusicSinger=view.findViewById(R.id.music_singer_in_title);
        serverMusicSinger2=view.findViewById(R.id.music_singer);

        //下一首播放
        view.findViewById(R.id.music_next_play).setVisibility(View.GONE);

        //点击收藏到歌单
        view.findViewById(R.id.music_add_to_playlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sp.getBoolean("login",false)){
                    String userId=sp.getString("userId","-1");
                    new PlayListDialog(MusicPlayActivity.this,
                            Integer.valueOf(userId),music.getMd5());

                }else{
                    Toast.makeText(MusicPlayActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                }

                musicMenu.dismiss();
            }
        });

        //点击评论
        view.findViewById(R.id.music_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sp.getBoolean("login",false)){

                    new S2SHttpUtil(
                            MusicPlayActivity.this,
                            music.getMd5(),
                            MyEnvironment.serverBasePath+"getMusicId",
                            mHandler)
                    .call(BasicCode.GET_MUSIC_ID);

                }else{
                    Toast.makeText(MusicPlayActivity.this,
                            "请先登录", Toast.LENGTH_SHORT).show();
                }
                musicMenu.dismiss();
            }
        });

        //点击歌手
        view.findViewById(R.id.music_search_singer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SingerDialog(MusicPlayActivity.this,
                        music.getSingerName(),music.getSingerId());
                musicMenu.dismiss();
            }
        });

        //点击查看歌曲信息
        view.findViewById(R.id.music_info).setVisibility(View.GONE);

        //点击删除
        view.findViewById(R.id.music_delete).setVisibility(View.GONE);

        musicMenu.setContentView(view);
    }

    private void initContent(){
        music=binder.getCurrentMusic();

        isCollection=false;
        if(music.getIsServer()==1){
            if(sp.getBoolean("login",false)&&music.getIsServer()==1){
                collection.setEnabled(false);
                List<String> data=new ArrayList<>();
                data.add(sp.getString("userId","-1"));
                data.add(music.getMd5());
                String url=MyEnvironment.serverBasePath+"musicIsCollection";

                new S2SHttpUtil(this,gson.toJson(data),url,mHandler)
                        .call(BasicCode.GET_IS_COLLECT_MUSIC_END);
            }

            info.setVisibility(View.VISIBLE);
        }else{
            info.setVisibility(View.INVISIBLE);
        }

        musicCover.getLayoutParams().width= (int) (DensityUtil.getDisplayWidth(this)*0.8);
        musicCover.getLayoutParams().height= (int) (DensityUtil.getDisplayWidth(this)*0.8);
        musicCover.setEventHandler(mHandler);
        musicCover.setImageUrl(MyEnvironment.serverBasePath+
                "music/loadMusicCover?singerId="+music.getSingerId().split("/")[0]);

        seekBar.setMax(music.getDuration());
        seekBar.setProgress(binder.getCurrentProgress()/1000);
        maxProgress.setText(FormatUtil.secondToString(music.getDuration()));
        currentProgress.setText(FormatUtil.secondToString(binder.getCurrentProgress()/1000));

        musicName.setText(music.getMusicName());
        singerName.setText(music.getSingerName().replace("/"," "));

        int loopType=binder.getCurrentLoopType();
        switch(loopType){
            case MusicPlayService.LOOP_TYPE_LIST:
                playMode.setImageBitmap(list);
                break;
            case MusicPlayService.LOOP_TYPE_RANDOM:
                playMode.setImageBitmap(random);
                break;
            case MusicPlayService.LOOP_TYPE_SINGLE:
                playMode.setImageBitmap(single);
                break;
        }

        if(binder.getStatus()==MusicPlayService.IS_START){
            controlPlay.setImageBitmap(stop);
        }else{
            controlPlay.setImageBitmap(start);
        }

        serverMusicCover.setImageUrl(MyEnvironment.serverBasePath+
                "music/loadMusicCover?singerId="+music.getSingerId().split("/")[0]);
        serverMusicName.setText(music.getMusicName());
        serverMusicSinger.setText(music.getSingerName().replace("/"," "));
        serverMusicSinger2.setText(music.getSingerName().replace("/"," "));
    }

    private void initLyrics(int height) {
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter=new LyricsAdapter(this,height));
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(dragging &&newState==RecyclerView.SCROLL_STATE_IDLE){
                    dragging =false;
                    index.setVisibility(View.GONE);
                    if(adapter.getItemCount()==0){
                        return ;
                    }
                    int position=adapter.getCurrent();
                    int a=(adapter.getHeight(position)-adapter.getHeight(position-1))/2
                            +adapter.getHeight(position-1);
                    rv.smoothScrollBy(0,a-sum);

                    long seek=adapter.getLrc().getTimes().get(position);
                    if(seek<Integer.MAX_VALUE){
                        binder.seekTo((int) seek);
                    }
                }

                if(newState==RecyclerView.SCROLL_STATE_DRAGGING){
                    dragging =true;
                    index.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                sum+=dy;
                int newIndex=adapter.search(sum);
                if(newIndex!=adapter.getCurrent()){
                    adapter.setCurrent(newIndex==0?1:newIndex);
                    indexTime.setText(FormatUtil.secondToString(adapter.getLrc().getTimes().get(newIndex)/1000));
                    adapter.notifyDataSetChanged();
                }
            }
        });

        loadLyrics();
    }

    private void changeLrcByTime(long time){
        int newIndex=adapter.getIndex(time);
        adapter.setCurrent(newIndex==0?1:newIndex);
        int a=(adapter.getHeight(newIndex)-adapter.getHeight(newIndex-1))/2
                +adapter.getHeight(newIndex-1);
        if(a==sum){
            return;
        }
        rv.smoothScrollBy(0,a-sum);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()){
            case R.id.content_1:
            case R.id.content_2:
                switchMode();
                break;

            case R.id.play_list:
                showPlayList();
                break;

            case R.id.play_mode:
                switch (binder.getCurrentLoopType()){
                    case MusicPlayService.LOOP_TYPE_RANDOM:
                        playMode.setImageBitmap(list);
                        binder.setCurrentLoopType(MusicPlayService.LOOP_TYPE_LIST);
                        break;
                    case MusicPlayService.LOOP_TYPE_LIST:
                        playMode.setImageBitmap(single);
                        binder.setCurrentLoopType(MusicPlayService.LOOP_TYPE_SINGLE);
                        break;
                    case MusicPlayService.LOOP_TYPE_SINGLE:
                        playMode.setImageBitmap(random);
                        binder.setCurrentLoopType(MusicPlayService.LOOP_TYPE_RANDOM);
                        break;
                }
                break;

            case R.id.control_play:
                if(binder.getStatus()==MusicPlayService.IS_START){
                    controlPlay.setImageBitmap(start);
                    binder.pauseMusic();
                }else{
                    controlPlay.setImageBitmap(stop);
                    binder.startMusic();
                }
                break;

            case R.id.previous_music:
                binder.playPrevious();
                break;

            case R.id.next_music:
                binder.playNext();
                break;

            case R.id.collection:
                collection.setEnabled(false);

                if(sp.getBoolean("login",false)){
                    if(music.getIsServer()==1){
                        String userId=sp.getString("userId","-1");
                        List<String> data=new ArrayList<>();
                        data.add(userId);
                        data.add(music.getMd5());

                        if(isCollection){
                            String url=MyEnvironment.serverBasePath+"cancelCollectionMusic";
                            new S2SHttpUtil(this,gson.toJson(data),url,mHandler)
                                    .call(BasicCode.CANCEL_COLLECTION_MUSIC_END);
                        }else{
                            PlayListHttpUtil.collectionMusic(this,gson.toJson(data),mHandler);
                        }

                    }else{
                        Toast.makeText(this, "无法收藏本地音乐", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.download:
                if(music.getIsServer()==1){
                    getDownloadBinder().addTask(music);
                }else{
                    Toast.makeText(this, "这是本地音乐", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.info:
                musicMenu.show();
                break;

            case R.id.comment:
                if(sp.getBoolean("login",false)){
                    if(music.getIsServer()==1){

                        //获取音乐ID
                        new S2SHttpUtil(
                                MusicPlayActivity.this,
                                music.getMd5(),
                                MyEnvironment.serverBasePath+"getMusicId",
                                mHandler)
                                .call(BasicCode.GET_MUSIC_ID);

                    }else{
                        Toast.makeText(this, "本地音乐没有评论", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.lrc_revision:
                int[] position = new int[2];
                lrcRevision.getLocationInWindow(position);
                LogUtil.printLog(Arrays.toString(position));
                lrcMenu.showAsDropDown(lrcRevision);
                break;

        }
    }

    public void switchMode(){
        if(activityMode){
            activityMode=false;
            musicCoverLayout.setVisibility(View.VISIBLE);
            musicLyricsLayout.setVisibility(View.GONE);
        }else{
            activityMode=true;
            musicCoverLayout.setVisibility(View.GONE);
            musicLyricsLayout.setVisibility(View.VISIBLE);
        }
    }

    private void loadLyrics(){

        //重置已滑动高度
        sum=0;

        File file=new File(MyEnvironment.musicLrc+music.getMd5()+".lrc");
        if(file.exists()){
            try {
                adapter.setLrc(LrcUtil.parseFileToLrc(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            new LrcHttpUtil(this,music.getMd5(),mHandler).call(BasicCode.DOWNLOAD_END);
        }
    }

    static class MyHandler extends Handler{
        MusicPlayActivity activity;
        MyHandler(MusicPlayActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MyCircleImageView.LOAD_END:
                    if(msg.obj!=null){
                        Bitmap mc= (Bitmap) msg.obj;
                        Bitmap blur= ImageFilter.blurBitmap(activity,mc,25);
                        activity.background.setImageBitmap(blur);
                    }
                    break;

                case MusicPlayService.EVENT_UPDATE_TIME:
                    if(!activity.tracking){
                        activity.currentProgress.setText(FormatUtil.secondToString(msg.arg1/1000));
                        activity.seekBar.setProgress(msg.arg1/1000);

                        if(activity.activityMode&&!activity.dragging){
                            activity.changeLrcByTime(msg.arg1);
                        }
                    }
                    break;

                case MusicPlayService.IS_PREPARE:
                    if(!activity.music.equals(activity.binder.getCurrentMusic())){
                        activity.music=activity.binder.getCurrentMusic();
                        activity.initContent();
                        activity.loadLyrics();
                        activity.refreshPlayList();
                    }else{
                        activity.controlPlay.setImageBitmap(activity.stop);
                    }
                    break;

                case MusicPlayService.EVENT_CHANGE_LOOP_TYPE:
                    int loopType=activity.binder.getCurrentLoopType();
                    switch(loopType){
                        case MusicPlayService.LOOP_TYPE_LIST:
                            activity.playMode.setImageBitmap(activity.list);
                            break;
                        case MusicPlayService.LOOP_TYPE_RANDOM:
                            activity.playMode.setImageBitmap(activity.random);
                            break;
                        case MusicPlayService.LOOP_TYPE_SINGLE:
                            activity.playMode.setImageBitmap(activity.single);
                            break;
                    }
                    break;

                case MusicPlayService.EVENT_DELETE_MUSIC:
                    if(activity.binder.getPlayList().size()==0){
                        activity.finish();
                    }
                    break;

                case PlayListHttpUtil.MESSAGE_COLLECTION_MUSIC_EDN:
                    activity.isCollection=true;
                    activity.collection.setImageBitmap(activity.collectioned);
                    activity.collection.setEnabled(true);
                    break;

                case BasicCode.CANCEL_COLLECTION_MUSIC_END:
                    activity.isCollection=false;
                    activity.collection.setImageBitmap(activity.noCollection);
                    activity.collection.setEnabled(true);
                    break;

                case BasicCode.GET_IS_COLLECT_MUSIC_END:
                    String isCollect= (String) msg.obj;
                    if(isCollect.equals("1")){
                        activity.isCollection=true;
                        activity.collection.setImageBitmap(activity.collectioned);
                    }else{
                        activity.isCollection=false;
                        activity.collection.setImageBitmap(activity.noCollection);
                    }

                    activity.collection.setEnabled(true);
                    break;

                case BasicCode.DOWNLOAD_END:
                    activity.loadLyrics();
                    break;

                case BasicCode.NO_LRC:
                    activity.adapter.setLrc(Lrc.getSimple("没有歌词"));
                    break;

                case BasicCode.GET_MUSIC_ID:
                    int musicId = Integer.valueOf((String)msg.obj);
                    CommentActivity.startActivity(activity,musicId,Comment.MUSIC);
                    break;
            }
        }
    }
}
