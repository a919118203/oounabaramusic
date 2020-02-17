package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.PlayListActivity;
import com.oounabaramusic.android.PlayListSquareActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.NDPlayListAdapter;
import com.oounabaramusic.android.adapter.NDRankAdapter;
import com.oounabaramusic.android.adapter.NDRecommendAdapter;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.bean.PlayListSmallTag;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainNewDiscoveryFragment extends BaseFragment{

    private BaseActivity activity;
    private View rootView;
    private NDRecommendAdapter adapter1;         //每日推荐
    private NDPlayListAdapter adapter2;          //推荐歌单
    private NDRankAdapter adapter3;              //排行榜
    private ViewPager imageVp;
    private List<Fragment> images;
    private boolean f;                 //标记是否开始滚动图片

    private int userId;

    public MainNewDiscoveryFragment(BaseActivity activity){
        this.activity=activity;
        userId = Integer.valueOf(activity.sp.getString("userId","-1"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(R.layout.fragment_main_new_discovery,container,false);
            init();
        }
        return rootView;
    }

    private void init() {
        RecyclerView rv1=rootView.findViewById(R.id.recycler_view_1);
        rv1.setAdapter(adapter1=new NDRecommendAdapter(activity));
        rv1.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false));

        RecyclerView rv2=rootView.findViewById(R.id.recycler_view_2);
        rv2.setAdapter(adapter2=new NDPlayListAdapter(activity));
        rv2.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false));

        RecyclerView rv3=rootView.findViewById(R.id.recycler_view_3);
        rv3.setAdapter(adapter3=new NDRankAdapter(activity));
        rv3.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false));

        rootView.findViewById(R.id.check_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new S2SHttpUtil(
                        activity,
                        userId+"",
                        MyEnvironment.serverBasePath+"getUserPlayListTag",
                        new MyHandler(MainNewDiscoveryFragment.this))
                .call(BasicCode.GET_CONTENT_2);
            }
        });


        initScrollImage();
    }

    @Override
    public void onResume() {
        super.onResume();
        initPlayListContent();
    }

    private void initPlayListContent(){
        new S2SHttpUtil(
                activity,
                userId+"",
                MyEnvironment.serverBasePath+"getPopularityPlayList",
                new MyHandler(this))
        .call(BasicCode.GET_CONTENT);
    }

    private void initScrollImage(){
        imageVp=rootView.findViewById(R.id.view_pager);
        images=new ArrayList<>();
        images.add(new ImageFragment(R.drawable.mogeko1));
        images.add(new ImageFragment(R.drawable.mogeko2));
        images.add(new ImageFragment(R.drawable.mogeko3));
        images.add(new ImageFragment(R.drawable.mogeko4));
        imageVp.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

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
    }

    public void setF(boolean f) {
        this.f = f;
        if(!f){
            imageVp.setCurrentItem(0);
        }
    }

    static class MyHandler extends Handler{

        MainNewDiscoveryFragment fragment;

        MyHandler(MainNewDiscoveryFragment fragment){
            this.fragment=fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    LogUtil.printLog("json:  "+(String)msg.obj);

                    List<PlayList> dataList = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<PlayList>>(){}.getType());

                    fragment.adapter2.setDataList(dataList);
                    break;

                case BasicCode.GET_CONTENT_2:
                    Intent intent=new Intent(fragment.activity, PlayListSquareActivity.class);
                    ArrayList<PlayListSmallTag> tags = new Gson().fromJson((String)msg.obj,
                            new TypeToken<ArrayList<PlayListSmallTag>>(){}.getType());
                    intent.putExtra("tags",tags);
                    fragment.activity.startActivity(intent);
                    break;
            }
        }
    }
}
