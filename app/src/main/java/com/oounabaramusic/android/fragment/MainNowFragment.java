package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SendPostActivity;
import com.oounabaramusic.android.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainNowFragment extends BaseFragment implements View.OnClickListener{

    private BaseActivity activity;
    private View rootView;
    private List<BaseFragment> fragments;
    private FloatingActionButton addPost;

    public MainNowFragment(BaseActivity activity){
        this.activity=activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_main_now,container,false);
        init();
        return rootView;
    }

    private void init() {
        fragments=new ArrayList<>();
        fragments.add(new MainNowSquareFragment(activity));
        fragments.add(new MainNowFollowFragment(activity));

        ViewPager vp=rootView.findViewById(R.id.view_pager);
        TabLayout tl=rootView.findViewById(R.id.tab_layout);

        vp.setAdapter(new ViewPagerAdapter());
        tl.setupWithViewPager(vp);

        addPost=rootView.findViewById(R.id.send_post);
        addPost.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_post:
                if(!SharedPreferencesUtil.isLogin(activity.sp)){
                    Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(activity, SendPostActivity.class);
                activity.startActivity(intent);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        ViewPagerAdapter() {
            super(MainNowFragment.this.getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
