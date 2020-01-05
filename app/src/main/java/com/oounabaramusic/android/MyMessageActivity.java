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
import com.oounabaramusic.android.fragment.MyMessageCommentFragment;
import com.oounabaramusic.android.fragment.NoticeFragment;
import com.oounabaramusic.android.fragment.PrivateMessageFragment;

import java.util.ArrayList;
import java.util.List;

public class MyMessageActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager vp;
    private List<BaseFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);

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
        vp=findViewById(R.id.view_pager);
        tabLayout=findViewById(R.id.tab_layout);

        fragments.add(new PrivateMessageFragment(this));
        fragments.add(new MyMessageCommentFragment(this));
        fragments.add(new NoticeFragment(this));

        vp.setAdapter(new ViewPagerAdapter());

        tabLayout.setupWithViewPager(vp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter{

        ViewPagerAdapter() {
            super(MyMessageActivity.this.getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
