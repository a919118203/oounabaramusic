package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.oounabaramusic.android.adapter.LyricsAdapter;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.ImageFilter;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.Arrays;

public class MusicPlayActivity extends BaseActivity implements View.OnClickListener{

    private LyricsAdapter adapter;
    private RecyclerView rv;
    private boolean activityMode=false;  //true->歌词   false->封面
    private FrameLayout musicCoverLayout,musicLyricsLayout;

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

        init();
    }

    private void init() {
        ImageView musicCover=findViewById(R.id.music_cover);
        musicCover.getLayoutParams().width= (int) (DensityUtil.getDisplayWidth(this)*0.8);
        musicCover.getLayoutParams().height= (int) (DensityUtil.getDisplayWidth(this)*0.8);
        Bitmap mc= ((BitmapDrawable)musicCover.getDrawable()).getBitmap();
        Bitmap blur= ImageFilter.blurBitmap(this,mc,20);

        ImageView background=findViewById(R.id.background);
        background.setImageBitmap(blur);


        musicLyricsLayout=findViewById(R.id.content_2);
        musicLyricsLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                musicLyricsLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                initLyrics(musicLyricsLayout.getHeight()/2-DensityUtil.dip2px(MusicPlayActivity.this,30));
                musicCoverLayout.setVisibility(View.VISIBLE);
                musicLyricsLayout.setVisibility(View.GONE);
                return true;
            }
        });

        musicLyricsLayout.setOnClickListener(this);
        musicCoverLayout=findViewById(R.id.content_1);
        musicCoverLayout.setOnClickListener(this);
    }

    private void initLyrics(int height) {
        rv=findViewById(R.id.lyrics);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter=new LyricsAdapter(this,height));
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int sum=0;      //用于判断线所处的歌词是哪一句
            private boolean f=false;//防止smoothScroll后无限套娃
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!f&&newState==RecyclerView.SCROLL_STATE_IDLE){
                    f=true;
                    int position=adapter.getCurrent();
                    int a=(adapter.getHeight(position)-adapter.getHeight(position-1))/2
                            +adapter.getHeight(position-1);
                    rv.smoothScrollBy(0,a-sum);
                }

                if(newState==RecyclerView.SCROLL_STATE_DRAGGING){
                    f=false;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                sum+=dy;
                final int current=adapter.search(sum);
                if(current!=adapter.getCurrent()){
                    adapter.setCurrent(current==0?1:current);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.content_1:
            case R.id.content_2:
                if(activityMode){
                    activityMode=false;
                    musicCoverLayout.setVisibility(View.VISIBLE);
                    musicLyricsLayout.setVisibility(View.GONE);
                }else{
                    activityMode=true;
                    musicLyricsLayout.setVisibility(View.VISIBLE);
                    musicCoverLayout.setVisibility(View.GONE);
                }
                break;
        }
    }
}
