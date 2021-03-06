package com.oounabaramusic.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.oounabaramusic.android.util.StatusBarUtil;

public class TitlePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.hiddenStatusBar(this);
        setContentView(R.layout.activity_title_page);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent;

                SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(TitlePageActivity.this);
                boolean f=sp.getBoolean("login",false);
                if(f){
                    intent=new Intent(TitlePageActivity.this,MainActivity.class);
                }else{
                    intent=new Intent(TitlePageActivity.this,ChooseLoginActivity.class);
                }

                startActivity(intent);
                TitlePageActivity.this.finish();

            }
        }).start();
    }
}
