package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.User;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.fragment.BaseFragment;
import com.oounabaramusic.android.fragment.UserInfoMainFragment;
import com.oounabaramusic.android.fragment.UserInfoPostFragment;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.okhttputil.UserHttpUtil;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.RealPathFromUriUtils;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.util.UserInfoActivityManager;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;
import com.oounabaramusic.android.widget.customview.MyImageView;
import com.oounabaramusic.android.widget.popupwindow.MyBottomSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener{

    private int mode;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private List<BaseFragment> fragments;
    private RelativeLayout header;
    private TabLayout tabLayout;
    private int mainUserId,contentUserId;

    private MyImageView background;
    private View backgroundBlock;
    private MyCircleImageView userHeader;//用户头像
    private TextView userName;
    private LinearLayout userFollowed;//粉丝
    private LinearLayout userToFollow;//关注
    private TextView followedCnt,toFollowCnt;

    private TextView changeBackground;
    private TextView editInfo;
    private TextView sendMsg;
    private TextView follow,cancelFollow;

    private User user;

    private boolean first;   //第二次调用onResume时重新加载数据

    public static void startActivity(Context context,int userId){
        Intent intent = new Intent(context,UserInfoActivity.class);
        intent.putExtra("userId",userId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        UserInfoActivityManager.addActivity(this);
        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.moveDownStatusBar(this,R.id.toolbar);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mainUserId = SharedPreferencesUtil.getUserId(sp);
        contentUserId = getIntent().getIntExtra("userId",0);
        init();

    }

    public int getContentUserId() {
        return contentUserId;
    }

    public void moveToMain(){
        Objects.requireNonNull(tabLayout.getTabAt(0)).select();
    }

    private void init() {
        first=true;

        userFollowed=findViewById(R.id.user_followed);
        userToFollow=findViewById(R.id.user_to_follow);
        userHeader=findViewById(R.id.user_cover);
        userName=findViewById(R.id.user_name);
        header=findViewById(R.id.user_info_header);
        tabLayout=findViewById(R.id.user_tab_layout);
        viewPager=findViewById(R.id.user_view_pager);
        background=findViewById(R.id.background);
        backgroundBlock=findViewById(R.id.background_block);
        editInfo=findViewById(R.id.user_edit);
        sendMsg=findViewById(R.id.user_send_message);
        changeBackground=findViewById(R.id.user_change_background);
        follow=findViewById(R.id.user_follow);
        cancelFollow=findViewById(R.id.user_cancel_follow);
        followedCnt=findViewById(R.id.followed_cnt);
        toFollowCnt=findViewById(R.id.to_follow_cnt);

        userHeader.setOnClickListener(this);
        userFollowed.setOnClickListener(this);
        userToFollow.setOnClickListener(this);
        editInfo.setOnClickListener(this);
        changeBackground.setOnClickListener(this);
        follow.setOnClickListener(this);
        cancelFollow.setOnClickListener(this);
        sendMsg.setOnClickListener(this);

        final AppBarLayout appBarLayout=findViewById(R.id.appbar);
        final TextView title=findViewById(R.id.toolbar_title);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {

            boolean f=false;//toolbar标题
            boolean f1=false; //header

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                i=Math.abs(i);
                double change=((double)i/(double) appBarLayout.getTotalScrollRange())*70f;

                if(!f&&change>69){
                    f=true;
                    title.setText(userName.getText());
                }else if(f&&change<=69){
                    f=false;
                    title.setText("");
                }

                if(!f1&&change>60){
                    f1=true;
                    header.setVisibility(View.GONE);
                }else if(f1&&change<=60){
                    f1=false;
                    header.setVisibility(View.VISIBLE);
                }
            }
        });


        fragments=new ArrayList<>();
        fragments.add(new UserInfoMainFragment(this));
        fragments.add(new UserInfoPostFragment(this));
        viewPager.setAdapter(new UserFragmentPagerAdapter());
        tabLayout.setupWithViewPager(viewPager);

        initContent(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!first){
            initContent(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        first=false;
    }

    /**
     *
     * @param f  是否要获取用户信息
     */
    private void initContent(boolean f){
        if(f){
            Map<String,Integer> data = new HashMap<>();
            data.put("contentUserId",contentUserId);
            data.put("mainUserId",mainUserId);

            new S2SHttpUtil(
                    this,
                    gson.toJson(data),
                    MyEnvironment.serverBasePath+"getUserInfo",
                    new MyHandler(this))
                    .call(BasicCode.GET_USER_INFO_END);
            return ;
        }

        background.setDefaultImage(BitmapFactory.decodeResource(getResources(),R.mipmap.default_background));
        background.setImage(new MyImage(
                MyImage.TYPE_USER_BACKGROUND,user.getId()));
        userHeader.setImageUrl(MyEnvironment.serverBasePath+
                "loadUserHeader?userId="+user.getId());
        userName.setText(user.getUserName());
        followedCnt.setText(String.valueOf(user.getFollowedCnt()));
        toFollowCnt.setText(String.valueOf(user.getToFollowCnt()));

        initMode();
    }

    private void initMode() {
        if(mainUserId==contentUserId){
            selfMode();
        }else {
            otherMode();
        }
    }

    private void selfMode() {
        changeBackground.setVisibility(View.VISIBLE);
        editInfo.setVisibility(View.VISIBLE);
    }

    private void otherMode() {
        sendMsg.setVisibility(View.VISIBLE);
        if(user.getFollowed()){
            follow.setVisibility(View.GONE);
            cancelFollow.setVisibility(View.VISIBLE);
        }else{
            follow.setVisibility(View.VISIBLE);
            cancelFollow.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            UserInfoActivityManager.removeActivity(this);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.user_cover:             //点击头像时
                showChangeHeaderPopupWindow();
                break;
            case R.id.user_to_follow:         //点击关注时
                MyFriendActivity.startActivity(
                        this,
                        user.getId(),
                        MyFriendActivity.TO_FOLLOW);
                break;
            case R.id.user_followed:          //点击粉丝时
                MyFriendActivity.startActivity(
                        this,
                        user.getId(),
                        MyFriendActivity.FOLLOWED);
                break;
            case R.id.user_edit:              //点击编辑
                intent=new Intent(this,UserInfoEditActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
                break;
            case R.id.user_change_background:  //点击更换背景
                openAlbum();
                break;

            case R.id.user_follow:
            case R.id.user_cancel_follow:
                Map<String,Integer> data =new HashMap<>();
                data.put("from", SharedPreferencesUtil.getUserId(sp));
                data.put("to",user.getId());

                new S2SHttpUtil(
                        this,
                        gson.toJson(data),
                        MyEnvironment.serverBasePath+"toFollowUser",
                        new MyHandler(this))
                .call(BasicCode.TO_FOLLOW_USER);
                break;

            case R.id.user_send_message:
                MessageActivity.startActivity(this,user.getId(),user.getUserName());
                break;
        }
    }

    private void showChangeHeaderPopupWindow() {
        View contentView= LayoutInflater
                .from(this)
                .inflate(R.layout.pw_change_header, (ViewGroup) getWindow().getDecorView(),false);

        ImageView iv=contentView.findViewById(R.id.user_header);
        iv.getLayoutParams().height=DensityUtil.getDisplayWidth(this);
        iv.setImageDrawable(userHeader.getDrawable());

        final AlertDialog dialog=new AlertDialog.Builder(this)
                .setView(contentView)
                .create();

        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.user_change_header){
                    Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent,CHOOSE_USER_HEADER_PHOTO);
                }
                dialog.dismiss();
            }
        };

        contentView.findViewById(R.id.user_change_header).setOnClickListener(listener);
        contentView.findViewById(R.id.user_header).setOnClickListener(listener);
        contentView.findViewById(R.id.rootLayout).setOnClickListener(listener);

        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.alpha_background));
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(data!=null&&data.getData()!=null){
            Uri uri=data.getData();
            String imagePath= new RealPathFromUriUtils(this).getPath(uri);

            switch (requestCode){
                case CHOOSE_PHOTO:     //更改背景
                    UserHttpUtil.uploadUserBackground(this,imagePath,
                            mainUserId+"",new MyHandler(this));

                    Bitmap bit=BitmapFactory.decodeFile(imagePath);
                    background.setImageBitmap(bit);
                    break;

                case CHOOSE_USER_HEADER_PHOTO:  //更换头像
                    UserHttpUtil.uploadUserHeader(this,imagePath,
                            mainUserId+"",new MyHandler(this));
                    bit=BitmapFactory.decodeFile(imagePath);
                    userHeader.setImageBitmap(bit);
                    break;
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    class UserFragmentPagerAdapter extends FragmentPagerAdapter{

        UserFragmentPagerAdapter() {
            super(UserInfoActivity.this.getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).getTitle();
        }
    }

    static class MyHandler extends Handler{
        private UserInfoActivity activity;
        MyHandler(UserInfoActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_USER_INFO_END:
                    activity.user=new Gson().fromJson((String) msg.obj,User.class);

                    //刷新主页碎片
                    ((UserInfoMainFragment)activity.fragments.get(0))
                            .setUser(activity.user);

                    activity.initContent(false);
                    break;

                case BasicCode.UPLOAD_USER_BACKGROUND_END:
                    Toast.makeText(activity, "背景上传成功", Toast.LENGTH_SHORT).show();
                    break;

                case BasicCode.UPLOAD_USER_HEADER_END:
                    Toast.makeText(activity, "头像上传成功", Toast.LENGTH_SHORT).show();
                    break;

                case BasicCode.TO_FOLLOW_USER:
                    Map<String,Integer> data = new Gson().fromJson((String) msg.obj,
                            new TypeToken<Map<String,Integer>>(){}.getType());

                    int followed = data.get("followed");
                    int followedCnt = data.get("followedCnt");
                    activity.user.setFollowed(followed==1);
                    activity.user.setFollowedCnt(followedCnt);
                    activity.otherMode();
                    activity.followedCnt.setText(String.valueOf(followedCnt));
                    break;
            }
        }
    }
}
