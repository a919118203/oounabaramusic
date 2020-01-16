package com.oounabaramusic.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.oounabaramusic.android.fragment.MainMyFragment;
import com.oounabaramusic.android.fragment.MainNewDiscoveryFragment;
import com.oounabaramusic.android.fragment.MainNowFragment;
import com.oounabaramusic.android.fragment.MainVideoFragment;
import com.oounabaramusic.android.util.LinksTextViewAndViewPager;
import com.oounabaramusic.android.util.LogUtil;
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


    private ImageView mainSetting,mainSearch;
    private ViewPager viewPager;
    private DrawerLayout dl;
    private List<Fragment> fragments=new ArrayList<>();
    private LinksTextViewAndViewPager link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setTranslucentStatusAndDarkContent(this);
        StatusBarUtil.moveDownStatusBar(this);
        init();
    }

    private void init() {
        dl=findViewById(R.id.main_drawer_layout);
        mainSetting=findViewById(R.id.main_setting);
        mainSearch=findViewById(R.id.main_search);

        fragments.add(new MainMyFragment(this));
        fragments.add(new MainNewDiscoveryFragment(this,getSupportFragmentManager()));
        fragments.add(new MainNowFragment(this));
        fragments.add(new MainVideoFragment(this));

        viewPager=findViewById(R.id.main_view_pager);
        viewPager.setAdapter(new MainFragmentPagerAdapter());

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

        List<TextView> textViews=new ArrayList<>();
        textViews.add((TextView) findViewById(R.id.fragment_main_my));
        textViews.add((TextView) findViewById(R.id.fragment_main_new_discovery));
        textViews.add((TextView) findViewById(R.id.fragment_main_now));
        textViews.add((TextView) findViewById(R.id.fragment_main_video));
        link=new LinksTextViewAndViewPager(this,textViews,viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                link.select(position);
                if(position==1){
                    ((MainNewDiscoveryFragment)fragments.get(1)).setF(true);
                }else{
                    ((MainNewDiscoveryFragment)fragments.get(1)).setF(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.main_setting:                     //点击左上角菜单时
                dl.openDrawer(GravityCompat.START);
                break;
            case R.id.main_search:
                intent=new Intent(this,SearchActivity.class);
                startActivity(intent);
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
}
