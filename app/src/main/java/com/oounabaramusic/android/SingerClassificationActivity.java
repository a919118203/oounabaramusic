package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.adapter.SingerAdapter;
import com.oounabaramusic.android.anim.HeightChangeAnimation;
import com.oounabaramusic.android.bean.Singer;
import com.oounabaramusic.android.bean.SingerCountry;
import com.oounabaramusic.android.bean.SingerType;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SingerClassificationActivity extends BaseActivity {

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

    private boolean end;

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

        new S2SHttpUtil(
                this,
                "",
                MyEnvironment.serverBasePath+"music/loadSingerType",
                handler)
        .call(BasicCode.GET_CONTENT);
    }

    //返回时，刷新
    @Override
    protected void onRestart() {
        super.onRestart();

        if(countries==null){
            return;
        }
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

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();

                if(llm.findLastVisibleItemPosition()==adapter.getItemCount()-1){
                    loadNextSinger();
                }
            }
        });

        loadSinger();
    }

    private void loadSinger(){
        int userId=0;

        if(SharedPreferencesUtil.isLogin(sp)){
            userId=SharedPreferencesUtil.getUserId(sp);
        }

        rv.setVisibility(View.GONE);
        recycler.setVisibility(View.VISIBLE);

        Singer singer = new Singer();
        singer.setCountryId(countries.get(country.getSelectedTabPosition()).getId());
        singer.setTypeId(types.get(type.getSelectedTabPosition()).getId());
        singer.setMainUserId(userId);
        singer.setStart(0);
        new S2SHttpUtil(
                this,
                gson.toJson(singer),
                MyEnvironment.serverBasePath+"music/loadSinger",
                handler)
        .call(BasicCode.GET_CONTENT_2);
    }

    private void loadNextSinger(){
        if(end){
            return;
        }

        int userId=0;

        if(SharedPreferencesUtil.isLogin(sp)){
            userId=SharedPreferencesUtil.getUserId(sp);
        }

        Singer singer = new Singer();
        singer.setCountryId(countries.get(country.getSelectedTabPosition()).getId());
        singer.setTypeId(types.get(type.getSelectedTabPosition()).getId());
        singer.setMainUserId(userId);
        singer.setStart(adapter.getItemCount());
        new S2SHttpUtil(
                this,
                gson.toJson(singer),
                MyEnvironment.serverBasePath+"music/loadSinger",
                handler)
                .call(BasicCode.GET_CONTENT_3);
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
                case BasicCode.GET_CONTENT:
                    json= (String) msg.obj;
                    data=activity.gson.fromJson(json,
                            new TypeToken<Map<String, String>>(){}.getType());
                    activity.initSingerTag(data);
                    break;
                case BasicCode.GET_CONTENT_2:
                    activity.rv.setVisibility(View.VISIBLE);
                    activity.recycler.setVisibility(View.GONE);
                    json= (String) msg.obj;

                    List<Singer> singers=activity.gson.fromJson(json,
                            new TypeToken<List<Singer>>(){}.getType());

                    activity.adapter.setDataList(singers);

                    activity.end=false;
                    break;

                case BasicCode.GET_CONTENT_3:
                    json= (String) msg.obj;

                    singers=activity.gson.fromJson(json,
                            new TypeToken<List<Singer>>(){}.getType());

                    if(singers.isEmpty()){
                        activity.end=true;
                    }

                    activity.adapter.addDataList(singers);

                    break;
                case BasicCode.TO_FOLLOW_SINGER:
                    activity.adapter.followSingerEnd();
            }
        }
    }
}
