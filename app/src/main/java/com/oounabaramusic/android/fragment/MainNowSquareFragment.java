package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.MainNowSquareAdapter;
import com.oounabaramusic.android.bean.Post;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainNowSquareFragment extends BaseFragment {

    private Activity activity;
    private View rootView;
    private MainNowSquareAdapter adapter;

    private SwipeRefreshLayout srl;

    private S2SHttpUtil http;

    private boolean end ;//服务器是否还有更多的动态
    private boolean getContent;

    public MainNowSquareFragment(Activity activity){
        this.activity=activity;
        setTitle("广场");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_main_now_square,container,false);
        init();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!getContent){
            getContent=true;
            getContent();
        }
    }

    private void init() {
        srl = (SwipeRefreshLayout) rootView;
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getContent();
            }
        });

        RecyclerView rv= rootView.findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new MainNowSquareAdapter(activity,this));
        rv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                //滚动停止时，判断是不是最后一个
                if(newState==RecyclerView.SCROLL_STATE_IDLE){
                    StaggeredGridLayoutManager sgm = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();

                    int[] index;
                    index = sgm.findLastVisibleItemPositions(null);
                    if(index[0]==adapter.getItemCount()-1||index[1]==adapter.getItemCount()-1){
                        if(!end){
                            getNextContent();
                        }
                    }
                }
            }
        });
    }

    private void getContent(){
        if(http!=null){
            return;
        }

        srl.setRefreshing(true);

         http = new S2SHttpUtil(
                activity,
                String.valueOf(0),
                MyEnvironment.serverBasePath+"getSquarePost",
                new MyHandler(this));
        http.call(BasicCode.GET_CONTENT_2);
    }

    public void getNextContent(){
        if(http!=null){
            return;
        }

        srl.setRefreshing(true);

        int start = adapter.getItemCount();
        http = new S2SHttpUtil(
                activity,
                String.valueOf(start),
                MyEnvironment.serverBasePath+"getSquarePost",
                new MyHandler(this));
        http.call(BasicCode.GET_CONTENT);
    }

    static class MyHandler extends Handler{
        MainNowSquareFragment fragment;
        MyHandler(MainNowSquareFragment fragment){
            this.fragment=fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    List<Post> dataList = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<Post>>(){}.getType());

                    if(dataList.isEmpty()){
                        fragment.end=true;
                    }
                    fragment.adapter.addDataList(dataList);
                    fragment.http=null;
                    break;

                case BasicCode.GET_CONTENT_2:
                    dataList = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<Post>>(){}.getType());

                    fragment.adapter.setDataList(dataList);
                    fragment.http=null;
                    fragment.end=false;
                    break;
            }
            fragment.srl.setRefreshing(false);
        }
    }
}
