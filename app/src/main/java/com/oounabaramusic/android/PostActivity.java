package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.adapter.CommentAdapter;
import com.oounabaramusic.android.adapter.PostAdapter;
import com.oounabaramusic.android.bean.Comment;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.Post;
import com.oounabaramusic.android.bean.Reply;
import com.oounabaramusic.android.bean.Video;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.fragment.BaseFragment;
import com.oounabaramusic.android.fragment.ForwardFragment;
import com.oounabaramusic.android.fragment.GoodFragment;
import com.oounabaramusic.android.fragment.PostCommentFragment;
import com.oounabaramusic.android.fragment.UserInfoMainFragment;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.InputMethodUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;
import com.oounabaramusic.android.widget.customview.MyImageView;
import com.oounabaramusic.android.widget.customview.MyVideoPlayer;
import com.oounabaramusic.android.widget.popupwindow.ShowImageDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PostActivity extends BaseActivity implements View.OnClickListener{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<BaseFragment> fragments;

    private int postId;
    private Post post;

    private EditText commentContent;
    private TextView send;

    private MyCircleImageView userHeader;
    private TextView userName;
    private TextView theme;
    private TextView date;
    private TextView content;

    private MyImageView image;

    private RelativeLayout music;
    private MyImageView musicCover;
    private TextView musicName;
    private TextView singerName;

    private MyVideoPlayer video;

    private LinearLayout forward;
    private TextView innerContent;
    private MyImageView innerImage;
    private RelativeLayout innerMusic;
    private MyImageView innerMusicCover;
    private TextView innerMusicName;
    private TextView innerSingerName;
    private MyVideoPlayer innerVideo;

    private ImageView toGood;
    private ImageView toForward;

    private Bitmap good,noGood;

    public static void startActivity(Context context,int postId){
        Intent intent = new Intent(context,PostActivity.class);
        intent.putExtra("postId",postId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        StatusBarUtil.setWhiteStyleStatusBar(this);
        setAutoCloseInputMethod(false);

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

    @Override
    protected void onResume() {
        super.onResume();
        initContent(true);
    }

    public int getPostId() {
        return postId;
    }

    private void init() {
        good = BitmapFactory.decodeResource(getResources(),R.mipmap.good);
        noGood = BitmapFactory.decodeResource(getResources(),R.mipmap.no_good);

        commentContent=findViewById(R.id.message_edit);
        send=findViewById(R.id.send);
        userHeader=findViewById(R.id.user_header);
        userName=findViewById(R.id.user_name);
        theme=findViewById(R.id.theme);
        date=findViewById(R.id.post_time);
        content=findViewById(R.id.post_content);
        image=findViewById(R.id.post_image);
        music=findViewById(R.id.post_music);
        musicCover=findViewById(R.id.music_cover);
        musicName=findViewById(R.id.music_name);
        singerName=findViewById(R.id.music_singer);
        forward=findViewById(R.id.inner);
        innerContent=findViewById(R.id.inner_content);
        innerImage=findViewById(R.id.inner_image);
        innerMusic=findViewById(R.id.inner_music);
        innerMusicCover=findViewById(R.id.forward_music_cover);
        innerMusicName=findViewById(R.id.forward_music_name);
        innerSingerName=findViewById(R.id.forward_music_singer);
        toGood=findViewById(R.id.to_good);
        toForward=findViewById(R.id.to_forward);
        video=findViewById(R.id.video);
        innerVideo=findViewById(R.id.inner_video);

        fragments=new ArrayList<>();
        fragments.add(new PostCommentFragment(this,commentContent));
        fragments.add(new ForwardFragment(this));
        fragments.add(new GoodFragment(this));

        tabLayout=findViewById(R.id.tab_layout);
        viewPager=findViewById(R.id.view_pager);
        viewPager.setAdapter(new ViewPagerAdapter());
        tabLayout.setupWithViewPager(viewPager);

        send.setOnClickListener(this);
        toGood.setOnClickListener(this);
        toForward.setOnClickListener(this);

        commentContent.setHint("留下无敌的评论...(最长"+ Comment.MAX_LEN+"个字)");
        innerContent.setMovementMethod(LinkMovementMethod.getInstance());

        findViewById(R.id.post_forward).setVisibility(View.GONE);
        findViewById(R.id.post_comment).setVisibility(View.GONE);
        findViewById(R.id.post_good).setVisibility(View.GONE);

        userName.setOnClickListener(this);
        userHeader.setOnClickListener(this);
        forward.setOnClickListener(this);
        innerContent.setOnClickListener(this);
        image.setOnClickListener(this);
        innerImage.setOnClickListener(this);
        music.setOnClickListener(this);
        innerMusic.setOnClickListener(this);

        //初始化video大小
        int width = DensityUtil.getDisplayWidth(this)-DensityUtil.dip2px(this,90);
        video.getLayoutParams().height= (int)(9f/16f*(double)width);
        video.getLayoutParams().width=width;
        video.requestLayout();

        //初始化里面video大小
        width = DensityUtil.getDisplayWidth(this)-DensityUtil.dip2px(this,110);
        innerVideo.getLayoutParams().height= (int)(9f/16f*(double)width);
        innerVideo.getLayoutParams().width=width;
        innerVideo.requestLayout();
    }

    private void initContent(boolean f){
        if(f){
            Post s = new Post();
            s.setId(postId);
            s.setMainUserId(SharedPreferencesUtil.getUserId(sp));

            new S2SHttpUtil(
                    this,
                    gson.toJson(s),
                    MyEnvironment.serverBasePath+"getPostInfo",
                    new MyHandler(this))
                    .call(BasicCode.GET_CONTENT);
            return;
        }

        if(post.getGooded()>0){
            toGood.setImageBitmap(good);
        }else{
            toGood.setImageBitmap(noGood);
        }
        userHeader.setImage(new MyImage(MyImage.TYPE_USER_HEADER,post.getUserId()));
        userName.setText(post.getUserName());
        theme.setText(Post.getTheme(post.getContentType()));
        date.setText(FormatUtil.DateTimeToString(post.getDate()));
        content.setText(post.getContent());
        if(post.getHasImage()){
            image.setVisibility(View.VISIBLE);
            image.setImage(new MyImage(MyImage.TYPE_POST_IMAGE,postId));
        }
        switch (post.getContentType()){
            case Post.MUSIC:
                Music item = new Music(post.getMusic());
                music.setVisibility(View.VISIBLE);
                musicCover.setImage(new MyImage(
                        MyImage.TYPE_SINGER_COVER,
                        Integer.valueOf(item.getSingerId().split("/")[0])));
                musicName.setText(item.getMusicName());
                singerName.setText(item.getSingerName().replace("/"," "));
                break;

            case Post.VIDEO:
                video.setVisibility(View.VISIBLE);
                Video video = post.getVideo();
                this.video.setVideo(video);
                break;

            case Post.FORWARD:
                forward.setVisibility(View.VISIBLE);

                Post index=post.getPost();
                innerContent.setText("");
                while(index!=null){
                    SpannableString user = new SpannableString("@"+index.getUserName());
                    final Post finalIndex = index;
                    user.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            UserInfoActivity.startActivity(PostActivity.this, finalIndex.getUserId());
                        }

                        @Override
                        public void updateDrawState(@NonNull TextPaint ds) {
                            ds.setUnderlineText(false);
                            ds.setColor(getResources().getColor(R.color.colorPrimary));
                        }
                    },0,user.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    innerContent.append(user);
                    innerContent.append("：");
                    innerContent.append(index.getContent());

                    if(index.getContentType()!=Post.FORWARD){
                        break;
                    }
                    index=index.getPost();
                }

                //如果为空，就说明该动态已被删除
                if(index!=null){
                    if(index.getHasImage()){
                        innerImage.setVisibility(View.VISIBLE);
                        innerImage.setImage(new MyImage(MyImage.
                                TYPE_POST_IMAGE,index.getId()));
                    }
                    if(index.getMusic()!=null){
                        Music m = new Music(index.getMusic());
                        innerMusic.setVisibility(View.VISIBLE);
                        innerMusicCover.setImage(new MyImage(
                                MyImage.TYPE_SINGER_COVER,
                                Integer.valueOf(m.getSingerId().split("/")[0])));
                        innerMusicName.setText(m.getMusicName());
                        innerSingerName.setText(m.getSingerName().replace("/"," "));
                    }

                    if(index.getVideo()!=null){
                        Video v = index.getVideo();
                        innerVideo.setVisibility(View.VISIBLE);
                        innerVideo.setVideo(v);
                    }
                }else{
                    innerContent.append("\n从这以后的动态已被转移到了虚空...");
                }

                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void onClosedInputMethod() {
        CommentAdapter adapter = ((PostCommentFragment)fragments.get(0)).getAdapter();
        adapter.cancelSelect();
        commentContent.setHint("留下无敌的评论...(最长1000个字)");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            View v=getCurrentFocus();
            if(!InputMethodUtil.isClickView(send,ev)&&!InputMethodUtil.isClickEditText(v,ev)){
                InputMethodUtil.hideSoftKeyboard(this);
                onClosedInputMethod();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        CommentAdapter adapter = ((PostCommentFragment)fragments.get(0)).getAdapter();
        switch (v.getId()){
            case R.id.send:
                if(commentContent.getText().length()==0){
                    Toast.makeText(this, "还没输入内容", Toast.LENGTH_SHORT).show();
                }else if(commentContent.getText().length()>Comment.MAX_LEN){
                    Toast.makeText(this, "长度过长", Toast.LENGTH_SHORT).show();
                }else{

                    if(adapter.getSelectPosition()==-1){
                        Comment comment=new Comment();
                        comment.setUserId(SharedPreferencesUtil.getUserId(sp));
                        comment.setContent(commentContent.getText().toString());
                        comment.setTargetType(Comment.POST);
                        comment.setTargetId(postId);

                        new S2SHttpUtil(
                                this,
                                gson.toJson(comment),
                                MyEnvironment.serverBasePath+"addComment",
                                new MyHandler(this)).call(BasicCode.SEND_COMMENT_END);

                    }else{
                        Reply reply=new Reply();
                        reply.setContent(commentContent.getText().toString());
                        reply.setUserId(SharedPreferencesUtil.getUserId(sp));
                        reply.setCommentId(adapter.getDataList().get(adapter.getSelectPosition()).getId());

                        new S2SHttpUtil(
                                this,
                                gson.toJson(reply),
                                MyEnvironment.serverBasePath+"addReply",
                                new MyHandler(this)).call(BasicCode.SEND_COMMENT_END);
                    }

                    commentContent.setText("");
                    InputMethodUtil.hideSoftKeyboard(this);
                }
                break;

            case R.id.to_good:
                Map<String,Integer> data = new HashMap<>();
                data.put("userId",SharedPreferencesUtil.getUserId(sp));
                data.put("postId",post.getId());

                new S2SHttpUtil(
                        this,
                        new Gson().toJson(data),
                        MyEnvironment.serverBasePath+"postToGood",
                        new MyHandler(this))
                        .call(BasicCode.TO_GOOD_END);
                break;

            case R.id.to_forward:
                ForwardActivity.startActivity(this,postId);
                break;

            case R.id.user_name:
            case R.id.user_header:
                UserInfoActivity.startActivity(this,post.getUserId());
                break;

            case R.id.inner:
            case R.id.inner_content:
                //获取最里层的动态ID
                Post post = this.post.getPost();
                while (post!=null){
                    if(post.getContentType()==Post.FORWARD){
                        post=post.getPost();
                    }else{
                        break;
                    }
                }

                if(post==null){
                    Toast.makeText(this, "该动态已被删除", Toast.LENGTH_SHORT).show();
                }else{
                    PostActivity.startActivity(this,post.getId());
                }
                break;

            case R.id.post_image:
                new ShowImageDialog(
                        this,
                        new MyImage(MyImage.TYPE_POST_IMAGE,this.post.getId()))
                        .show();
                break;

            case R.id.inner_image:
                new ShowImageDialog(this,
                        new MyImage(MyImage.TYPE_POST_IMAGE,this.post.getPost().getId())).show();
                break;

            case R.id.post_music:
                getBinder().playMusic(new Music(this.post.getMusic()));
                break;

            case R.id.inner_music:
                getBinder().playMusic(new Music(this.post.getPost().getMusic()));
                break;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        ViewPagerAdapter() {
            super(PostActivity.this.getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
        PostActivity activity;
        MyHandler(PostActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.SEND_COMMENT_END:
                    PostCommentFragment fragment = (PostCommentFragment) activity.fragments.get(0);
                    CommentAdapter adapter = fragment.getAdapter();
                    adapter.cancelSelect();
                    fragment.initContent();
                    break;

                case BasicCode.GET_CONTENT:
                    activity.post=new Gson().fromJson((String) msg.obj,Post.class);
                    if(activity.post!=null){
                        activity.initContent(false);
                    }else{
                        Toast.makeText(activity, "该动态已被删除", Toast.LENGTH_SHORT).show();
                        activity.finish();
                    }
                    break;

                case BasicCode.TO_GOOD_END:
                    Map<String,Integer> result = new Gson().fromJson((String) msg.obj,
                            new TypeToken<Map<String,Integer>>(){}.getType());
                    int gooded = result.get("gooded");
                    activity.post.setGooded(gooded);
                    if(gooded>0){
                        activity.toGood.setImageBitmap(activity.good);
                    }else{
                        activity.toGood.setImageBitmap(activity.noGood);
                    }
                    break;
            }
        }
    }
}
