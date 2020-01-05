package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.oounabaramusic.android.fragment.UserInfoMainFragment;
import com.oounabaramusic.android.fragment.UserInfoPostFragment;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.util.UserInfoActivityManager;
import com.oounabaramusic.android.widget.popupwindow.MyPopupWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener{

    public static final int MODE_SELF=1;
    public static final int MODE_OTHER=2;
    private int mode;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private List<Fragment> fragments;
    private TextView userName;
    private RelativeLayout header;
    private TabLayout tabLayout;
    private ImageView userHeader;//用户头像
    private TextView userFollowed;//粉丝
    private TextView userToFollow;//关注

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        UserInfoActivityManager.addActivity(this);
        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.moveDownStatusBar(this,R.id.toolbar);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        init();

    }

    private void init() {
        initMode();

        userFollowed=findViewById(R.id.user_followed);
        userToFollow=findViewById(R.id.user_to_follow);
        userHeader=findViewById(R.id.user_cover);
        userName=findViewById(R.id.user_name);
        header=findViewById(R.id.user_info_header);
        tabLayout=findViewById(R.id.user_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("主页"));
        tabLayout.addTab(tabLayout.newTab().setText("动态"));
        tabLayout.addOnTabSelectedListener(new OnTabChangeListener());

        userHeader.setOnClickListener(this);
        userFollowed.setOnClickListener(this);
        userToFollow.setOnClickListener(this);

        final AppBarLayout appBarLayout=findViewById(R.id.appbar);
        final View view=findViewById(R.id.background);
        final TextView title=findViewById(R.id.toolbar_title);
        view.getBackground().mutate().setAlpha(80);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {

            boolean f=false;//toolbar标题
            boolean f1=false; //header

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                i=Math.abs(i);
                double change=((double)i/(double) appBarLayout.getTotalScrollRange())*70f;

                if(!f&&change>69){
                    f=true;
                    title.setText(userName.getText());
                }else if(f&&change<=69){
                    f=false;
                    title.setText("");
                }

                if(!f1&&change>60){
                    f1=true;
                    header.setVisibility(View.GONE);
                }else if(f1&&change<=60){
                    f1=false;
                    header.setVisibility(View.VISIBLE);
                }
            }
        });


        fragments=new ArrayList<>();
        fragments.add(new UserInfoMainFragment(this));
        fragments.add(new UserInfoPostFragment(this));
        viewPager=findViewById(R.id.user_view_pager);
        viewPager.setAdapter(new UserFragmentPagerAdapter());
        viewPager.addOnPageChangeListener(new UserOnPagerChangeListener());


    }

    private void initMode() {
        Intent intent=getIntent();
        mode=intent.getIntExtra("mode",-1);
        if(mode==MODE_SELF){
            selfMode();
        }else if(mode==MODE_OTHER){
            otherMode();
        }
    }

    private void selfMode() {
        findViewById(R.id.user_follow).setVisibility(View.GONE);
        findViewById(R.id.user_send_message).setVisibility(View.GONE);
        TextView userEdit=findViewById(R.id.user_edit);
        userEdit.setOnClickListener(this);
    }

    private void otherMode() {
        findViewById(R.id.user_change_background).setVisibility(View.GONE);
        findViewById(R.id.user_edit).setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            UserInfoActivityManager.removeActivity(this);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.user_cover:             //点击头像时
                showChangeHeaderPopupWindow();
                break;
            case R.id.user_to_follow:         //点击关注时
                intent=new Intent(this,MyFriendActivity.class);
                intent.putExtra("from",MyFriendActivity.TO_FOLLOW);
                startActivity(intent);
                break;
            case R.id.user_followed:          //点击粉丝时
                intent=new Intent(this,MyFriendActivity.class);
                intent.putExtra("from",MyFriendActivity.FOLLOWED);
                startActivity(intent);
                break;
            case R.id.user_edit:              //点击编辑
                intent=new Intent(this,UserInfoEditActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void showChangeHeaderPopupWindow() {
        View contentView= LayoutInflater
                .from(this)
                .inflate(R.layout.popupwindow_change_header, (ViewGroup) getWindow().getDecorView(),false);

        ImageView iv=contentView.findViewById(R.id.user_header);
        iv.getLayoutParams().height=DensityUtil.getDisplayWidth(this);
        iv.setImageDrawable(userHeader.getDrawable());//TODO 以后得改

        final MyPopupWindow pw=new MyPopupWindow(this,
                contentView,
                Gravity.TOP);

        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.user_change_header){
                    openAlbum();
                }else{
                    pw.dismiss();
                }
            }
        };

        contentView.findViewById(R.id.user_change_header).setOnClickListener(listener);
        contentView.findViewById(R.id.user_header).setOnClickListener(listener);
        contentView.findViewById(R.id.rootLayout).setOnClickListener(listener);

        pw.showAtLocation(getWindow().getDecorView(), Gravity.CENTER,0,0);
    }

    class UserFragmentPagerAdapter extends FragmentPagerAdapter{

        UserFragmentPagerAdapter() {
            super(UserInfoActivity.this.getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
    }


    class UserOnPagerChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Objects.requireNonNull(tabLayout.getTabAt(position)).select();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class OnTabChangeListener implements TabLayout.BaseOnTabSelectedListener{

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int position = tab.getPosition();
            viewPager.setCurrentItem(position);
            if(position==0){
                ((UserInfoMainFragment)fragments.get(position)).resizeFocus();
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }
}
