package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.oounabaramusic.android.fragment.ForwardFragment;
import com.oounabaramusic.android.fragment.GoodFragment;
import com.oounabaramusic.android.fragment.PostCommentFragment;
import com.oounabaramusic.android.fragment.UserInfoMainFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PostActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        init();
    }

    private void init() {
        fragments=new ArrayList<>();
        fragments.add(new PostCommentFragment(this));
        fragments.add(new ForwardFragment(this));
        fragments.add(new GoodFragment(this));

        tabLayout=findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("评论"));
        tabLayout.addTab(tabLayout.newTab().setText("转发"));
        tabLayout.addTab(tabLayout.newTab().setText("赞"));
        tabLayout.addOnTabSelectedListener(new OnTabChangeListener());

        viewPager=findViewById(R.id.view_pager);
        viewPager.setAdapter(new ViewPagerAdapter());
        viewPager.addOnPageChangeListener(new UserOnPagerChangeListener());

        findViewById(R.id.post_good_1).setVisibility(View.GONE);
        findViewById(R.id.post_forward_1).setVisibility(View.GONE);
        findViewById(R.id.post_delete_1).setVisibility(View.GONE);
        findViewById(R.id.post_comment_1).setVisibility(View.GONE);
        findViewById(R.id.post_forward).setVisibility(View.GONE);
        findViewById(R.id.post_good).setVisibility(View.GONE);
        findViewById(R.id.post_delete).setVisibility(View.GONE);
        findViewById(R.id.post_comment).setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        ViewPagerAdapter() {
            super(PostActivity.this.getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }
}
