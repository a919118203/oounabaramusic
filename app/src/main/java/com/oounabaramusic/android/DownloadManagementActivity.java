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
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.dao.LocalMusicDao;
import com.oounabaramusic.android.fragment.BaseFragment;
import com.oounabaramusic.android.fragment.DMMusicFragment;
import com.oounabaramusic.android.fragment.DMDownloadingFragment;
import com.oounabaramusic.android.util.LogUtil;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_management);
        StatusBarUtil.setWhiteStyleStatusBar(this);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onDownloadBindOk() {
        init();
    }

    private void init() {
        fragments=new ArrayList<>();
        fragments.add(new DMMusicFragment(this));
        fragments.add(new DMDownloadingFragment(this));

        vp=findViewById(R.id.view_pager);
        tl=findViewById(R.id.tab_layout);

        vp.setAdapter(new ViewPagerAdapter());
        tl.setupWithViewPager(vp);
    }

    public void switchMode(int mode){
        this.mode=mode;

        String title="";
        switch (mode){
            case MODE_NORMAL:
                tl.setVisibility(View.VISIBLE);
                selectAll.setVisible(false);
                cancelSelectAll.setVisible(false);
                title="下载管理";
                break;
            case MODE_CHOICE:
                tl.setVisibility(View.GONE);
                selectAll.setVisible(true);
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

    private MenuItem selectAll,cancelSelectAll;

    public void setSelectAllVisible(boolean f){
        selectAll.setVisible(f);
    }

    public void setCancelSelectAll(boolean f){
        cancelSelectAll.setVisible(f);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dm_activity,menu);
        selectAll=menu.getItem(0);
        cancelSelectAll=menu.getItem(1);
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
            case R.id.select_all:
                ((DMMusicFragment)fragments.get(0)).getChoiceAdapter().selectAll();
                break;
            case R.id.cancel_select_all:
                ((DMMusicFragment)fragments.get(0)).getChoiceAdapter().cancelSelectAll();
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

    public LocalMusicDao getLocalMusicDao(){
        return localMusicDao;
    }
}
