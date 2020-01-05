package com.oounabaramusic.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.oounabaramusic.android.fragment.MainMyFragment;
import com.oounabaramusic.android.fragment.PrivateMessageFragment;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends BaseActivity implements View.OnClickListener{


    private TextView fragmentMainMy, fragmentMainSearch, fragmentMainNow, fragmentMainVideo;
    private ImageView mainSetting,mainSearch;
    private ViewPager viewPager;
    private DrawerLayout dl;
    private List<Fragment> fragments=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.changeStatusBarContentColor(this);
        StatusBarUtil.moveDownStatusBar(this);
        init();
    }

    private void init() {
        dl=findViewById(R.id.main_drawer_layout);
        fragmentMainMy =findViewById(R.id.fragment_main_my);
        fragmentMainSearch =findViewById(R.id.fragment_main_search);
        fragmentMainNow =findViewById(R.id.fragment_main_now);
        fragmentMainVideo =findViewById(R.id.fragment_main_video);
        mainSetting=findViewById(R.id.main_setting);
        mainSearch=findViewById(R.id.main_search);

        fragments.add(new MainMyFragment(this));
        viewPager=findViewById(R.id.main_view_pager);
        viewPager.setAdapter(new MainFragmentPagerAdapter());
        viewPager.addOnPageChangeListener(new MainOnPagerChangeListener());

        fragmentMainMy.setOnClickListener(this);
        fragmentMainSearch.setOnClickListener(this);
        fragmentMainNow.setOnClickListener(this);
        fragmentMainVideo.setOnClickListener(this);
        mainSearch.setOnClickListener(this);
        mainSetting.setOnClickListener(this);
        NavigationView view=findViewById(R.id.navigation_view);
        //view.findViewById(R.id.to_user_info).setOnClickListener(this);  //找不到该ID
        view.getHeaderView(0).setOnClickListener(this);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;
                switch (menuItem.getItemId()){
                    case R.id.my_friends:
                        intent=new Intent(MainActivity.this,MyFriendActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.my_news:
                        intent=new Intent(MainActivity.this, MyMessageActivity.class);
                        startActivity(intent);
                        break;
                }
                dl.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        findViewById(R.id.main_setting).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.fragment_main_my:
                break;
            case R.id.fragment_main_search:
                break;
            case R.id.fragment_main_now:
                break;
            case R.id.fragment_main_video:
                break;
            case R.id.main_setting:                     //点击左上角菜单时
                dl.openDrawer(GravityCompat.START);
                break;
            case R.id.to_user_info:                     //点击NavigationView的header部分时
                intent=new Intent(this,UserInfoActivity.class);
                intent.putExtra("mode",UserInfoActivity.MODE_SELF);
                startActivity(intent);
                dl.closeDrawer(GravityCompat.START);
                break;
        }
    }

    private class MainFragmentPagerAdapter extends FragmentPagerAdapter {

        MainFragmentPagerAdapter() {
            super(MainActivity.this.getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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

    private class MainOnPagerChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
