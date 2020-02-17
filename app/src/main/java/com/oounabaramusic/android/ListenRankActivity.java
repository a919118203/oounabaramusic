package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.oounabaramusic.android.adapter.ListenRankAdapter;
import com.oounabaramusic.android.util.StatusBarUtil;

public class ListenRankActivity extends BaseActivity {

    private int userId;
    private RecyclerView rv;
    private ListenRankAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_rank);
        StatusBarUtil.setWhiteStyleStatusBar(this);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        userId = getIntent().getIntExtra("userId",-1);

        init();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.refresh();
    }

    public int getUserId() {
        return userId;
    }

    private void init() {
        rv=findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new ListenRankAdapter(this));
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter.refresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
