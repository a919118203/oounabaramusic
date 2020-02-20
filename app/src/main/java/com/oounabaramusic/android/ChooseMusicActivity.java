package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.oounabaramusic.android.adapter.ChooseMusicAdapter;
import com.oounabaramusic.android.util.InputMethodUtil;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.Objects;

public class ChooseMusicActivity extends AppCompatActivity {

    private EditText musicName;
    private ChooseMusicAdapter adapter;
    private SwipeRefreshLayout srl;

    private boolean f;
    private String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_music);
        StatusBarUtil.setWhiteStyleStatusBar(this);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void init(){
        srl=findViewById(R.id.swipe_refresh);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl.setRefreshing(false);
            }
        });

        musicName=findViewById(R.id.music_name);
        musicName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH) {
                    String searchText=musicName.getText().toString();
                    if(searchText.equals("")){
                        searchText=musicName.getHint().toString();
                    }
                    search(searchText);
                }
                return true;
            }
        });

        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new ChooseMusicAdapter(this,srl));
        rv.setLayoutManager(new LinearLayoutManager(this));
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

    public String getSearchText() {
        return searchText;
    }

    private void search(String musicName){
        searchText=musicName;
        adapter.getContent();
        InputMethodUtil.hideSoftKeyboard(this);
    }
}
