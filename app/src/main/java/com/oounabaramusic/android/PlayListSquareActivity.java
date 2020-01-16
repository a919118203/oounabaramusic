package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.oounabaramusic.android.fragment.BaseFragment;
import com.oounabaramusic.android.fragment.PlayListFragment;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayListSquareActivity extends BaseActivity {

    private List<BaseFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_square);
        StatusBarUtil.setWhiteStyleStatusBar(this);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        fragments=new ArrayList<>();
        fragments.add(new PlayListFragment(this,"ACG"));
        fragments.add(new PlayListFragment(this,"和风"));
        fragments.add(new PlayListFragment(this,"翻唱"));
        fragments.add(new PlayListFragment(this,"民谣"));
        fragments.add(new PlayListFragment(this,"ACG"));
        fragments.add(new PlayListFragment(this,"ACG"));
        fragments.add(new PlayListFragment(this,"ACG"));
        fragments.add(new PlayListFragment(this,"ACG"));
        fragments.add(new PlayListFragment(this,"ACG"));

        ViewPager vp=findViewById(R.id.view_pager);
        TabLayout tl=findViewById(R.id.tab_layout);
        vp.setAdapter(new ViewPagerAdapter());
        tl.setupWithViewPager(vp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_play_list_square,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.play_list_tag:
                Intent intent=new Intent(this,PlayListTagActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        public ViewPagerAdapter() {
            super(PlayListSquareActivity.this.getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
