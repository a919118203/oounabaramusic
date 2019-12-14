package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.oounabaramusic.android.adapter.MusicAdapter;
import com.oounabaramusic.android.util.LogUtil;
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
        StatusBarUtil.moveDownStatusBar(this);
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
        musicRv.setAdapter(adapter=new MusicAdapter(this, (FrameLayout) findViewById(R.id.rootLayout)));
        musicRv.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.play_all).setOnClickListener(this);
        findViewById(R.id.edit_play_list_info).setOnClickListener(this);
        findViewById(R.id.user).setOnClickListener(this);

        //初始化appBar，监听滑动时的事件
        appBarLayout=findViewById(R.id.appbar_toolbar);
        appBarLayout.setBackgroundColor(getResources().getColor(R.color.blue));
        appBarLayout.getBackground().mutate().setAlpha(0);
        final AppBarLayout appBar=findViewById(R.id.appbar);
        final TextView playListName=findViewById(R.id.playlist_name);
        final TextView title=findViewById(R.id.title);
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
                double p=((double)i/(double)sum)*255f;
                PlayListActivity.this.appBarLayout.getBackground().mutate().setAlpha((int)p);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play_list_menu,menu);
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
