package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.adapter.SingerAdapter;
import com.oounabaramusic.android.anim.HeightChangeAnimation;
import com.oounabaramusic.android.bean.Singer;
import com.oounabaramusic.android.bean.SingerCountry;
import com.oounabaramusic.android.bean.SingerType;
import com.oounabaramusic.android.okhttputil.SingerClassificationHttpUtil;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SingerClassificationActivity extends BaseActivity {

    public static final int MESSAGE_TYPE_LOAD_END=0;
    public static final int MESSAGE_LOAD_SINGER_END=1;
    public static final int MESSAGE_FOLLOW_SINGER_END=2;
    public static final int MESSAGE_CANCEL_FOLLOW_END=3;
    private SingerAdapter adapter;
    private LinearLayout tab;
    private LinearLayout filter;
    private TabLayout country,type;
    private TextView title_country,title_type;
    private HeightChangeAnimation animation;
    private RecyclerView rv;
    private boolean f=false;

    private ProgressBar tag,recycler;

    private SingerClassificationHandler handler;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer_classification);
        StatusBarUtil.setWhiteStyleStatusBar(this);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        handler=new SingerClassificationHandler(this);

        init();

        SingerClassificationHttpUtil.loadSingerType(this,handler);
    }

    //返回时，刷新
    @Override
    protected void onRestart() {
        super.onRestart();
        loadSinger();
    }

    private List<SingerCountry> countries;
    private List<SingerType> types;

    //从服务器获取歌手tag后开始以当前的tabLayout的tag搜索歌手
    private void initSingerTag(Map<String, String> data){
        countries=gson.fromJson(data.get("countries"),new TypeToken<List<SingerCountry>>(){}.getType());

        for(SingerCountry c:countries){
            country.addTab(country.newTab().setText(c.getCountry()));
        }

        types=gson.fromJson(data.get("types"),new TypeToken<List<SingerType>>(){}.getType());
        for(SingerType t:types){
            type.addTab(type.newTab().setText(t.getType()));
        }

        tag.setVisibility(View.GONE);
        tab.setVisibility(View.VISIBLE);

        country.addOnTabSelectedListener(new SingerTagSelectedListener());
        type.addOnTabSelectedListener(new SingerTagSelectedListener());

        title_country.setText(countries.get(country.getSelectedTabPosition()).getCountry());
        title_type.setText(types.get(type.getSelectedTabPosition()).getType());
        //加载完歌手tag后初始化tag收起动画
        animation=new HeightChangeAnimation(tab,filter);

        loadSinger();
    }

    private void loadSinger(){
        String userId;

        if(!sp.getBoolean("login",false)){
            userId="-1";
        }else{
            userId=sp.getString("userId","-1");
        }

        rv.setVisibility(View.GONE);
        recycler.setVisibility(View.VISIBLE);

        SingerClassificationHttpUtil.loadSinger(SingerClassificationActivity.this,
                countries.get(country.getSelectedTabPosition()).getId(),
                types.get(type.getSelectedTabPosition()).getId(),
                userId,
                handler);
    }

    private void init() {
        rv=findViewById(R.id.recycler_view);
        filter=findViewById(R.id.filter);
        tab=findViewById(R.id.tab);
        country=findViewById(R.id.country);
        type=findViewById(R.id.type);
        title_country=findViewById(R.id.title_country);
        title_type=findViewById(R.id.title_type);

        //转圈圈
        tag=findViewById(R.id.type_loading);
        recycler=findViewById(R.id.loading);

        rv.setAdapter(adapter=new SingerAdapter(this,handler));
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

        sp= PreferenceManager.getDefaultSharedPreferences(this);
    }

    private class SingerTagSelectedListener implements TabLayout.BaseOnTabSelectedListener{

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            title_country.setText(countries.get(country.getSelectedTabPosition()).getCountry());
            title_type.setText(types.get(type.getSelectedTabPosition()).getType());
            loadSinger();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
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

    private static class SingerClassificationHandler extends Handler{

        private SingerClassificationActivity activity;

        private SingerClassificationHandler(SingerClassificationActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            String json;
            Map<String, String> data;

            switch (msg.what){
                case MESSAGE_TYPE_LOAD_END:
                    json= (String) msg.obj;
                    data=activity.gson.fromJson(json,
                            new TypeToken<Map<String, String>>(){}.getType());
                    activity.initSingerTag(data);
                    break;
                case MESSAGE_LOAD_SINGER_END:
                    activity.rv.setVisibility(View.VISIBLE);
                    activity.recycler.setVisibility(View.GONE);
                    json= (String) msg.obj;

                    data=activity.gson.fromJson(json,
                            new TypeToken<Map<String,String>>(){}.getType());

                    List<Singer> singers=activity.gson.fromJson(data.get("singers"),
                            new TypeToken<List<Singer>>(){}.getType());

                    List<Integer> followed=activity.gson.fromJson(data.get("followed"),
                            new TypeToken<List<Integer>>(){}.getType());

                    activity.adapter.setFollowed(followed);
                    activity.adapter.setDataList(singers);
                    break;
                case MESSAGE_FOLLOW_SINGER_END:
                    activity.adapter.followSingerEnd((String) msg.obj);
            }
        }
    }
}
