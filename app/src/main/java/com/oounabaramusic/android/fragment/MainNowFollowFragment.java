package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.PostAdapter;
import com.oounabaramusic.android.bean.Post;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainNowFollowFragment extends BaseFragment {

    private BaseActivity activity;
    private View rootView;
    private PostAdapter adapter;

    private SwipeRefreshLayout srl;

    private boolean ok;
    private boolean end;

    public MainNowFollowFragment(BaseActivity activity){
        this.activity=activity;
        setTitle("关注");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_main_now_follow,container,false);
        init();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!ok){
            ok=true;
            getContent();
        }
    }

    private void init() {
        ok=false;
        srl= (SwipeRefreshLayout) rootView;
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getContent();
            }
        });

        RecyclerView rv= rootView.findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new PostAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if(llm.findLastVisibleItemPosition()==adapter.getItemCount()-1){
                    if(!end){
                        getNextContent();
                    }
                }
            }
        });
    }

    private void getContent(){
        srl.setRefreshing(true);

        Post post = new Post();
        post.setFromUserId(SharedPreferencesUtil.getUserId(activity.sp));
        post.setMainUserId(SharedPreferencesUtil.getUserId(activity.sp));
        post.setStart(0);

        new S2SHttpUtil(
                activity,
                new Gson().toJson(post),
                MyEnvironment.serverBasePath+"getPost",
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT);
    }

    private void getNextContent(){
        if(srl.isRefreshing()){
            return;
        }
        srl.setRefreshing(true);

        Post post = new Post();
        post.setFromUserId(SharedPreferencesUtil.getUserId(activity.sp));
        post.setMainUserId(SharedPreferencesUtil.getUserId(activity.sp));
        post.setStart(adapter.getItemCount());

        new S2SHttpUtil(
                activity,
                new Gson().toJson(post),
                MyEnvironment.serverBasePath+"getPost",
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT_2);
    }

    static class MyHandler extends Handler {
        MainNowFollowFragment fragment;
        MyHandler(MainNowFollowFragment fragment){
            this.fragment=fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            String str = (String)msg.obj;

            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    List<Post> dataList = new Gson().fromJson(str,
                            new TypeToken<List<Post>>(){}.getType());

                    fragment.adapter.setDataList(dataList);
                    fragment.srl.setRefreshing(false);
                    fragment.end=false;
                    break;

                case BasicCode.GET_CONTENT_2:
                    dataList = new Gson().fromJson(str,
                            new TypeToken<List<Post>>(){}.getType());

                    if(dataList.isEmpty()){
                        fragment.end=true;
                    }
                    fragment.adapter.addDataList(dataList);
                    fragment.srl.setRefreshing(false);
                    break;
            }
        }
    }
}
