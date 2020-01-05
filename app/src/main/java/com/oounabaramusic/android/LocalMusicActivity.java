package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.oounabaramusic.android.adapter.LocalMusicAdapter;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class LocalMusicActivity extends BaseActivity implements View.OnClickListener{

    public final static int TOOLBAR_MODE_EDIT=0;//检索编辑模式
    public final static int TOOLBAR_MODE_NORMAL=1;//普通模式
    public final static int TOOLBAR_MODE_MULTIPLE_CHOICE=2;//多选模式
    private final int MUSIC_MAX_COUNT=10001;
    private int toolbarMode=TOOLBAR_MODE_NORMAL;//ToolBar现在的模式
    private RecyclerView rv;//显示歌曲列表
    private LocalMusicAdapter adapter;//歌曲列表的适配器
    private EditText edit;//用于搜索歌曲列表的输入框
    private LinearLayout bottomToolBar;//多选模式时，显示在底部的菜单
    private LinearLayout nextPlay;//底部菜单的“下一首播放”
    private LinearLayout addToPlaylist;//底部菜单的“加入歌单”
    private LinearLayout delete;//底部菜单的“删除”
    private LinearLayout playAll;//ToolBar下方的播放全部
    private LinearLayout multipleChoice;//ToolBar下方的多选
    private List<Music> musicList;//存歌曲列表的List

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        StatusBarUtil.changeStatusBarContentColor(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Toolbar toolBar=findViewById(R.id.local_music_toolbar);
        setSupportActionBar(toolBar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        init();
    }

    private void init() {
        View view=findViewById(R.id.outermost_layout);
        view.setPadding(
                view.getPaddingLeft(),
                view.getPaddingTop()+StatusBarUtil.getStatusBarHeight(this),
                view.getPaddingRight(),
                view.getPaddingBottom());

        rv=findViewById(R.id.local_music_recycler_view);
        adapter=new LocalMusicAdapter(this,createMusicList());
        rv.setAdapter(adapter);
        LinearLayoutManager llm=new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        edit=findViewById(R.id.local_music_edit);
        playAll=findViewById(R.id.local_music_play_all);
        multipleChoice=findViewById(R.id.local_music_multiple_choice);
        bottomToolBar=findViewById(R.id.bottom_toolbar);
        nextPlay=findViewById(R.id.bottom_tool_next_play);
        addToPlaylist=findViewById(R.id.bottom_tool_add_to_playlist);
        delete=findViewById(R.id.bottom_tool_delete);

        playAll.setOnClickListener(this);
        multipleChoice.setOnClickListener(this);
        nextPlay.setOnClickListener(this);
        addToPlaylist.setOnClickListener(this);
        delete.setOnClickListener(this);
    }

    private List<Music> createMusicList() {
        musicList=new ArrayList<>();
        return musicList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_local_music,menu);
        for(int i=0;i<menu.size();i++){
            MenuItem item=menu.getItem(i);
            switch(item.getItemId()){
                case R.id.local_music_search_in_list:
                case R.id.local_music_search_in_file:
                    if(toolbarMode!=TOOLBAR_MODE_NORMAL){
                        item.setVisible(false);
                    }
                    break;
                case R.id.local_music_menu_multiple_choice:
                    if(toolbarMode!=TOOLBAR_MODE_MULTIPLE_CHOICE){
                        item.setVisible(false);
                    }
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home://退出或退出edit模式
                if(toolbarMode!=TOOLBAR_MODE_NORMAL){
                    switchToolBar(TOOLBAR_MODE_NORMAL);
                    StatusBarUtil.hideSoftKeyboard(this);
                }else{
                    finish();
                }
                break;
            case R.id.local_music_search_in_list://检索list中的歌曲
                switchToolBar(TOOLBAR_MODE_EDIT);
                break;
            case R.id.local_music_search_in_file://扫描所有文件夹中的歌曲
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.local_music_play_all://播放全部
                break;
            case R.id.local_music_multiple_choice://多选
                switchToolBar(TOOLBAR_MODE_MULTIPLE_CHOICE);
                break;
            case R.id.bottom_tool_next_play:
                if(!adapter.checkSelected()){
                    Toast toast=Toast.makeText(this, "请选择歌曲", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM,0,200);
                    toast.show();
                }
                break;
            case R.id.bottom_tool_add_to_playlist:
                if(!adapter.checkSelected()){
                    Toast toast=Toast.makeText(this, "请选择歌曲", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM,0,200);
                    toast.show();
                }
                int a=1;
                break;
            case R.id.bottom_tool_delete:
                if(!adapter.checkSelected()){
                    Toast toast=Toast.makeText(this, "请选择歌曲", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM,0,200);
                    toast.show();
                }
                int b=1,c=2;
                break;
        }
    }

    /**
     * 切换toolbar的模式
     * @param mode
     */

    private void switchToolBar(int mode){
        toolbarMode=mode;
        invalidateOptionsMenu();

        String actionBarTitle="";
        switch (mode){
            case TOOLBAR_MODE_NORMAL:
                playAll.setVisibility(View.VISIBLE);
                edit.setVisibility(View.GONE);
                bottomToolBar.setVisibility(View.GONE);
                actionBarTitle="本地音乐";
                break;
            case TOOLBAR_MODE_EDIT:
                playAll.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                bottomToolBar.setVisibility(View.GONE);
                break;
            case TOOLBAR_MODE_MULTIPLE_CHOICE:
                playAll.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                bottomToolBar.setVisibility(View.VISIBLE);
                actionBarTitle="已选择0项";
                break;
        }

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(actionBarTitle);
        }

        adapter.setToolBarMode(toolbarMode);
        adapter.notifyDataSetChanged();
    }


    /**
     * 如果为普通模式就直接退出，
     * 否则退出到普通模式
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(toolbarMode!=TOOLBAR_MODE_NORMAL){
                switchToolBar(TOOLBAR_MODE_NORMAL);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
