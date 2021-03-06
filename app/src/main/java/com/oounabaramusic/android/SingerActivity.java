package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.Singer;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.fragment.BaseFragment;
import com.oounabaramusic.android.fragment.SingerMainFragment;
import com.oounabaramusic.android.fragment.SingerSongFragment;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.widget.customview.MyImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SingerActivity extends BaseActivity {

    private List<BaseFragment> fragments;
    private TabLayout tl;
    private ViewPager vp;
    private Singer singer;
    private int singerId;
    private boolean followed;

    private RelativeLayout headerLayout;
    private AppBarLayout appBarLayout;
    private LinearLayout toolbarContent;

    private TextView toolBarToFollow;
    private TextView toolBarFollowed;
    private TextView toolBarSingerName;
    private TextView tvToFollow,tvFollowed;
    private TextView fans,singerName;
    private MyImageView imageView;
    private View block;

    private SingerActivityHandler handler;
    private boolean initOk;

    public static void startActivity(Context context,int singerId){
        Intent intent = new Intent(context,SingerActivity.class);
        intent.putExtra("singerId",singerId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer);
        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.moveDownStatusBar(this,R.id.toolbar);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);


        handler=new SingerActivityHandler(this);
        initOk=false;

        Intent intent=getIntent();
        singerId=intent.getIntExtra("singerId",1);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initContent();
    }

    public void moreTo(int position){
        tl.getTabAt(position).select();
    }

    private void initSinger(){

        toolBarToFollow=findViewById(R.id.toolbar_to_follow);
        toolBarFollowed=findViewById(R.id.toolbar_followed);
        toolBarSingerName=findViewById(R.id.toolbar_singer_name);
        tvToFollow=findViewById(R.id.to_follow);
        tvFollowed=findViewById(R.id.followed);
        fans=findViewById(R.id.fans_cnt);
        singerName=findViewById(R.id.header_singer_name);
        imageView=findViewById(R.id.background);

        boolean login=sp.getBoolean("login",false);

        if(login){
            if(followed){
                toolBarToFollow.setVisibility(View.GONE);
                toolBarFollowed.setVisibility(View.VISIBLE);
                tvToFollow.setVisibility(View.GONE);
                tvFollowed.setVisibility(View.VISIBLE);
            }else{
                toolBarToFollow.setVisibility(View.VISIBLE);
                toolBarFollowed.setVisibility(View.GONE);
                tvToFollow.setVisibility(View.VISIBLE);
                tvFollowed.setVisibility(View.GONE);
            }
        }else{
            toolBarToFollow.setVisibility(View.GONE);
            toolBarFollowed.setVisibility(View.GONE);
            tvToFollow.setVisibility(View.GONE);
            tvFollowed.setVisibility(View.GONE);
        }

        toolBarSingerName.setText(singer.getSingerName());
        singerName.setText(singer.getSingerName());
        fans.setText(FormatUtil.numberToString(singer.getFans()));
        imageView.setImage(new MyImage(
                MyImage.TYPE_SINGER_COVER,singer.getId()));

        View.OnClickListener followListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.toolbar_to_follow:
                    case R.id.to_follow:

                        Singer data = new Singer();
                        data.setMainUserId(SharedPreferencesUtil.getUserId(sp));
                        data.setId(SingerActivity.this.singer.getId());

                        new S2SHttpUtil(
                                SingerActivity.this,
                                gson.toJson(data),
                                MyEnvironment.serverBasePath+"toFollowSinger",
                                handler)
                        .call(BasicCode.TO_FOLLOW_SINGER);

                        break;
                    case R.id.toolbar_followed:
                    case R.id.followed:
                        showCancelFollowDialog();
                        break;
                }
            }
        };

        toolBarToFollow.setOnClickListener(followListener);
        toolBarFollowed.setOnClickListener(followListener);
        tvToFollow.setOnClickListener(followListener);
        tvFollowed.setOnClickListener(followListener);

    }

    private void initContent(){
        int userId=0;
        if(SharedPreferencesUtil.isLogin(sp)){
            userId=SharedPreferencesUtil.getUserId(sp);
        }

        Singer singer = new Singer();
        singer.setMainUserId(userId);
        singer.setId(singerId);

        new S2SHttpUtil(
                this,
                gson.toJson(singer),
                MyEnvironment.serverBasePath+"music/loadSingerBySingerId",
                handler)
        .call(BasicCode.GET_CONTENT);
    }

    private void showCancelFollowDialog(){
        AlertDialog dialog=new AlertDialog.Builder(this)
                .setTitle("确定要取消关注吗？")
                .setNegativeButton("不",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Singer data = new Singer();
                        data.setId(singer.getId());
                        data.setMainUserId(SharedPreferencesUtil.getUserId(sp));

                        new S2SHttpUtil(
                                SingerActivity.this,
                                gson.toJson(data),
                                MyEnvironment.serverBasePath+"cancelFollowSinger",
                                handler)
                        .call(BasicCode.CANCEL_FOLLOW);
                    }
                })
                .create();

        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.layout_card_2));
        dialog.show();
    }

    public int getSingerId() {
        return singerId;
    }

    public Singer getSinger() {
        return singer;
    }

    private void init(){
        fragments=new ArrayList<>();
        fragments.add(new SingerMainFragment(this));
        fragments.add(new SingerSongFragment(this));

        tl=findViewById(R.id.tab_layout);
        vp=findViewById(R.id.view_pager);
        headerLayout=findViewById(R.id.header_layout);
        appBarLayout=findViewById(R.id.appbar);
        toolbarContent=findViewById(R.id.toolbar_content);
        block=findViewById(R.id.block);

        vp.setAdapter(new ViewPagerAdapter());
        tl.setupWithViewPager(vp);

        //监听appBar滑动，调节headerLayout内容隐藏
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            int total;

            int alphaStart=32,alphaEnd=200;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if(total==0)
                    total=appBarLayout.getTotalScrollRange();

                headerLayout.setAlpha(1f-Math.abs((float)i/(float)total));
                int alpha=alphaStart+(int)((alphaEnd-alphaStart)*(Math.abs((float)i/(float)total)));
                block.getBackground().mutate().setAlpha(alpha);

                if(total==Math.abs(i)){
                    toolbarContent.setVisibility(View.VISIBLE);
                }else{
                    toolbarContent.setVisibility(View.GONE);
                }
            }
        });

        //如果初始化完成才开始初始化歌手信息
        initOk=true;
        if(singer!=null){
            initSinger();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    static class SingerActivityHandler extends Handler{
        private SingerActivity activity;
        SingerActivityHandler(SingerActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.TO_FOLLOW_SINGER:
                    activity.toolBarToFollow.setVisibility(View.GONE);
                    activity.toolBarFollowed.setVisibility(View.VISIBLE);
                    activity.tvToFollow.setVisibility(View.GONE);
                    activity.tvFollowed.setVisibility(View.VISIBLE);
                    activity.followed=true;
                    break;
                case BasicCode.CANCEL_FOLLOW:
                    activity.toolBarToFollow.setVisibility(View.VISIBLE);
                    activity.toolBarFollowed.setVisibility(View.GONE);
                    activity.tvToFollow.setVisibility(View.VISIBLE);
                    activity.tvFollowed.setVisibility(View.GONE);
                    activity.followed=false;
                    break;
                case BasicCode.GET_CONTENT:
                    Map<String,String> json=activity.gson.fromJson(
                            (String)msg.obj,
                            new TypeToken<Map<String,String>>(){}.getType());

                    activity.singer=activity.gson.fromJson(json.get("singer"),
                            new TypeToken<Singer>(){}.getType());

                    //获取歌手信息后刷新 主页
                    ((SingerMainFragment)activity.fragments.get(0)).refresh();
                    String f=json.get("followed");
                    if(f!=null){
                        activity.followed=activity.gson.fromJson(f,
                                new TypeToken<Boolean>(){}.getType());
                    }

                    //如果初始化完成才开始初始化歌手信息
                    if(activity.initOk){
                        activity.initSinger();
                    }
                    break;
            }
        }
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter() {
            super(SingerActivity.this.getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
