package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SearchActivity;
import com.oounabaramusic.android.adapter.SearchUserAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SearchUserFragment extends BaseFragment {

    private SearchActivity activity;
    private View rootView;
    private SearchUserAdapter adapter;
    private RecyclerView rv;
    private SwipeRefreshLayout srl;
    private boolean f;

    public SearchUserFragment(SearchActivity activity){
        this.activity=activity;
        setTitle("用户");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_search_user,container,false);
        init();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!f){
            f=true;
            adapter.getContent();
        }
    }

    private void init() {
        f=false;

        srl= (SwipeRefreshLayout) rootView;
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                f=false;
                adapter.getContent();
            }
        });

        rv=rootView.findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new SearchUserAdapter(activity,srl));
        rv.setLayoutManager(new LinearLayoutManager(activity));

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if(llm.findLastVisibleItemPosition()==adapter.getItemCount()-1){
                    adapter.getNextContent();
                }
            }
        });
    }

    @Override
    public void notifyFragment() {
        f=false;
    }
}
