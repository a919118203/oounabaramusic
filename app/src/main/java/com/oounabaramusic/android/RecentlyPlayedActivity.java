package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.oounabaramusic.android.fragment.BaseFragment;
import com.oounabaramusic.android.fragment.RPMusicFragment;
import com.oounabaramusic.android.fragment.RPPlayListFragment;
import com.oounabaramusic.android.fragment.RPVideoFragment;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecentlyPlayedActivity extends BaseActivity {

    private TabLayout tl;
    private ViewPager vp;
    private List<BaseFragment> fragments;

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
        fragments.add(new RPVideoFragment(this));
        fragments.add(new RPPlayListFragment(this));

        vp=findViewById(R.id.view_pager);
        tl=findViewById(R.id.tab_layout);

        vp.setAdapter(new ViewPagerAdapter());
        tl.setupWithViewPager(vp);
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
}
