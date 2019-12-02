package com.oounabaramusic.android;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.oounabaramusic.android.fragment.MainMyFragment;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends BaseActivity implements View.OnClickListener{


    private TextView fragmentMainMy, fragmentMainSearch, fragmentMainNow, fragmentMainVideo;
    private ImageView mainSetting,mainSearch;
    private ViewPager viewPager;
    private List<Fragment> fragments=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.changeStatusBarContentColor(this);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        fragments.add(new MainMyFragment());
        viewPager=findViewById(R.id.main_view_pager);
        viewPager.setAdapter(new MainFragmentPagerAdapter());
        viewPager.addOnPageChangeListener(new MainOnPagerChangeListener());

        fragmentMainMy =findViewById(R.id.fragment_main_my);
        fragmentMainSearch =findViewById(R.id.fragment_main_search);
        fragmentMainNow =findViewById(R.id.fragment_main_now);
        fragmentMainVideo =findViewById(R.id.fragment_main_video);
        mainSetting=findViewById(R.id.main_setting);
        mainSearch=findViewById(R.id.main_search);

        fragmentMainMy.setOnClickListener(this);
        fragmentMainSearch.setOnClickListener(this);
        fragmentMainNow.setOnClickListener(this);
        fragmentMainVideo.setOnClickListener(this);
        mainSearch.setOnClickListener(this);
        mainSetting.setOnClickListener(this);

        findViewById(R.id.main_setting).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fragment_main_my:
                break;
            case R.id.fragment_main_search:
                break;
            case R.id.fragment_main_now:
                break;
            case R.id.fragment_main_video:
                break;
            case R.id.main_setting:
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
            LogUtil.printLog("MainActivity","OnPageScrolled");
        }

        @Override
        public void onPageSelected(int position) {
            LogUtil.printLog("MainActivity","onPageSelected");
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            LogUtil.printLog("MainActivity","onPageScrollStateChanged");
        }
    }
}
