package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.material.tabs.TabLayout;
import com.oounabaramusic.android.fragment.BaseFragment;
import com.oounabaramusic.android.fragment.DMMusicFragment;
import com.oounabaramusic.android.fragment.DMDownloadingFragment;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DownloadManagementActivity extends BaseActivity {

    public static final int MODE_NORMAL=0;
    public static final int MODE_CHOICE=1;
    private int mode=MODE_NORMAL;
    private TabLayout tl;
    private ViewPager vp;
    private List<BaseFragment> fragments;
    private RelativeLayout playControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently_played);
        StatusBarUtil.setWhiteStyleStatusBar(this);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        fragments=new ArrayList<>();
        fragments.add(new DMMusicFragment(this));
        fragments.add(new DMDownloadingFragment(this));

        vp=findViewById(R.id.view_pager);
        tl=findViewById(R.id.tab_layout);
        playControl=findViewById(R.id.tool_current_play_layout);

        vp.setAdapter(new ViewPagerAdapter());
        tl.setupWithViewPager(vp);
    }

    public void switchMode(int mode){
        this.mode=mode;
        invalidateOptionsMenu();//重新加载menu

        String title="";
        switch (mode){
            case MODE_NORMAL:
                tl.setVisibility(View.VISIBLE);
                playControl.setVisibility(View.VISIBLE);
                title="下载管理";
                break;
            case MODE_CHOICE:
                tl.setVisibility(View.GONE);
                playControl.setVisibility(View.GONE);
                title="已选择0项";
                break;
        }

        Objects.requireNonNull(getSupportActionBar()).setTitle(title);

        ((DMMusicFragment)fragments.get(0)).switchMode(mode);
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {

        ViewPagerAdapter(){
            super(DownloadManagementActivity.this.getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dm_activity,menu);
        menu.getItem(0).setVisible(mode==MODE_NORMAL);
        menu.getItem(1).setVisible(mode==MODE_CHOICE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(mode!=MODE_NORMAL){
                    switchMode(MODE_NORMAL);
                    break;
                }
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(mode!=MODE_NORMAL){
                switchMode(MODE_NORMAL);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
