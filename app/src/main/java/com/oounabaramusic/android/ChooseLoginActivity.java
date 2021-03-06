package com.oounabaramusic.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.StatusBarUtil;

public class ChooseLoginActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTranslucentStatus(this);
        setContentView(R.layout.activity_choose_login);
        findViewById(R.id.choose_login_phone).setOnClickListener(this);
        findViewById(R.id.choose_login_tourist).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent=null;
        switch (view.getId()) {
            case R.id.choose_login_phone:
                intent = new Intent(this, PhotoLoginActivity.class);
                startActivity(intent);
                break;
            case R.id.choose_login_tourist:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
