package com.oounabaramusic.android;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.fragment.MainMyFragment;
import com.oounabaramusic.android.fragment.MainNewDiscoveryFragment;
import com.oounabaramusic.android.fragment.MainNowFragment;
import com.oounabaramusic.android.fragment.MainVideoFragment;
import com.oounabaramusic.android.service.MusicPlayService;
import com.oounabaramusic.android.util.LinksTextViewAndViewPager;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends BaseActivity implements View.OnClickListener{


    private ImageView mainSetting,mainSearch;
    private ViewPager viewPager;
    private DrawerLayout dl;
    private List<Fragment> fragments=new ArrayList<>();
    private LinksTextViewAndViewPager link;
    private String userId,userName;

    private MyCircleImageView userHeader;
    private TextView userNameTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setTranslucentStatusAndDarkContent(this);
        StatusBarUtil.moveDownStatusBar(this);
        init();
    }

    private void init() {

        initDir();
        dl=findViewById(R.id.main_drawer_layout);
        mainSetting=findViewById(R.id.main_setting);
        mainSearch=findViewById(R.id.main_search);

        fragments.add(new MainMyFragment(this));
        fragments.add(new MainNewDiscoveryFragment(this));
        fragments.add(new MainNowFragment(this));
        fragments.add(new MainVideoFragment(this));

        viewPager=findViewById(R.id.main_view_pager);
        viewPager.setAdapter(new MainFragmentPagerAdapter());

        mainSearch.setOnClickListener(this);
        mainSetting.setOnClickListener(this);
        NavigationView view=findViewById(R.id.navigation_view);
        //view.findViewById(R.id.to_user_info).setOnClickListener(this);  //找不到该ID
        view.getHeaderView(0).setOnClickListener(this);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(!SharedPreferencesUtil.isLogin(sp)){
                    Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    return true;
                }

                Intent intent;
                int userId =  SharedPreferencesUtil.getUserId(sp);
                switch (menuItem.getItemId()){
                    case R.id.my_friends:
                        MyFriendActivity.startActivity(
                                MainActivity.this,
                                userId,
                                MyFriendActivity.TO_FOLLOW);
                        break;
                    case R.id.my_news:
                        intent=new Intent(MainActivity.this, MyMessageActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.quit:
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putBoolean("login",false);
                        editor.apply();

                        startActivity(new Intent(MainActivity.this,ChooseLoginActivity.class));
                        MainActivity.this.finish();
                        break;
                }
                dl.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        findViewById(R.id.main_setting).setOnClickListener(this);

        List<TextView> textViews=new ArrayList<>();
        textViews.add((TextView) findViewById(R.id.fragment_main_my));
        textViews.add((TextView) findViewById(R.id.fragment_main_new_discovery));
        textViews.add((TextView) findViewById(R.id.fragment_main_now));
        textViews.add((TextView) findViewById(R.id.fragment_main_video));
        link=new LinksTextViewAndViewPager(this,textViews,viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                link.select(position);
                if(position==1){
                    ((MainNewDiscoveryFragment)fragments.get(1)).setF(true);
                }else{
                    ((MainNewDiscoveryFragment)fragments.get(1)).setF(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        userHeader = view.getHeaderView(0).findViewById(R.id.user_header);
        userNameTV = view.getHeaderView(0).findViewById(R.id.user_name);

        initContent();
    }

    private void initDir() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
            return ;
        }
        File file=new File(MyEnvironment.cachePath);
        file.mkdirs();
        file=new File(MyEnvironment.musicPath);
        file.mkdirs();
        file=new File(MyEnvironment.musicLrc);
        file.mkdirs();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initContent();
    }

    private void initContent(){
        userId = sp.getString("userId","-1");
        userName = sp.getString("userName","null");

        if(SharedPreferencesUtil.isLogin(sp)){
            userHeader.setImage(new MyImage(
                    MyImage.TYPE_USER_HEADER,
                    Integer.valueOf(userId)));
            userNameTV.setText(userName);
        }else{
            userHeader.setImageBitmap(null);
            userNameTV.setText("请先登录");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==0){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                initDir();
            }
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.main_setting:                     //点击左上角菜单时
                dl.openDrawer(GravityCompat.START);
                break;
            case R.id.main_search:
                intent=new Intent(this,SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.to_user_info:                     //点击NavigationView的header部分时
                if(SharedPreferencesUtil.isLogin(sp)){
                    intent=new Intent(this,UserInfoActivity.class);
                    intent.putExtra("userId",Integer.valueOf(userId));
                    startActivity(intent);
                }else{
                    intent = new Intent(this,ChooseLoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                dl.closeDrawer(GravityCompat.START);
                break;
        }
    }

    private class MainFragmentPagerAdapter extends FragmentPagerAdapter {

        MainFragmentPagerAdapter() {
            super(MainActivity.this.getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
    }
}
