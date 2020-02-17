package com.oounabaramusic.android.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.UserInfoActivity;
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

public class UserInfoPostFragment extends BaseFragment {

    private UserInfoActivity activity;
    private View rootView;
    private PostAdapter adapter;

    private SwipeRefreshLayout srl;
    private boolean ok;
    private boolean end;  //标记服务器还有没有更多的数据

    public UserInfoPostFragment(UserInfoActivity activity){
        this.activity=activity;
        setTitle("动态");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=LayoutInflater.from(activity).inflate(R.layout.fragment_user_info_post,container,false);
        init();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(ok){
            ok=false;
            getContent();
        }
    }

    private void init() {
        srl= (SwipeRefreshLayout) rootView;
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getContent();
            }
        });

        RecyclerView recyclerView= rootView.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter=new PostAdapter(activity));
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        ok=true;
    }

    private void getContent(){
        srl.setRefreshing(true);

        Post post = new Post();
        post.setUserId(activity.getContentUserId());
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
        post.setUserId(activity.getContentUserId());
        post.setMainUserId(SharedPreferencesUtil.getUserId(activity.sp));
        post.setStart(adapter.getItemCount());

        new S2SHttpUtil(
                activity,
                new Gson().toJson(post),
                MyEnvironment.serverBasePath+"getPost",
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT_2);
    }

    static class MyHandler extends Handler{
        UserInfoPostFragment fragment;
        MyHandler(UserInfoPostFragment fragment){
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
