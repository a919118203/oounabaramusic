package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.oounabaramusic.android.fragment.BaseFragment;
import com.oounabaramusic.android.fragment.FollowedFragment;
import com.oounabaramusic.android.fragment.ToFollowFragment;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.util.UserInfoActivityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyFriendActivity extends BaseActivity {

    public static final int TO_FOLLOW=0;       //关注
    public static final int FOLLOWED=1;        //粉丝
    private TabLayout tabLayout;
    private List<BaseFragment> fragments;
    private ViewPager viewPager;

    private int userId;

    public static void startActivity(Context context,int userId,int from){
        Intent intent=new Intent(context,MyFriendActivity.class);
        intent.putExtra("userId",userId);
        intent.putExtra("from",from);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friend);
        UserInfoActivityManager.addActivity(this);
        StatusBarUtil.setWhiteStyleStatusBar(this);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        Intent intent=getIntent();
        userId=intent.getIntExtra("userId",0);

        fragments=new ArrayList<>();
        fragments.add(new ToFollowFragment(this,userId));
        fragments.add(new FollowedFragment(this,userId));


        tabLayout=findViewById(R.id.my_friend_tab_layout);
        viewPager=findViewById(R.id.my_friend_view_pager);
        viewPager.setAdapter(new ViewPagerAdapter());
        tabLayout.setupWithViewPager(viewPager);

        int position=intent.getIntExtra("from",0);
        Objects.requireNonNull(tabLayout.getTabAt(position)).select();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        UserInfoActivityManager.removeActivity(this);
        return true;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        ViewPagerAdapter() {
            super(MyFriendActivity.this.getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
