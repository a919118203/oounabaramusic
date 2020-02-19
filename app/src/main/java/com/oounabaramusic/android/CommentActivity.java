package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.adapter.CommentAdapter;
import com.oounabaramusic.android.bean.Comment;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.bean.Reply;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.InputMethodUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.widget.customview.MyImageView;
import com.oounabaramusic.android.widget.popupwindow.SingerDialog;

import java.util.List;

public class CommentActivity extends BaseActivity implements View.OnClickListener{

    private CommentAdapter adapter;

    private Music music;
    private PlayList playList;

    private int dataType;
    private int dataId;
    private String userId;
    private EditText commentContent;
    private TextView send;
    public SwipeRefreshLayout srl;

    private MyHandler mHandler;
    private int orderByType;
    private TextView orderBy;
    private TextView orderByName;

    private MyImageView contentCover;
    private TextView contentName,creatorName;
    private View content;

    public static void startActivity(Context context,int dataId,int dataType){
        Intent intent=new Intent(context,CommentActivity.class);
        intent.putExtra("dataId",dataId);
        intent.putExtra("dataType",dataType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        StatusBarUtil.setWhiteStyleStatusBar(this);
        setAutoCloseInputMethod(false);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent=getIntent();
        dataType=intent.getIntExtra("dataType",-1);
        dataId=intent.getIntExtra("dataId",-1);
        if(dataType==-1)
            return ;

        init();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initContent();
    }

    private void initMusicContent(){
        contentCover.setImage(new MyImage(
                MyImage.TYPE_SINGER_COVER,
                Integer.valueOf(music.getSingerId().split("/")[0])));
        contentName.setText(music.getMusicName());
        creatorName.setText(music.getSingerName().replace("/"," "));

        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBinder().playMusic(music);
                startActivity(new Intent(CommentActivity.this,MusicPlayActivity.class));
            }
        });

        creatorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SingerDialog(CommentActivity.this,
                        music.getSingerName(),music.getSingerId());
            }
        });
    }

    private void initPlayListContent(){
        contentCover.setImage(new MyImage(MyImage.TYPE_PLAY_LIST_COVER,playList.getId()));
        contentName.setText(playList.getPlayListName());
        creatorName.setText(playList.getCreateUserName());

        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayListActivity.startActivity(CommentActivity.this,playList.getId());
            }
        });

        creatorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoActivity.startActivity(CommentActivity.this,
                        playList.getCreateUserId());
            }
        });
    }

    @Override
    protected void onClosedInputMethod() {
        adapter.cancelSelect();
        commentContent.setHint("留下无敌的评论...(最长"+Comment.MAX_LEN+"个字)");
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

    private void init() {
        commentContent=findViewById(R.id.message_edit);
        send=findViewById(R.id.add_comment);
        srl=findViewById(R.id.swipe_refresh);
        contentCover =findViewById(R.id.cover);
        contentName =findViewById(R.id.name);
        creatorName=findViewById(R.id.author);
        content=findViewById(R.id.comment_for);
        orderBy=findViewById(R.id.order_by);
        orderByName=findViewById(R.id.order_by_name);

        RecyclerView latestComments = findViewById(R.id.latest_comments);
        latestComments.setAdapter(adapter=new CommentAdapter(this,commentContent));
        latestComments.setLayoutManager(new LinearLayoutManager(this));

        send.setOnClickListener(this);
        orderBy.setOnClickListener(this);

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initContent();
            }
        });

        orderByType=Comment.ORDER_BY_GOOD_CNT;
        commentContent.setHint("留下无敌的评论...(最长"+Comment.MAX_LEN+"个字)");

        initContent();

        initTopData();
    }

    private void initTopData(){
        String url=MyEnvironment.serverBasePath;

        switch (dataType){
            case Comment.MUSIC:
                url+="music/getMusicById";
                break;
            case Comment.PLAY_LIST:
                url+="findPlayListById";
                break;
        }

        new S2SHttpUtil(
                this,
                dataId+"",
                url,
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT);
    }

    private void initContent() {
        userId= SharedPreferencesUtil.getUserId(sp)+"";
        mHandler=new MyHandler(this);
        adapter.cancelSelect();

        Comment comment=new Comment();
        comment.setMainUserId(Integer.valueOf(userId));
        comment.setTargetType(dataType);
        comment.setTargetId(dataId);
        comment.setOrderByType(orderByType);

        srl.setRefreshing(true);

        new S2SHttpUtil(
                this,
                gson.toJson(comment),
                MyEnvironment.serverBasePath+"getComment",mHandler)
                .call(BasicCode.GET_COMMENT_END);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_comment:
                if(commentContent.getText().length()==0){
                    Toast.makeText(this, "还没输入内容", Toast.LENGTH_SHORT).show();
                }else if(commentContent.getText().length()>Comment.MAX_LEN){
                    Toast.makeText(this, "长度过长", Toast.LENGTH_SHORT).show();
                }else{
                    srl.setRefreshing(true);

                    if(adapter.getSelectPosition()==-1){
                        Comment comment=new Comment();
                        comment.setUserId(Integer.valueOf(userId));
                        comment.setContent(commentContent.getText().toString());
                        comment.setTargetType(dataType);
                        comment.setTargetId(dataId);

                        new S2SHttpUtil(this,gson.toJson(comment),
                                MyEnvironment.serverBasePath+"addComment",mHandler).call(BasicCode.SEND_COMMENT_END);

                    }else{
                        Reply reply=new Reply();
                        reply.setContent(commentContent.getText().toString());
                        reply.setUserId(Integer.valueOf(userId));
                        reply.setCommentId(adapter.getDataList().get(adapter.getSelectPosition()).getId());

                        new S2SHttpUtil(this,gson.toJson(reply),
                                MyEnvironment.serverBasePath+"addReply",mHandler).call(BasicCode.SEND_COMMENT_END);
                    }

                    commentContent.setText("");
                    InputMethodUtil.hideSoftKeyboard(this);
                }
                break;

            case R.id.order_by:
                if(orderByType==Comment.ORDER_BY_GOOD_CNT){
                    orderByType=Comment.ORDER_BY_DATE;
                    orderBy.setText("按最新");
                    orderByName.setText("最新评论");
                }else {
                    orderByType=Comment.ORDER_BY_GOOD_CNT;
                    orderBy.setText("按热度");
                    orderByName.setText("最热评论");
                }
                initContent();
                break;
        }
    }

    static class MyHandler extends Handler{

        private CommentActivity activity;

        private MyHandler(CommentActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_COMMENT_END:
                    List<Comment> dataList=activity.gson.fromJson((String)msg.obj,
                            new TypeToken<List<Comment>>(){}.getType());
                    activity.adapter.setDataList(dataList);
                    if(activity.srl.isRefreshing()){
                        activity.srl.setRefreshing(false);
                    }
                    break;

                case BasicCode.SEND_COMMENT_END:
                    activity.adapter.cancelSelect();
                    activity.initContent();
                    break;

                case BasicCode.GET_CONTENT:
                    switch (activity.dataType){
                        case Comment.MUSIC:
                            activity.music= new Music((String) msg.obj);
                            activity.initMusicContent();
                            break;
                        case Comment.PLAY_LIST:
                            activity.playList=new Gson().fromJson((String) msg.obj,PlayList.class);
                            activity.initPlayListContent();
                            break;
                    }
                    break;
            }
        }
    }

    public MyHandler getHandler() {
        return mHandler;
    }

    public String getUserId() {
        return userId;
    }
}
