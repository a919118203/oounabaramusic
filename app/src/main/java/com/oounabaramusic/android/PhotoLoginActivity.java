package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oounabaramusic.android.okhttputil.HttpUtil;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.util.StringUtil;

import java.lang.ref.WeakReference;

public class PhotoLoginActivity extends BaseActivity {

    private EditText photo,verificationCode;
    private String strPhoto;
    private Handler afterHandler=new AfterHandler(this),
            beforeHandler=new BeforeHandler(this);

    private TextView getVerificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_login);
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
        photo=findViewById(R.id.photo);
        verificationCode=findViewById(R.id.verification_code);
        getVerificationCode=findViewById(R.id.get_verification_code);

        SMSSDK.registerEventHandler(new EventHandler(){

            @Override
            public void afterEvent(int i, int i1, Object o) {
                Message msg=new Message();
                msg.arg1=i;            //event
                msg.arg2=i1;           //result
                msg.obj=o;             //data
                afterHandler.sendMessage(msg);
            }

            @Override
            public void beforeEvent(int i, Object o) {
                Message msg=new Message();
                msg.arg1=i;            //event
                msg.obj=o;             //data
                beforeHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void onLogin(View view){
//        String strVerificationCode=verificationCode.getText().toString();
//
//        if(strPhoto==null){
//            Toast.makeText(this,"你还没去获取验证码呢",Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if(strVerificationCode.length()==0){
//            Toast.makeText(this, "还没写验证码呢", Toast.LENGTH_SHORT).show();
//        }else if(strVerificationCode.length()!=4){
//            Toast.makeText(this, "验证码得4位", Toast.LENGTH_SHORT).show();
//        }else{
//            if(StringUtil.checkAllNumbers(strVerificationCode)){
//                SMSSDK.submitVerificationCode("86",strPhoto,strVerificationCode);
//            }else{
//                Toast.makeText(this, "请输入数字", Toast.LENGTH_SHORT).show();
//            }
//        }
        strPhoto=photo.getText().toString();
        login();
    }

    public void onGetVerificationCode(View view){
        getVerificationCode.setEnabled(false);
        getVerificationCode.setTextColor(getResources().getColor(R.color.negative));
        new Thread(new Runnable() {
            int timeSum=60;
            @Override
            public void run() {
                while(timeSum!=0){
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getVerificationCode.setText("("+timeSum+"S)");
                            }
                        });
                        timeSum--;
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getVerificationCode.setEnabled(true);
                        getVerificationCode.setTextColor(getResources().getColor(R.color.colorPrimary));
                        getVerificationCode.setText("获取验证码");
                    }
                });
            }
        }).start();


        strPhoto=photo.getText().toString();
        if(strPhoto.length()==0){
            Toast.makeText(this, "还没写电话号码呢", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!StringUtil.checkAllNumbers(strPhoto)){
            Toast.makeText(this, "输入全数字", Toast.LENGTH_SHORT).show();
            return;
        }
        SMSSDK.getVerificationCode("86",strPhoto);
    }

    public void login(){
        HttpUtil.login(strPhoto,this);
    }

    static class AfterHandler extends Handler {

        private PhotoLoginActivity activity;

        AfterHandler(PhotoLoginActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            int event = msg.arg1;
            int result = msg.arg2;
            Object obj = msg.obj;

            switch (event) {
                case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                    if (result == SMSSDK.RESULT_ERROR) {
                        Toast.makeText(activity, "获取失败,检查一哈电话号码", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        login();
                    } else {
                        Toast.makeText(activity, "验证码错误", Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }

        private void login(){
            activity.login();
        }
    }


    static class BeforeHandler extends Handler{

        private PhotoLoginActivity activity;

        BeforeHandler(PhotoLoginActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            int event=msg.arg1;
            switch (event){
                case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                    Toast.makeText(activity, "正在获取验证码...", Toast.LENGTH_SHORT).show();
                    break;
                case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                    Toast.makeText(activity, "正在提交验证码...", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
