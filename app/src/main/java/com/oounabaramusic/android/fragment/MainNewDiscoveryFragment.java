package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.PlayListActivity;
import com.oounabaramusic.android.PlayListSquareActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.NDPlayListAdapter;
import com.oounabaramusic.android.adapter.NDRankAdapter;
import com.oounabaramusic.android.adapter.NDRecommendAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class MainNewDiscoveryFragment extends BaseFragment{

    private Activity activity;
    private View rootView;
    private NDRecommendAdapter adapter1;         //每日推荐
    private NDPlayListAdapter adapter2;          //推荐歌单
    private NDRankAdapter adapter3;              //排行榜
    private FragmentManager fm;
    private ViewPager imageVp;
    private List<Fragment> images;
    private boolean f;                 //开始滚动图片

    public MainNewDiscoveryFragment(Activity activity, FragmentManager fm){
        this.activity=activity;
        this.fm=fm;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(R.layout.fragment_main_new_discovery,container,false);
            init(rootView);
        }
        return rootView;
    }

    private void init(View view) {
        RecyclerView rv1=view.findViewById(R.id.recycler_view_1);
        rv1.setAdapter(adapter1=new NDRecommendAdapter(activity));
        rv1.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false));

        RecyclerView rv2=view.findViewById(R.id.recycler_view_2);
        rv2.setAdapter(adapter2=new NDPlayListAdapter(activity));
        rv2.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false));

        RecyclerView rv3=view.findViewById(R.id.recycler_view_3);
        rv3.setAdapter(adapter3=new NDRankAdapter(activity));
        rv3.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false));

        imageVp=view.findViewById(R.id.view_pager);
        imageVp.setOverScrollMode(ViewPager.OVER_SCROLL_ALWAYS);
        images=new ArrayList<>();
        images.add(new ImageFragment(R.drawable.mogeko1));
        images.add(new ImageFragment(R.drawable.mogeko2));
        images.add(new ImageFragment(R.drawable.mogeko3));
        images.add(new ImageFragment(R.drawable.mogeko4));
        imageVp.setAdapter(new FragmentPagerAdapter(fm,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

            @NonNull
            @Override
            public Fragment getItem(int position) {
                return images.get(position);
            }

            @Override
            public int getCount() {
                return images.size();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(4000);
                        if(f)
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageVp.setCurrentItem((imageVp.getCurrentItem()+1)%4);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        view.findViewById(R.id.check_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, PlayListSquareActivity.class);
                activity.startActivity(intent);
            }
        });
    }

    public void setF(boolean f) {
        this.f = f;
        if(!f){
            imageVp.setCurrentItem(0);
        }
    }
}
