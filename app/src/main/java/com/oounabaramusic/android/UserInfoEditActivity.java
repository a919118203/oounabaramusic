package com.oounabaramusic.android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oounabaramusic.android.bean.User;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.okhttputil.UserHttpUtil;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.InputMethodUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.RealPathFromUriUtils;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.widget.customview.ChooseDateView;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;

public class UserInfoEditActivity extends BaseActivity implements View.OnClickListener{

    private static final int MAX_INTRODUCTION_SIZE=300;
    
    private static final int MODE_NORMAL=0;
    private static final int MODE_EDIT_NAME =1;
    private static final int MODE_EDIT_INTRODUCTION=2;
    private int mode;
    private EditText editUserName;
    private EditText editUserIntroduction;
    private TextView len;
    private LinearLayout layoutContent;
    private FrameLayout editIntroduction;
    private User user;

    private MyCircleImageView userHeader;
    private TextView userName,userSex,userBirthday,userIntroduction;

    private AlertDialog sexDialog,calendarDialog;
    private ChooseDateView cdv;

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

        user= (User) getIntent().getSerializableExtra("user");

        init();
    }

    private void init() {
        findViewById(R.id.user_header).setOnClickListener(this);
        findViewById(R.id.user_name).setOnClickListener(this);
        findViewById(R.id.user_sex).setOnClickListener(this);
        findViewById(R.id.user_birthday).setOnClickListener(this);
        findViewById(R.id.user_introduction).setOnClickListener(this);
        editUserName=findViewById(R.id.edit_user_name);
        editUserIntroduction=findViewById(R.id.edit_introduction_content);
        layoutContent=findViewById(R.id.layout_content);
        editIntroduction=findViewById(R.id.edit_introduction);
        userHeader=findViewById(R.id.user_header_content);
        userName=findViewById(R.id.user_name_content);
        userSex=findViewById(R.id.user_sex_content);
        userBirthday=findViewById(R.id.user_birthday_content);
        userIntroduction=findViewById(R.id.user_introduction_content);
        len=findViewById(R.id.len);

        sexDialog =new AlertDialog.Builder(this)
                .setView(createContentView())
                .create();
        sexDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.layout_card_2));

        initCalendar();

        editUserIntroduction.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                len.setText(String.valueOf(MAX_INTRODUCTION_SIZE-s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        initContent();
    }

    private void initCalendar(){
        cdv=new ChooseDateView(this);

        calendarDialog=new AlertDialog.Builder(this)
                .setView(cdv)
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        User data = new User();
                        data.setId(user.getId());
                        data.setBirthday(cdv.getDate());

                        new S2SHttpUtil(
                                UserInfoEditActivity.this,
                                gson.toJson(data),
                                MyEnvironment.serverBasePath+"updateUser",
                                new MyHandler(UserInfoEditActivity.this))
                                .call(BasicCode.UPDATE_USER_BIRTHDAY_END);
                        calendarDialog.dismiss();
                        user.setBirthday(cdv.getDate());
                        userBirthday.setText(FormatUtil.DateToString(cdv.getDate()));
                    }
                })
                .create();
        calendarDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.layout_card_2));
    }

    private void initContent(){
        userHeader.setImageUrl(MyEnvironment.serverBasePath+
                "loadUserHeader?userId="+user.getId());

        userName.setText(user.getUserName());
        userSex.setText(user.getSex());
        userBirthday.setText(FormatUtil.DateToString(user.getBirthday()));
        userIntroduction.setText(user.getIntroduction());
    }

    private View createContentView() {
        View contentView= LayoutInflater.from(this).inflate(
                R.layout.pw_choose_sex,
                (ViewGroup) getWindow().getDecorView(),
                false);

        contentView.findViewById(R.id.nan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User data = new User();
                data.setId(user.getId());
                data.setSex("男");

                new S2SHttpUtil(
                        UserInfoEditActivity.this,
                        gson.toJson(data),
                        MyEnvironment.serverBasePath+"updateUser",
                        new MyHandler(UserInfoEditActivity.this))
                        .call(BasicCode.UPDATE_USER_SEX_END);
                sexDialog.dismiss();
                userSex.setText("男");
                user.setSex("男");
            }
        });

        contentView.findViewById(R.id.nv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User data = new User();
                data.setId(user.getId());
                data.setSex("女");

                new S2SHttpUtil(
                        UserInfoEditActivity.this,
                        gson.toJson(data),
                        MyEnvironment.serverBasePath+"updateUser",
                        new MyHandler(UserInfoEditActivity.this))
                        .call(BasicCode.UPDATE_USER_SEX_END);
                sexDialog.dismiss();
                userSex.setText("女");
                user.setSex("女");
            }
        });
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
                String userName = editUserName.getText().toString();
                
                if(userName.length()==0){
                    Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
                }else if(userName.length()<=25){
                    User data = new User();
                    data.setId(user.getId());
                    data.setUserName(userName);

                    new S2SHttpUtil(
                            this,
                            gson.toJson(data),
                            MyEnvironment.serverBasePath+"updateUser",
                            new MyHandler(this))
                            .call(BasicCode.UPDATE_USER_USERNAME_END);

                    switchMode(MODE_NORMAL);
                }else{
                    Toast.makeText(this, "名字过长", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.save_introduction:
                String introduction = editUserIntroduction.getText().toString();
                
                if(introduction.length()<=MAX_INTRODUCTION_SIZE){
                    User data = new User();
                    data.setId(user.getId());
                    data.setIntroduction(introduction);

                    new S2SHttpUtil(
                            this,
                            gson.toJson(data),
                            MyEnvironment.serverBasePath+"updateUser",
                            new MyHandler(this))
                            .call(BasicCode.UPDATE_USER_INTRODUCTION_END);

                    switchMode(MODE_NORMAL);
                }else{
                    Toast.makeText(this, "签名过长", Toast.LENGTH_SHORT).show();
                }
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
                InputMethodUtil.hideSoftKeyboard(this);
                break;
            case MODE_EDIT_NAME:
                title="修改昵称";
                editUserName.setVisibility(View.VISIBLE);
                editUserName.requestFocus();
                editUserName.setText(user.getUserName());
                InputMethodUtil.showSoftKeyboard(this,editUserName);
                editUserName.selectAll();
                layoutContent.setVisibility(View.GONE);
                editIntroduction.setVisibility(View.GONE);
                break;
            case MODE_EDIT_INTRODUCTION:
                title="修改签名";
                len.setText(String.valueOf(MAX_INTRODUCTION_SIZE-user.getIntroduction().length()));
                editUserIntroduction.requestFocus();
                editUserIntroduction.setText(user.getIntroduction());
                InputMethodUtil.showSoftKeyboard(this,editUserIntroduction);
                editUserName.selectAll();
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
                sexDialog.show();
                break;
            case R.id.user_birthday:
                cdv.setCurrentDate(user.getBirthday());
                calendarDialog.show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if(data!=null&&data.getData()!=null) {
                    Uri uri = data.getData();
                    String imagePath = new RealPathFromUriUtils(this).getPath(uri);
                    UserHttpUtil.uploadUserHeader(this,imagePath,
                            user.getId()+"",new MyHandler(this));
                    Bitmap bit=BitmapFactory.decodeFile(imagePath);
                    userHeader.setImageBitmap(bit);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    static class MyHandler extends Handler{
        UserInfoEditActivity activity;
        MyHandler(UserInfoEditActivity activity){
            this.activity=activity;
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.UPLOAD_USER_HEADER_END:
                    Toast.makeText(activity, "头像上传成功", Toast.LENGTH_SHORT).show();
                    break;
                case BasicCode.UPDATE_USER_USERNAME_END:
                    String userName=activity.editUserName.getText().toString();
                    activity.user.setUserName(userName);
                    activity.userName.setText(userName);
                    SharedPreferences.Editor editor=activity.sp.edit();
                    editor.putString("userName",userName);
                    editor.apply();
                    Toast.makeText(activity, "成功修改昵称", Toast.LENGTH_SHORT).show();
                    break;
                case BasicCode.UPDATE_USER_SEX_END:
                    Toast.makeText(activity, "成功修改性别", Toast.LENGTH_SHORT).show();
                    break;
                case BasicCode.UPDATE_USER_BIRTHDAY_END:
                    Toast.makeText(activity, "成功修改生日", Toast.LENGTH_SHORT).show();
                    break;
                case BasicCode.UPDATE_USER_INTRODUCTION_END:
                    String i = activity.editUserIntroduction.getText().toString();
                    activity.user.setIntroduction(i);
                    activity.userIntroduction.setText(i);
                    Toast.makeText(activity, "成功修改签名", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
