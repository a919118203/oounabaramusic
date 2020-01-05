package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.oounabaramusic.android.adapter.MusicAdapter;
import com.oounabaramusic.android.util.ImageFilter;
import com.oounabaramusic.android.util.StatusBarUtil;

public class PlayListActivity extends BaseActivity implements View.OnClickListener{

    private RecyclerView musicRv;
    private MusicAdapter adapter;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.moveDownStatusBar(this,R.id.header_content);
        StatusBarUtil.moveDownStatusBar(this,R.id.appbar_toolbar);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        init();
    }

    private void init() {
        //初始化音乐列表
        musicRv=findViewById(R.id.music_recycler_view);
        musicRv.setAdapter(adapter=new MusicAdapter(this));
        musicRv.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.play_all).setOnClickListener(this);
        findViewById(R.id.edit_play_list_info).setOnClickListener(this);
        findViewById(R.id.user).setOnClickListener(this);

        //初始化appBar，监听滑动时的事件
        ImageView playListCover=findViewById(R.id.playlist_cover);
        Bitmap bm=((BitmapDrawable)playListCover.getDrawable()).getBitmap();
        Bitmap background= ImageFilter.blurBitmap(this,bm,25);
        ImageView playListBG = findViewById(R.id.background);
        playListBG.setImageBitmap(background);

        final AppBarLayout appBar=findViewById(R.id.appbar);
        final TextView playListName=findViewById(R.id.playlist_name);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_play_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.edit_play_list_info:  //编辑信息
                intent=new Intent(this,EditPlayListInfoActivity.class);
                startActivity(intent);
                break;

            case R.id.play_all:      //播放全部
                break;

            case R.id.user:       //点击用户名
                intent=new Intent(this,UserInfoActivity.class);
                startActivity(intent);
                break;
        }
    }
}
