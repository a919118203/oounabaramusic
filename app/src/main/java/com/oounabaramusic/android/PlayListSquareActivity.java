package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.bean.PlayListSmallTag;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.fragment.BaseFragment;
import com.oounabaramusic.android.fragment.MainNewDiscoveryFragment;
import com.oounabaramusic.android.fragment.PlayListFragment;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayListSquareActivity extends BaseActivity {

    private List<PlayListFragment> fragments;
    private List<PlayListSmallTag> tags;
    private int userId;

    private ViewPager vp;
    private TabLayout tl;
    private ViewPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_square);
        StatusBarUtil.setWhiteStyleStatusBar(this);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        fragments=new ArrayList<>();
        userId = Integer.valueOf(sp.getString("userId","-1"));

        Intent intent=getIntent();
        tags=(ArrayList<PlayListSmallTag>)intent.getSerializableExtra("tags");

        vp=findViewById(R.id.view_pager);
        tl=findViewById(R.id.tab_layout);
        vp.setAdapter(adapter = new ViewPagerAdapter());
        tl.setupWithViewPager(vp,true);;

        initContent();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new S2SHttpUtil(
                this,
                userId+"",
                MyEnvironment.serverBasePath+"getUserPlayListTag",
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT);
    }

    private void initContent(){
        int len=fragments.size();
        if(len>0){
            tl.getTabAt(0).select();
        }
        for(int i=0;i<tags.size();i++){

            PlayListSmallTag tag = tags.get(i);

            if(i<len){
                fragments.get(i).setTag(tag);
            }else{
                fragments.add(new PlayListFragment(this,tag));
            }
        }


        //不但要在数据源删除  还要在FragmentManager中删除缓存
        FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
        while(fragments.size()>tags.size()){
            ft.remove(fragments.remove(fragments.size()-1));
        }

        //提交
        ft.commit();
        getSupportFragmentManager().executePendingTransactions();

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_play_list_square,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.play_list_tag:
                Intent intent=new Intent(this,PlayListTagActivity.class);
                intent.putExtra("tags", (Serializable) tags);
                startActivity(intent);
                break;
        }
        return true;
    }

    static class MyHandler extends Handler{
        PlayListSquareActivity activity;
        MyHandler(PlayListSquareActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    ArrayList<PlayListSmallTag> tags = new Gson().fromJson((String)msg.obj,
                            new TypeToken<ArrayList<PlayListSmallTag>>(){}.getType());

                    activity.tags=tags;
                    activity.initContent();
                    break;
            }
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        public ViewPagerAdapter() {
            super(PlayListSquareActivity.this.getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).getTitle();
        }
    }
}
