package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.List;

public class LocalMusicActivity extends BaseActivity {

    private RecyclerView rv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.changeStatusBarContentColor(this);
        setContentView(R.layout.activity_local_music);

        setSupportActionBar((Toolbar) findViewById(R.id.local_music_toolbar));
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.quit);
        }
        findViewById(R.id.local_music_layout).setPadding(0,StatusBarUtil.getStatusBarHeight(this),0,0);

        init();
    }

    private void init() {
        rv=findViewById(R.id.local_music_recycler_view);
        rv.setAdapter(new LocalMusicAdapter());
        LinearLayoutManager llm=new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.local_music_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.local_music_search_in_list:
                ActionBar actionBar=getSupportActionBar();
                if(actionBar!=null){
                    actionBar.hide();
                }
                break;
            case R.id.local_music_search_in_file:
                break;
        }
        return true;
    }

    private class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.LocalMusicViewHolder>{


        @NonNull
        @Override
        public LocalMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull LocalMusicViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        private class LocalMusicViewHolder extends RecyclerView.ViewHolder{

            public LocalMusicViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
