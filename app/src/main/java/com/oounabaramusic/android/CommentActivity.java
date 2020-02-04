package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.adapter.CommentAdapter;
import com.oounabaramusic.android.bean.Comment;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.bean.Reply;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.InputMethodUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.widget.customview.MyImageView;
import com.oounabaramusic.android.widget.popupwindow.SingerDialog;

import java.util.List;

public class CommentActivity extends BaseActivity implements View.OnClickListener{

    private CommentAdapter adapter;
    private Music music;
    private int dataType;
    private int dataId;
    private String userId;
    private EditText commentContent;
    private TextView send;
    public SwipeRefreshLayout srl;

    private MyHandler mHandler;
    private Comment selectComment;
    private int orderByType;
    private TextView orderBy;
    private TextView orderByName;

    private MyImageView contentCover;
    private TextView contentName,creatorName;
    private View content;

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
        if(dataType==-1)
            return ;

        switch (dataType){
            case Comment.MUSIC:
                music= (Music) intent.getSerializableExtra("data");
                break;
        }

        init();

        switch (dataType){
            case Comment.MUSIC:
                initMusicContent();
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initContent();
    }

    private void initMusicContent(){
        contentCover.setImageUrl(MyEnvironment.serverBasePath+
                "music/loadMusicCover?singerId="+music.getSingerId().split("/")[0]);
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

    public void replyTo(Comment comment){
        selectComment=comment;
        commentContent.setHint("回复 @"+comment.getUserName()+"：");
        commentContent.requestFocus();
        InputMethodUtil.showSoftKeyboard(this,commentContent);
    }

    @Override
    protected void onClosedInputMethod() {
        selectComment=null;
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

    private void init() {
        RecyclerView latestComments = findViewById(R.id.latest_comments);
        latestComments.setAdapter(adapter=new CommentAdapter(this));
        latestComments.setLayoutManager(new LinearLayoutManager(this));

        commentContent=findViewById(R.id.message_edit);
        send=findViewById(R.id.add_comment);
        srl=findViewById(R.id.swipe_refresh);
        contentCover =findViewById(R.id.cover);
        contentName =findViewById(R.id.name);
        creatorName=findViewById(R.id.author);
        content=findViewById(R.id.comment_for);
        orderBy=findViewById(R.id.order_by);
        orderByName=findViewById(R.id.order_by_name);

        send.setOnClickListener(this);
        orderBy.setOnClickListener(this);

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initContent();
            }
        });

        orderByType=Comment.ORDER_BY_GOOD_CNT;

        initContent();
    }

    private void initContent() {
        userId=sp.getString("userId","-1");
        mHandler=new MyHandler(this);
        selectComment=null;

        Comment comment=new Comment();
        comment.setMainUserId(Integer.valueOf(userId));
        comment.setTargetType(dataType);
        comment.setOrderByType(orderByType);
        if(dataType==Comment.MUSIC){
            comment.setTargetMd5(music.getMd5());
        }else{
            comment.setTargetId(dataId);
        }

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
                }else if(commentContent.getText().length()>1000){
                    Toast.makeText(this, "长度过长", Toast.LENGTH_SHORT).show();
                }else{
                    srl.setRefreshing(true);

                    if(selectComment==null){
                        Comment comment=new Comment();
                        comment.setUserId(Integer.valueOf(userId));
                        comment.setContent(commentContent.getText().toString());
                        comment.setTargetType(dataType);
                        if(dataType==Comment.MUSIC){
                            comment.setTargetMd5(music.getMd5());
                        }

                        new S2SHttpUtil(this,gson.toJson(comment),
                                MyEnvironment.serverBasePath+"addComment",mHandler).call(BasicCode.SEND_COMMENT_END);

                    }else{
                        Reply reply=new Reply();
                        reply.setContent(commentContent.getText().toString());
                        reply.setUserId(Integer.valueOf(userId));
                        reply.setReplyTo(selectComment.getUserId());
                        reply.setCommentId(selectComment.getId());

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
                    activity.selectComment=null;
                    activity.initContent();
                    break;
                case BasicCode.TO_GOOD_END:
                    String goodCnt= (String) msg.obj;
                    int selectPosition=activity.adapter.getSelectPosition();

                    activity.adapter.getDataList().get(
                            selectPosition).setGooded(Integer.valueOf(goodCnt));

                    if(goodCnt.equals("0")){
                        activity.adapter.getDataList().get(selectPosition).setGoodCnt(
                                activity.adapter.getDataList().get(selectPosition).getGoodCnt()-1
                        );
                    }else{
                        activity.adapter.getDataList().get(selectPosition).setGoodCnt(
                                activity.adapter.getDataList().get(selectPosition).getGoodCnt()+1
                        );
                    }

                    activity.adapter.notifyItemChanged(selectPosition,
                            CommentAdapter.CHANGE_GOOD);
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
