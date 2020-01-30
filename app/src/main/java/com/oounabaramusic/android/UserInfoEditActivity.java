package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.InputMethodUtil;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.widget.popupwindow.MyBottomSheetDialog;

public class UserInfoEditActivity extends BaseActivity implements View.OnClickListener{

    private static final int MODE_NORMAL=0;
    private static final int MODE_EDIT_NAME =1;
    private static final int MODE_EDIT_INTRODUCTION=2;
    private int mode;
    private EditText editUserName;
    private LinearLayout layoutContent;
    private FrameLayout editIntroduction;
    private MyBottomSheetDialog pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);
        StatusBarUtil.setWhiteStyleStatusBar(this);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        init();
    }

    private void init() {
        findViewById(R.id.user_header).setOnClickListener(this);
        findViewById(R.id.user_name).setOnClickListener(this);
        findViewById(R.id.user_sex).setOnClickListener(this);
        findViewById(R.id.user_birthday).setOnClickListener(this);
        findViewById(R.id.user_introduction).setOnClickListener(this);
        editUserName=findViewById(R.id.edit_user_name);
        layoutContent=findViewById(R.id.layout_content);
        editIntroduction=findViewById(R.id.edit_introduction);

        pw=new MyBottomSheetDialog(this);
        pw.setContentView(createContentView());
    }

    private View createContentView() {
        View contentView= LayoutInflater.from(this).inflate(
                R.layout.pw_choose_sex,
                (ViewGroup) getWindow().getDecorView(),
                false);

        contentView.getLayoutParams().width= (int) (DensityUtil.getDisplayWidth(this)*0.9);
        return contentView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_info_edit,menu);
        menu.findItem(R.id.save_name).setVisible(mode==MODE_EDIT_NAME);
        menu.findItem(R.id.save_introduction).setVisible(mode==MODE_EDIT_INTRODUCTION);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(mode!=MODE_NORMAL){
                    switchMode(MODE_NORMAL);
                }else{
                    finish();
                }
                InputMethodUtil.hideSoftKeyboard(this);
                break;
            case R.id.save_name:
                break;
            case R.id.save_introduction:
                break;
        }
        return true;
    }

    private void switchMode(int mode){
        this.mode=mode;
        invalidateOptionsMenu();

        String title="";
        switch (mode){
            case MODE_NORMAL:
                title="我的资料";
                editUserName.setVisibility(View.GONE);
                layoutContent.setVisibility(View.VISIBLE);
                editIntroduction.setVisibility(View.GONE);
                break;
            case MODE_EDIT_NAME:
                title="修改昵称";
                editUserName.setVisibility(View.VISIBLE);
                layoutContent.setVisibility(View.GONE);
                editIntroduction.setVisibility(View.GONE);
                break;
            case MODE_EDIT_INTRODUCTION:
                title="修改签名";
                editUserName.setVisibility(View.GONE);
                layoutContent.setVisibility(View.GONE);
                editIntroduction.setVisibility(View.VISIBLE);
                break;
        }

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(title);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_header:
                openAlbum();
                break;
            case R.id.user_name:
                switchMode(MODE_EDIT_NAME);
                break;
            case R.id.user_sex:
                pw.show();
                break;
            case R.id.user_birthday:
                break;
            case R.id.user_introduction:
                switchMode(MODE_EDIT_INTRODUCTION);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(mode!=MODE_NORMAL){
                switchMode(MODE_NORMAL);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
