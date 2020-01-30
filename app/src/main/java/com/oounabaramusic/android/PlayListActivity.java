package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.adapter.MusicAdapter;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.okhttputil.PlayListHttpUtil;
import com.oounabaramusic.android.util.ImageFilter;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;
import com.oounabaramusic.android.widget.customview.MyImageView;

public class PlayListActivity extends BaseActivity implements View.OnClickListener{

    private RecyclerView musicRv;
    private MusicAdapter adapter;
    private AppBarLayout appBar;
    private PlayList playList;

    private TextView playListName,userName;
    private MyCircleImageView userHeader;
    private MyImageView playListCover,background;
    private TextView playListIntroduction;
    private SharedPreferences sp;

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

        init();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        PlayListHttpUtil.findPlayList(this,""+playList.getId(),new PlayListHandler(this));
    }

    private void init() {
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
        playListIntroduction=findViewById(R.id.playlist_introduction);

        setPlayListInfo();
    }

    private void setPlayListInfo(){
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_play_list,menu);

        boolean f=false;
        if(sp.getBoolean("login",false)){
            if(sp.getString("userId","-1").equals(playList.getCreateUserId()+"")){
                f=true;
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
                break;

            case R.id.user:       //点击用户名
                intent=new Intent(this,UserInfoActivity.class);
                startActivity(intent);
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
                    activity.setPlayListInfo();
                    break;
            }
        }
    }
}
