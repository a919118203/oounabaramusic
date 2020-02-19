package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.oounabaramusic.android.adapter.ChooseMusicAdapter;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.okhttputil.SearchHttpUtil;
import com.oounabaramusic.android.util.InputMethodUtil;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.List;
import java.util.Objects;

public class ChooseMusicActivity extends AppCompatActivity {

    private EditText musicName;
    private ChooseMusicAdapter adapter;

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
        rv.setAdapter(adapter=new ChooseMusicAdapter(this));
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void search(String musicName){
        //SearchHttpUtil.searchMusic(this,musicName,new MyHandler(this));  TODO
        InputMethodUtil.hideSoftKeyboard(this);
    }

    static class MyHandler extends Handler{
        ChooseMusicActivity activity;
        MyHandler(ChooseMusicActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SearchActivity.MESSAGE_SEARCH_MUSIC:
                    List<Music> dataList = (List<Music>) msg.obj;
                    activity.adapter.setDataList(dataList);
                    break;
            }
        }
    }
}
