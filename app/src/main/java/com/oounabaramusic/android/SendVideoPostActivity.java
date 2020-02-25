package com.oounabaramusic.android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.Post;
import com.oounabaramusic.android.bean.Video;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.HttpUtil;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.okhttputil.VideoHttpUtil;
import com.oounabaramusic.android.service.UploadVideoService;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.RealPathFromUriUtils;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.util.VideoUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SendVideoPostActivity extends BaseActivity implements View.OnClickListener{

    private TextView chooseVideo;
    private ImageView videoCover;
    private String filePath;

    private EditText title;
    private TextView titleCnt;
    private EditText content;
    private TextView contentCnt;

    public static void startActivity(Context context){
        Intent intent = new Intent(context,SendVideoPostActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_video_post);
        StatusBarUtil.setWhiteStyleStatusBar(this);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send_video,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.upload:
                if(checkSend()){
                    startUpload();

                    Toast.makeText(this, "正在上传", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startUpload(){
        Intent intent = new Intent(this, UploadVideoService.class);

        Post post = new Post();
        post.setContentId(0);
        post.setContentType(Post.VIDEO);
        post.setContent(content.getText().toString());
        post.setUserId(SharedPreferencesUtil.getUserId(sp));

        Video video = new Video();
        video.setTitle(title.getText().toString());
        video.setUserId(SharedPreferencesUtil.getUserId(sp));
        video.setDuration(VideoUtil.getVideoLen(filePath));

        Map<String,String> data = new HashMap<>();
        data.put("post",gson.toJson(post));
        data.put("video",gson.toJson(video));

        intent.putExtra("json",gson.toJson(data));
        intent.putExtra("filePath",filePath);
        startService(intent);
    }

    private void init(){
        chooseVideo=findViewById(R.id.choose_video);
        videoCover=findViewById(R.id.cover);
        title=findViewById(R.id.title);
        titleCnt=findViewById(R.id.title_cnt);
        content=findViewById(R.id.content);
        contentCnt=findViewById(R.id.content_cnt);

        titleCnt.setText(String.valueOf(Video.NAME_MAX_LEN));
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                titleCnt.setText(String.valueOf(Video.NAME_MAX_LEN-s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        contentCnt.setText(String.valueOf(Post.MAX_LEN));
        content.addTextChangedListener(new TextWatcher() {
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

        chooseVideo.setOnClickListener(this);
    }

    private boolean checkSend(){
        if(filePath==null){
            Toast.makeText(this, "请选择视频", Toast.LENGTH_SHORT).show();
            return false;
        }

        File file = new File(filePath);
        if(file.exists()){
            if(file.length()>(long)Integer.MAX_VALUE){
                Toast.makeText(this, "视频文件过大，不能大于"+
                        FormatUtil.fileSizeFormat(Integer.MAX_VALUE), Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(title.length()==0){
            Toast.makeText(this, "必须输入标题", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(title.length()>Video.NAME_MAX_LEN){
            Toast.makeText(this, "标题过长", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(content.length()>Post.MAX_LEN){
            Toast.makeText(this, "简介过长", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.choose_video:
                openVideoAlbum();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case CHOOSE_VIDEO:
                if(data!=null){
                    if(data.getData()!=null){
                        Uri uri = data.getData();
                        filePath = new RealPathFromUriUtils(this).getVideoPath(uri);
                        videoCover.setImageBitmap(VideoUtil.getVideoCover(filePath));
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
