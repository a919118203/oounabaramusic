package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.oounabaramusic.android.bean.Post;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.util.StatusBarUtil;

public class ForwardActivity extends BaseActivity{

    private EditText forwardContent;
    private TextView contentCnt;

    private int postId;

    private MenuItem send;
    public static void startActivity(Context context,int postId){
        Intent intent = new Intent(context,ForwardActivity.class);
        intent.putExtra("postId",postId);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward);
        StatusBarUtil.setWhiteStyleStatusBar(this);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        postId=intent.getIntExtra("postId",0);

        init();

    }

    private void init() {
        forwardContent=findViewById(R.id.forward_content);
        contentCnt=findViewById(R.id.content_cnt);

        contentCnt.setText(String.valueOf(Post.MAX_LEN));
        forwardContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contentCnt.setText(String.valueOf(Post.MAX_LEN-s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forward,menu);
        send=menu.getItem(0);
        return true;
    }

    private void sendPost(){
        send.setEnabled(false);
        String content = forwardContent.getText().toString();
        if(content.isEmpty()){
            content="转发动态";
        }

        Post post=new Post();
        post.setUserId(SharedPreferencesUtil.getUserId(sp));
        post.setContent(content);
        post.setContentType(Post.FORWARD);
        post.setContentId(postId);

        new S2SHttpUtil(
                this,
                gson.toJson(post),
                MyEnvironment.serverBasePath+"addPost",
                new MyHandler(this))
                .call(BasicCode.SEND_MESSAGE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.send_forward:
                int cnt = Integer.valueOf(contentCnt.getText().toString());
                if(cnt<0){
                    Toast.makeText(this, "内容过长", Toast.LENGTH_SHORT).show();
                    break;
                }
                sendPost();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    static class MyHandler extends Handler{
        ForwardActivity activity;
        MyHandler(ForwardActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.SEND_MESSAGE:
                    Toast.makeText(activity, "发表成功", Toast.LENGTH_SHORT).show();
                    activity.finish();
                    break;
            }
        }
    }
}
