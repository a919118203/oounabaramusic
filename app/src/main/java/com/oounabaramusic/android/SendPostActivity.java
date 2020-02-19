package com.oounabaramusic.android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.Post;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.PostHttpUtil;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.RealPathFromUriUtils;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.widget.customview.MyImageView;

import java.util.Objects;

public class SendPostActivity extends BaseActivity implements View.OnClickListener,View.OnLongClickListener{

    private static final int CHOOSE_MUSIC=2;
    private FrameLayout chooseImage;
    private String imagePath;
    private ImageView image;
    private TextView imageTv;

    private FrameLayout chooseMusic;
    private LinearLayout displayMusic;
    private MyImageView cover;
    private TextView name,singer;
    private Music music;

    private EditText postContent;
    private TextView contentCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_post);
        StatusBarUtil.setWhiteStyleStatusBar(this);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init(){
        chooseImage = findViewById(R.id.choose_image);
        imageTv=findViewById(R.id.image_text);
        image=findViewById(R.id.image);
        chooseMusic=findViewById(R.id.choose_music);
        displayMusic=findViewById(R.id.display_music);
        cover=findViewById(R.id.cover);
        name=findViewById(R.id.name);
        singer=findViewById(R.id.singer);
        postContent=findViewById(R.id.post_content);
        contentCnt=findViewById(R.id.text_cnt);

        chooseImage.setOnClickListener(this);
        chooseMusic.setOnClickListener(this);
        chooseImage.setOnLongClickListener(this);
        chooseMusic.setOnLongClickListener(this);

        Drawable add = getResources().getDrawable(R.mipmap.add);
        add.setBounds(0,40,50,90);
        imageTv.setCompoundDrawables(null,add,null,null);

        displayMusic.setVisibility(View.GONE);

        contentCnt.setText(String.valueOf(Post.MAX_LEN));
        postContent.addTextChangedListener(new TextWatcher() {
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

    private void setImage(){
        image.setVisibility(View.VISIBLE);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        image.setImageBitmap(bitmap);
    }

    private void clearImage(){
        image.setVisibility(View.GONE);
        imagePath=null;
    }

    private void setMusic(){
        displayMusic.setVisibility(View.VISIBLE);
        cover.setImage(new MyImage(
                MyImage.TYPE_SINGER_COVER,
                Integer.valueOf(music.getSingerId().split("/")[0])));
        name.setText(music.getMusicName());
        singer.setText(music.getSingerName().replace("/"," "));
    }

    private void clearMusic(){
        displayMusic.setVisibility(View.GONE);
        music=null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send_post,menu);
        return true;
    }

    private void sendPost(){
        String content = postContent.getText().toString();

        Post post = new Post();
        if(music!=null){
            post.setContentType(Post.MUSIC);
            post.setContentId(music.getId());
        }else{
            post.setContentType(Post.TEXT);
        }
        post.setContent(content);
        post.setUserId(SharedPreferencesUtil.getUserId(sp));

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
            case android.R.id.home:
                finish();
                break;
            case R.id.send_post:
                int len = Integer.valueOf(contentCnt.getText().toString());
                if(len<0){
                    Toast.makeText(this, "内容过长", Toast.LENGTH_SHORT).show();
                    break;
                }
                sendPost();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.choose_image:
                openAlbum();
                break;
            case R.id.choose_music:
                Intent intent = new Intent(this,ChooseMusicActivity.class);
                startActivityForResult(intent,CHOOSE_MUSIC);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.choose_image:
                clearImage();
                break;
            case R.id.choose_music:
                clearMusic();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case CHOOSE_MUSIC:
                if(data!=null) {
                    this.music= (Music) data.getSerializableExtra("music");
                    setMusic();
                }
                break;
            case CHOOSE_PHOTO:
                if(data!=null&&data.getData()!=null){
                    Uri uri = data.getData();
                    imagePath=new RealPathFromUriUtils(this).getPath(uri);
                    setImage();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    static class MyHandler extends Handler{
        SendPostActivity activity;
        MyHandler(SendPostActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.SEND_MESSAGE:
                    if(activity.imagePath!=null){
                        String postId = (String) msg.obj;
                        PostHttpUtil.uploadPostImage(activity,activity.imagePath,postId,this);
                    }else{
                        Toast.makeText(activity, "发表成功", Toast.LENGTH_SHORT).show();
                        activity.finish();
                    }
                    break;

                case BasicCode.UPLOAD_POST_IMAGE:
                    Toast.makeText(activity, "发表成功", Toast.LENGTH_SHORT).show();
                    activity.finish();
                    break;
            }
        }
    }
}
