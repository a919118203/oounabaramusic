package com.oounabaramusic.android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.adapter.TagAdapter;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.bean.PlayListBigTag;
import com.oounabaramusic.android.bean.PlayListSmallTag;
import com.oounabaramusic.android.okhttputil.PlayListHttpUtil;
import com.oounabaramusic.android.okhttputil.TagHttpUtil;
import com.oounabaramusic.android.util.InputMethodUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.RealPathFromUriUtils;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.widget.customview.MyImageView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditPlayListInfoActivity extends BaseActivity implements View.OnClickListener{

    private final static int TOOLBAR_MODE_NORMAL=0;//普通模式
    private final static int TOOLBAR_MODE_EDIT_NAME=1;//名称编辑模式
    private final static int TOOLBAR_MODE_EDIT_TAG=2;//标签编辑模式
    private final static int TOOLBAR_MODE_EDIT_DESCRIPTION=3;//描述编辑模式
    private final static int CONTENT_MAX_LENGTH=1000;//歌单介绍最大长度
    private int toolBarMode=TOOLBAR_MODE_NORMAL;

    private EditText newPlaylistName;
    private EditText newPlaylistDescription;
    private TextView descriptionCnt;
    private FrameLayout description;
    private LinearLayout mainContent;

    //tag
    private LinearLayout tagHint;
    private RecyclerView tagRv;
    private TagAdapter tagAdapter;
    private ProgressBar pbTag;

    private MyImageView playListCover;
    private TextView playListName;
    private TextView playListTag;
    private TextView playListIntroduction;

    private PlayList playList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//必须写在setContentView之前。
        setContentView(R.layout.activity_edit_play_list_info);
        Toolbar toolbar=findViewById(R.id.activity_edit_play_list_info_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        playList= (PlayList) getIntent().getSerializableExtra("playList");

        init();

    }

    private void init() {

        newPlaylistName=findViewById(R.id.edit_playlist_name);
        tagRv=findViewById(R.id.edit_play_list_info_tag);
        tagHint=findViewById(R.id.tag_count_hint_layout);
        pbTag=findViewById(R.id.load_tag);
        description=findViewById(R.id.edit_play_list_info_description);
        mainContent=findViewById(R.id.edit_play_list_info_content);
        playListCover=findViewById(R.id.playlist_cover);
        playListName=findViewById(R.id.playlist_name);
        playListTag=findViewById(R.id.playlist_tag);
        playListIntroduction=findViewById(R.id.playlist_introduction);

        playListCover.setImage(new MyImage(MyImage.TYPE_PLAY_LIST_COVER,playList.getId()));
        playListName.setText(playList.getPlayListName());

        StringBuilder tag=new StringBuilder();
        for(PlayListSmallTag smallTag:playList.getPlayListTag()){
            tag.append(smallTag.getName());
            tag.append(",");
        }
        if(tag.length()!=0){
            tag.deleteCharAt(tag.length()-1);
        }
        playListTag.setText(tag.toString());
        playListIntroduction.setText(playList.getIntroduction());


        findViewById(R.id.change_playlist_cover).setOnClickListener(this);
        findViewById(R.id.change_playlist_name).setOnClickListener(this);
        findViewById(R.id.change_playlist_tag).setOnClickListener(this);
        findViewById(R.id.change_playlist_description).setOnClickListener(this);

        //控制输入时字数的变化
        newPlaylistDescription=findViewById(R.id.playlist_description);
        descriptionCnt=findViewById(R.id.description_cnt);
        descriptionCnt.setText(String.valueOf(CONTENT_MAX_LENGTH));
        newPlaylistDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            /**
             * 如果总长度超过1000，就截断
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {
                descriptionCnt.setText(String.valueOf(CONTENT_MAX_LENGTH-s.length()));
                if(s.length()>CONTENT_MAX_LENGTH){
                    saveDescription.setEnabled(false);
                }else{
                    saveDescription.setEnabled(true);
                }
            }
        });

        tagRv.setAdapter(tagAdapter=new TagAdapter(this,
                (TextView) findViewById(R.id.tag_count_hint)));
        tagRv.setLayoutManager(new LinearLayoutManager(this));
    }

    MenuItem saveTag,saveName,saveDescription;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_play_list_info,menu);
        saveName=menu.findItem(R.id.edit_info_save_name).setVisible(toolBarMode==TOOLBAR_MODE_EDIT_NAME);
        saveDescription=menu.findItem(R.id.edit_info_save_description).setVisible(toolBarMode==TOOLBAR_MODE_EDIT_DESCRIPTION);
        saveTag=menu.findItem(R.id.edit_info_save_tag).setVisible(toolBarMode==TOOLBAR_MODE_EDIT_TAG);
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
                saveDescription.setEnabled(false);
                Map<String,String> dataD=new HashMap<>();
                dataD.put("playListId",playList.getId()+"");
                dataD.put("playListIntroduction",newPlaylistDescription.getText().toString());
                PlayListHttpUtil.savePlayListIntroduction(this,
                        gson.toJson(dataD),new EditPLHandler(this));
                break;
            case R.id.edit_info_save_tag:
                saveTag.setEnabled(false);
                List<Integer> data=new ArrayList<>();
                data.add(playList.getId());
                data.addAll(tagAdapter.getSelected());
                TagHttpUtil.savePlayListTag(this,
                        gson.toJson(data),new EditPLHandler(this));
                break;
            case R.id.edit_info_save_name:
                saveName.setEnabled(false);
                Map<String,String> map=new HashMap<>();
                map.put("playListId",playList.getId()+"");
                map.put("playListName",newPlaylistName.getText().toString());
                PlayListHttpUtil.savePlayListName(this,
                        gson.toJson(map),new EditPLHandler(this));
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
                pbTag.setVisibility(View.GONE);
                description.setVisibility(View.GONE);
                mainContent.setVisibility(View.VISIBLE);

                break;
            case TOOLBAR_MODE_EDIT_NAME:
                title="歌单名称";
                newPlaylistName.setVisibility(View.VISIBLE);
                tagHint.setVisibility(View.GONE);
                tagRv.setVisibility(View.GONE);
                pbTag.setVisibility(View.GONE);
                description.setVisibility(View.GONE);
                mainContent.setVisibility(View.GONE);
                newPlaylistName.setText(playListName.getText());
                break;
            case TOOLBAR_MODE_EDIT_TAG:
                title="选择标签";
                newPlaylistName.setVisibility(View.GONE);
                tagHint.setVisibility(View.VISIBLE);
                pbTag.setVisibility(View.VISIBLE);
                description.setVisibility(View.GONE);
                mainContent.setVisibility(View.GONE);
                TagHttpUtil.getPlayListTag(this,new EditPLHandler(this));
                break;
            case TOOLBAR_MODE_EDIT_DESCRIPTION:
                title="歌单介绍";
                newPlaylistName.setVisibility(View.GONE);
                tagHint.setVisibility(View.GONE);
                tagRv.setVisibility(View.GONE);
                pbTag.setVisibility(View.GONE);
                description.setVisibility(View.VISIBLE);
                mainContent.setVisibility(View.GONE);
                newPlaylistDescription.setText(playListIntroduction.getText());
                break;
        }

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(title);
        }

        InputMethodUtil.hideSoftKeyboard(this);
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
                openAlbum();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case CHOOSE_PHOTO:
                if(data!=null&&data.getData()!=null){
                    Uri uri=data.getData();
                    String imagePath= new RealPathFromUriUtils(this).getPath(uri);
                    PlayListHttpUtil.uploadPlayListCover(this,imagePath,
                            playList.getId()+"",new EditPLHandler(this));
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    static class EditPLHandler extends Handler{
        private EditPlayListInfoActivity activity;
        EditPLHandler(EditPlayListInfoActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TagHttpUtil.MESSAGE_GET_PLAY_LIST_TAG_END:
                    List<PlayListBigTag> dataList = new Gson().fromJson((String)msg.obj,
                            new TypeToken<List<PlayListBigTag>>(){}.getType());
                    activity.tagAdapter.setDataList(dataList);

                    List<Integer> selected=new ArrayList<>();
                    for(PlayListSmallTag tag:activity.playList.getPlayListTag()){
                        selected.add(tag.getId());
                    }
                    activity.tagAdapter.toSelect(selected);

                    activity.pbTag.setVisibility(View.GONE);
                    activity.tagRv.setVisibility(View.VISIBLE);
                    break;
                case TagHttpUtil.MESSAGE_SAVE_PLAY_LIST_TAG_END:
                    List<PlayListSmallTag> data=activity.gson.fromJson((String)msg.obj,
                            new TypeToken<List<PlayListSmallTag>>(){}.getType());

                    activity.playList.setPlayListTag(data);
                    StringBuilder tag=new StringBuilder();
                    for(PlayListSmallTag smallTag:data){
                        tag.append(smallTag.getName());
                        tag.append(",");
                    }
                    if(tag.length()!=0){
                        tag.deleteCharAt(tag.length()-1);
                    }
                    activity.playListTag.setText(tag.toString());
                    activity.saveTag.setEnabled(true);
                    Toast.makeText(activity, "保存成功", Toast.LENGTH_SHORT).show();
                    break;
                case PlayListHttpUtil.MESSAGE_SAVE_NAME_END:
                    String playListName= (String) msg.obj;
                    activity.playList.setPlayListName(playListName);
                    activity.playListName.setText(playListName);
                    activity.saveName.setEnabled(true);
                    Toast.makeText(activity, "保存成功", Toast.LENGTH_SHORT).show();
                    break;
                case PlayListHttpUtil.MESSAGE_SAVE_INTRODUCTION_END:
                    activity.playList.setIntroduction(activity.newPlaylistDescription.getText().toString());
                    activity.playListIntroduction.setText(activity.playList.getIntroduction());
                    activity.saveDescription.setEnabled(true);
                    Toast.makeText(activity, "保存成功", Toast.LENGTH_SHORT).show();
                    break;
                case PlayListHttpUtil.MESSAGE_UPLOAD_PL_COVER_END:
                    activity.playListCover.refresh();
                    Toast.makeText(activity, "封面上传成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
