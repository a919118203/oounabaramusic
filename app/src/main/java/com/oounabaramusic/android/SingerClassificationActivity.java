package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.oounabaramusic.android.adapter.SingerAdapter;
import com.oounabaramusic.android.anim.HeightChangeAnimation;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.Objects;

public class SingerClassificationActivity extends BaseActivity {

    private SingerAdapter adapter;
    private GridLayout gl;
    private LinearLayout filter;
    private HeightChangeAnimation animation;
    private boolean f=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer_classification);
        StatusBarUtil.setWhiteStyleStatusBar(this);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        RecyclerView rv=findViewById(R.id.recycler_view);
        filter=findViewById(R.id.filter);
        gl=findViewById(R.id.grid_layout);
        animation=new HeightChangeAnimation(gl,filter);
        rv.setAdapter(adapter=new SingerAdapter(this));
        rv.setLayoutManager(new LinearLayoutManager(this));

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if(!f&&newState==RecyclerView.SCROLL_STATE_DRAGGING){
                    f=true;
                    animation.toV2();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void openFilter(View view){
        animation.toV1();
        f=false;
    }
}
