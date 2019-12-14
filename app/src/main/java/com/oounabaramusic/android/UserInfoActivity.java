package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.oounabaramusic.android.fragment.MainMyFragment;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private List<Fragment> fragments;
    private View slide;
    private TextView userName;
    private RelativeLayout header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
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

        userName=findViewById(R.id.user_name);
        header=findViewById(R.id.user_info_header);
        slide=findViewById(R.id.slide);
        slide.getLayoutParams().width= DensityUtil.getDisplayWidth(this)/2;

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
                    LogUtil.printLog("Mogeko","11111");
                    header.setVisibility(View.VISIBLE);
                }
            }
        });


        fragments=new ArrayList<>();

        viewPager=findViewById(R.id.user_view_pager);
        viewPager.setAdapter(new UserFragmentPagerAdapter());
        viewPager.addOnPageChangeListener(new UserOnPagerChangeListener());


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
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

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
