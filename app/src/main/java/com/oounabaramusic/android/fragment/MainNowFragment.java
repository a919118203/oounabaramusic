package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainNowFragment extends BaseFragment {

    private BaseActivity activity;
    private View rootView;
    private List<BaseFragment> fragments;

    public MainNowFragment(BaseActivity activity){
        this.activity=activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(R.layout.fragment_main_now,container,false);
            init(rootView);
        }
        return rootView;
    }

    private void init(View view) {
        fragments=new ArrayList<>();
        fragments.add(new MainNowSquareFragment(activity));
        fragments.add(new MainNowFollowFragment(activity));

        ViewPager vp=view.findViewById(R.id.view_pager);
        TabLayout tl=view.findViewById(R.id.tab_layout);

        vp.setAdapter(new ViewPagerAdapter());
        tl.setupWithViewPager(vp);
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
