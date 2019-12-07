package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.Objects;

public class EditPlayListInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private final static int TOOLBAR_MODE_NORMAL=0;//普通模式
    private final static int TOOLBAR_MODE_EDIT_NAME=1;//名称编辑模式
    private final static int TOOLBAR_MODE_EDIT_TAG=2;//标签编辑模式
    private final static int TOOLBAR_MODE_EDIT_DESCRIPTION=3;//描述编辑模式
    private final static int CONTENT_MAX_LENGTH=1000;//歌单介绍最大长度
    private int toolBarMode=TOOLBAR_MODE_NORMAL;

    private EditText newPlaylistName;
    private EditText newPlaylistDescription;
    private TextView descriptionCnt;
    private LinearLayout tagHint;
    private RecyclerView tagRv;
    private FrameLayout description;
    private LinearLayout mainContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_play_list_info);

        Toolbar toolbar=findViewById(R.id.activity_edit_play_list_info_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        init();
    }

    private void init() {
        newPlaylistName=findViewById(R.id.edit_playlist_name);
        tagRv=findViewById(R.id.edit_play_list_info_tag);
        tagHint=findViewById(R.id.tag_count_hint_layout);
        description=findViewById(R.id.edit_play_list_info_description);
        mainContent=findViewById(R.id.edit_play_list_info_content);

        findViewById(R.id.change_playlist_cover).setOnClickListener(this);
        findViewById(R.id.change_playlist_name).setOnClickListener(this);
        findViewById(R.id.change_playlist_tag).setOnClickListener(this);
        findViewById(R.id.change_playlist_description).setOnClickListener(this);

        newPlaylistDescription=findViewById(R.id.playlist_description);
        descriptionCnt=findViewById(R.id.description_cnt);
        descriptionCnt.setText(String.valueOf(CONTENT_MAX_LENGTH));
        newPlaylistDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int cnt=newPlaylistDescription.getText().toString().length();
                descriptionCnt.setText(String.valueOf(CONTENT_MAX_LENGTH-cnt));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_play_list_info_menu,menu);
        menu.findItem(R.id.edit_info_save_name).setVisible(toolBarMode==TOOLBAR_MODE_EDIT_NAME);
        menu.findItem(R.id.edit_info_save_description).setVisible(toolBarMode==TOOLBAR_MODE_EDIT_DESCRIPTION);
        menu.findItem(R.id.edit_info_save_tag).setVisible(toolBarMode==TOOLBAR_MODE_EDIT_TAG);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                if(toolBarMode!=TOOLBAR_MODE_NORMAL){
                    switchToolBar(TOOLBAR_MODE_NORMAL);
                }else{
                    finish();
                }
                break;
            case R.id.edit_info_save_description:

                break;
        }
        return true;
    }


    /**
     * 切换画面模式
     * @param mode
     */
    private void switchToolBar(int mode){
        toolBarMode=mode;
        invalidateOptionsMenu();

        String title="";
        switch (mode){
            case TOOLBAR_MODE_NORMAL:
                title="编辑歌单信息";
                newPlaylistName.setVisibility(View.GONE);
                tagHint.setVisibility(View.GONE);
                tagRv.setVisibility(View.GONE);
                description.setVisibility(View.GONE);
                mainContent.setVisibility(View.VISIBLE);
                break;
            case TOOLBAR_MODE_EDIT_NAME:
                title="歌单名称";
                newPlaylistName.setVisibility(View.VISIBLE);
                tagHint.setVisibility(View.GONE);
                tagRv.setVisibility(View.GONE);
                description.setVisibility(View.GONE);
                mainContent.setVisibility(View.GONE);
                break;
            case TOOLBAR_MODE_EDIT_TAG:
                title="选择标签";
                newPlaylistName.setVisibility(View.GONE);
                tagHint.setVisibility(View.VISIBLE);
                tagRv.setVisibility(View.VISIBLE);
                description.setVisibility(View.GONE);
                mainContent.setVisibility(View.GONE);
                break;
            case TOOLBAR_MODE_EDIT_DESCRIPTION:
                title="歌单介绍";
                newPlaylistName.setVisibility(View.GONE);
                tagHint.setVisibility(View.GONE);
                tagRv.setVisibility(View.GONE);
                description.setVisibility(View.VISIBLE);
                mainContent.setVisibility(View.GONE);
                break;
        }

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(title);
        }

        StatusBarUtil.hideSoftKeyboard(this);
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
            if(toolBarMode!=TOOLBAR_MODE_NORMAL){
                switchToolBar(TOOLBAR_MODE_NORMAL);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_playlist_cover:
                break;
            case R.id.change_playlist_name:
                switchToolBar(TOOLBAR_MODE_EDIT_NAME);
                break;
            case R.id.change_playlist_tag:
                switchToolBar(TOOLBAR_MODE_EDIT_TAG);
                break;
            case R.id.change_playlist_description:
                switchToolBar(TOOLBAR_MODE_EDIT_DESCRIPTION);
                break;
        }
    }
}
