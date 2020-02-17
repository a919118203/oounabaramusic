package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.fragment.BaseFragment;
import com.oounabaramusic.android.fragment.RPMusicFragment;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecentlyPlayedActivity extends BaseActivity {

    public static final int MODE_NORMAL=0;
    public static final int MODE_CHOICE=1;
    private int mode=MODE_NORMAL;
    private TabLayout tl;
    private ViewPager vp;
    private List<BaseFragment> fragments;
    private int userId;
    public MenuItem selectAll,cancelSelectAll;
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
        fragments.add(new RPMusicFragment(this));

        vp=findViewById(R.id.view_pager);
        tl=findViewById(R.id.tab_layout);

        vp.setAdapter(new ViewPagerAdapter());
        tl.setupWithViewPager(vp);

        initContent();
    }

    private void initContent(){
        userId = Integer.valueOf(sp.getString("userId","-1"));
    }

    public int getUserId() {
        return userId;
    }

    public void setTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    public void switchMode(int mode){
        this.mode=mode;
        invalidateOptionsMenu();//重新加载menu

        String title="";
        switch (mode){
            case MODE_NORMAL:
                tl.setVisibility(View.VISIBLE);
                title="最近播放";
                break;
            case MODE_CHOICE:
                tl.setVisibility(View.GONE);
                title="已选择0项";
                break;
        }

        Objects.requireNonNull(getSupportActionBar()).setTitle(title);

        ((RPMusicFragment)fragments.get(0)).switchMode(mode);
    }



    class ViewPagerAdapter extends FragmentPagerAdapter{

        ViewPagerAdapter(){
            super(RecentlyPlayedActivity.this.getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
        getMenuInflater().inflate(R.menu.menu_rp_activity,menu);
        menu.getItem(0).setVisible(mode==MODE_NORMAL);
        menu.getItem(1).setVisible(mode==MODE_CHOICE);
        menu.getItem(2).setVisible(false);

        selectAll = menu.getItem(1);
        cancelSelectAll = menu.getItem(2);
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

            case R.id.clear:
                clearRPMusic();
                break;

            case R.id.select_all:
                ((RPMusicFragment)fragments.get(0)).getChoiceAdapter().selectAll();
                selectAll.setVisible(false);
                cancelSelectAll.setVisible(true);
                break;

            case R.id.cancel_select_all:
                ((RPMusicFragment)fragments.get(0)).getChoiceAdapter().clearSelected();
                selectAll.setVisible(true);
                cancelSelectAll.setVisible(false);
                break;
        }
        return true;
    }

    private void clearRPMusic(){
        AlertDialog dialog=new AlertDialog.Builder(this)
                .setTitle("确定要清空最近播放的音乐记录")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<Music> dataList = ((RPMusicFragment)fragments.get(0)).getNormalAdapter().getDataList();

                        List<Integer> jsonData = new ArrayList<>();
                        jsonData.add(getUserId());
                        for(Music item:dataList){
                            jsonData.add(item.getId());
                        }
                        new S2SHttpUtil(
                                RecentlyPlayedActivity.this,
                                new Gson().toJson(jsonData),
                                MyEnvironment.serverBasePath+"music/deleteRPMusic",
                                new MyHandler(RecentlyPlayedActivity.this))
                                .call(BasicCode.DELETE_RP_MUSIC_END);
                    }
                })
                .create();

        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.layout_card_2));
        dialog.show();
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

    static class MyHandler extends Handler{
        RecentlyPlayedActivity activity;
        MyHandler(RecentlyPlayedActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.DELETE_RP_MUSIC_END:
                    ((RPMusicFragment)activity.fragments.get(0))
                            .getNormalAdapter()
                            .initContent();
                    break;
            }
        }
    }
}
