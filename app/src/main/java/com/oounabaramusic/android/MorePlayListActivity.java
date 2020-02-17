package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.oounabaramusic.android.fragment.BaseFragment;
import com.oounabaramusic.android.fragment.FavoritePlayListFragment;
import com.oounabaramusic.android.fragment.MyPlayListFragment;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MorePlayListActivity extends BaseActivity {

    private List<BaseFragment> fragments;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_play_list);
        StatusBarUtil.setWhiteStyleStatusBar(this);
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
        fragments.add(new MyPlayListFragment(this));
        fragments.add(new FavoritePlayListFragment(this));

        ViewPager viewPager=findViewById(R.id.view_pager);
        viewPager.setAdapter(new ViewPagerAdapter());
        TabLayout tabLayout=findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        initContent();
    }

    private void initContent(){
        userId = getIntent().getIntExtra("userId",0);
        ((MyPlayListFragment)fragments.get(0)).setUserId(userId);
        ((FavoritePlayListFragment)fragments.get(1)).setUserId(userId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        ViewPagerAdapter() {
            super(MorePlayListActivity.this.getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
