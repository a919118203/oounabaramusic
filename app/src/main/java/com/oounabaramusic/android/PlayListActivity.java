package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.adapter.MusicAdapter;
import com.oounabaramusic.android.bean.Comment;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.PlayListHttpUtil;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.ImageFilter;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;
import com.oounabaramusic.android.widget.customview.MyImageView;
import com.oounabaramusic.android.widget.popupwindow.DownloadDialog;

import java.util.ArrayList;
import java.util.List;

public class PlayListActivity extends BaseActivity implements View.OnClickListener{

    private RecyclerView musicRv;
    private MusicAdapter adapter;
    private ProgressBar loadMusic;
    private AppBarLayout appBar;
    private PlayList playList;

    private TextView playListName,userName;
    private MyCircleImageView userHeader;
    private MyImageView playListCover,background;
    private TextView playListCnt;
    private TextView playListIntroduction;
    private ImageView collectionImage;

    private LinearLayout collectionLayout;

    private SharedPreferences sp;
    private PlayListHandler handler;

    private boolean isMyLove;     //是否是“我喜欢的音乐”歌单

    private Bitmap noCollect,collected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.moveDownStatusBar(this,R.id.header_content);
        StatusBarUtil.moveDownStatusBar(this,R.id.toolbar);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        sp= PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent=getIntent();
        playList= (PlayList) intent.getSerializableExtra("playList");
        isMyLove = intent.getBooleanExtra("isMyLove",false);

        init();

        loadMusic();
    }

    public PlayListHandler getHandler() {
        return handler;
    }

    public PlayList getPlayList() {
        return playList;
    }

    private void loadMusic(){
        loadMusic.setVisibility(View.VISIBLE);
        PlayListHttpUtil.findMusicByPlayList(this,
                playList.getId()+"",new PlayListHandler(this));
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        PlayListHttpUtil.findPlayList(this,""+playList.getId(),new PlayListHandler(this));
        loadMusic();
    }

    private void init() {
        handler=new PlayListHandler(this);

        //初始化音乐列表
        musicRv=findViewById(R.id.music_recycler_view);
        musicRv.setAdapter(adapter=new MusicAdapter(this));
        musicRv.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.play_all).setOnClickListener(this);
        findViewById(R.id.user).setOnClickListener(this);

        //初始化appBar，监听滑动时的事件
        appBar=findViewById(R.id.appbar);
        final TextView title=findViewById(R.id.title);
        final View view=findViewById(R.id.top_content);
        appBar.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            boolean f=false;//true->折叠状态，false->未折叠状态
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                i*=-1;
                if(!f&&i>=appBar.getTotalScrollRange()/2){
                    f=true;
                    title.setText(playListName.getText());
                }else if(f&&i<appBar.getTotalScrollRange()/2){
                    f=false;
                    title.setText("歌单");
                }
                int sum=appBar.getTotalScrollRange();
                double p=(double)i/(double)sum;
                view.setAlpha((float) (1f-p));
            }
        });

        playListCover=findViewById(R.id.playlist_cover);
        background = findViewById(R.id.background);
        userHeader = findViewById(R.id.user_header);
        playListName = findViewById(R.id.playlist_name);
        userName = findViewById(R.id.user_name);
        loadMusic=findViewById(R.id.load);
        playListIntroduction=findViewById(R.id.playlist_introduction);
        playListCnt=findViewById(R.id.playList_cnt);
        collectionImage=findViewById(R.id.collection_image);

        findViewById(R.id.download).setOnClickListener(this);
        findViewById(R.id.comment).setOnClickListener(this);
        collectionLayout = findViewById(R.id.collection);
        collectionLayout.setOnClickListener(this);

        noCollect= BitmapFactory.decodeResource(getResources(),R.mipmap.collection_before);
        collected= BitmapFactory.decodeResource(getResources(),R.mipmap.collection_after);

        initContent();
    }

    private void initContent(){

        //设置歌单内容
        playListName.setText(playList.getPlayListName());
        userName.setText(playList.getCreateUserName());
        playListCover.setEventHandler(new PlayListHandler(this));
        playListCover.setImageUrl(MyEnvironment.serverBasePath+
                "loadPlayListCover?playListId="+
                playList.getId());
        userHeader.setImageUrl(MyEnvironment.serverBasePath+
                "loadUserHeader?userId="+
                playList.getCreateUserId());
        playListIntroduction.setText(playList.getIntroduction());

        //获取用户是否已收藏该歌单
        //如果是自己的歌单就不显示收藏按钮
        if(sp.getBoolean("login",false)){
            if(sp.getString("userId","-1").equals(playList.getCreateUserId()+"")){
                collectionLayout.setVisibility(View.INVISIBLE);
            }else{
                int userId = SharedPreferencesUtil.getUserId(sp);
                int playListId = playList.getId();

                List<Integer> data = new ArrayList<>();
                data.add(userId);
                data.add(playListId);
                new S2SHttpUtil(
                        this,
                        gson.toJson(data),
                        MyEnvironment.serverBasePath+"playListIsCollect",
                        new PlayListHandler(this))
                        .call(BasicCode.PLAY_LIST_IS_COLLECT);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_play_list,menu);

        boolean f=false;
        if(sp.getBoolean("login",false)){
            if(sp.getString("userId","-1").equals(playList.getCreateUserId()+"")){
                if(isMyLove){
                    f=false;
                }else{
                    f=true;
                }
            }
        }

        menu.getItem(0).setVisible(f);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.edit_playlist_info:
                Intent intent;
                intent=new Intent(this,EditPlayListInfoActivity.class);
                intent.putExtra("playList",playList);
                startActivity(intent);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.play_all:      //播放全部
                List<Music> dataList=adapter.getDataList();
                for(int i=0;i<dataList.size();i++){
                    if(i==0){
                        getBinder().playMusic(dataList.get(i));
                    }else{
                        getBinder().nextPlay(dataList.get(i));
                    }
                }
                break;

            case R.id.user:       //点击用户名
                intent=new Intent(this,UserInfoActivity.class);
                intent.putExtra("userId",playList.getCreateUserId());
                startActivity(intent);
                break;

            case R.id.download:
                new DownloadDialog(this,adapter.getDataList());
                break;

            case R.id.comment:
                CommentActivity.startActivity(this,playList.getId(),Comment.PLAY_LIST);
                break;

            case R.id.collection:
                if(!SharedPreferencesUtil.isLogin(sp)){
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }

                int userId = Integer.valueOf(sp.getString("userId","-1"));
                int playListId = playList.getId();

                List<Integer> data = new ArrayList<>();
                data.add(userId);
                data.add(playListId);

                new S2SHttpUtil(
                        this,
                        gson.toJson(data),
                        MyEnvironment.serverBasePath+"collectPlayList",
                        new PlayListHandler(this))
                .call(BasicCode.COLLECT_PLAY_LIST);
                break;
        }
    }

    static class PlayListHandler extends Handler{
        PlayListActivity activity;
        PlayListHandler(PlayListActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MyCircleImageView.LOAD_END:
                    Bitmap bm=((BitmapDrawable)activity.playListCover.getDrawable()).getBitmap();
                    Bitmap newBackground= ImageFilter.blurBitmap(activity,bm,25);
                    activity.background.setImageBitmap(newBackground);
                    break;
                case PlayListHttpUtil.MESSAGE_FIND_PLAY_LIST_END:
                    activity.playList=activity.gson.fromJson((String)msg.obj,
                            new TypeToken<PlayList>(){}.getType());
                    activity.initContent();
                    break;
                case PlayListHttpUtil.MESSAGE_FIND_PLAY_LIST_MUSIC_END:
                    List<String> data=activity.gson.fromJson((String)msg.obj,
                            new TypeToken<List<String>>(){}.getType());

                    List<Music> result=new ArrayList<>();

                    for(String item:data){
                        result.add(new Music(item));
                    }
                    activity.playListCnt.setText(result.size()+"");
                    activity.adapter.setDataList(result);
                    activity.loadMusic.setVisibility(View.GONE);
                    activity.musicRv.setVisibility(View.VISIBLE);
                    break;
                case PlayListHttpUtil.MESSAGE_DELETE_MUSIC_END:
                    activity.loadMusic();
                    break;

                case BasicCode.COLLECT_PLAY_LIST:
                case BasicCode.PLAY_LIST_IS_COLLECT:
                    int isCollect = Integer.valueOf((String) msg.obj);
                    if(isCollect>0){
                        activity.collectionImage.setImageBitmap(activity.collected);
                    }else{
                        activity.collectionImage.setImageBitmap(activity.noCollect);
                    }
                    break;
            }
        }
    }
}
