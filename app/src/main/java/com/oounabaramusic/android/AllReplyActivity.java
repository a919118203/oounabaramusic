package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.adapter.AllReplyAdapter;
import com.oounabaramusic.android.bean.Comment;
import com.oounabaramusic.android.bean.Reply;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.InputMethodUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllReplyActivity extends BaseActivity implements View.OnClickListener{

    private Comment comment;

    private MyCircleImageView userHeader;
    private TextView userName;
    private TextView commentDate;
    private TextView commentContent;
    private ImageView good;
    private TextView goodCnt;
    private TextView replyCnt;
    private RecyclerView rv;
    private NestedScrollView nsv;

    private Bitmap goodBitmap,noGoodBitmap;

    private AllReplyAdapter adapter;
    private MyHandler mHandler;
    private String userId;

    private EditText replyContent;
    private TextView send;
    private Reply selectReply;

    private int scrollToReplyId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reply);

        setAutoCloseInputMethod(false);
        StatusBarUtil.setWhiteStyleStatusBar(this);
        setAutoCloseInputMethod(false);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent=getIntent();

        comment= (Comment) intent.getSerializableExtra("comment");
        scrollToReplyId = intent.getIntExtra("replyId",0);
        init();
    }

    private void init(){
        userHeader=findViewById(R.id.user_header);
        userName=findViewById(R.id.user_name);
        commentDate=findViewById(R.id.comment_date);
        commentContent=findViewById(R.id.comment_content);
        good=findViewById(R.id.good);
        goodCnt=findViewById(R.id.good_cnt);
        replyCnt=findViewById(R.id.reply_cnt);
        rv=findViewById(R.id.recycler_view);
        replyContent=findViewById(R.id.message_edit);
        send=findViewById(R.id.add_comment);
        nsv=findViewById(R.id.nsv);

        send.setOnClickListener(this);
        good.setOnClickListener(this);

        rv.setAdapter(adapter=new AllReplyAdapter(this,comment.getUserId(),scrollToReplyId));
        rv.setLayoutManager(new LinearLayoutManager(this));

        goodBitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.good);
        noGoodBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.no_good);

        mHandler=new MyHandler(this);
        selectReply=null;
        userId=sp.getString("userId","-1");

        initContent();
    }

    public void smoothScrollTo(int y){
        nsv.smoothScrollTo(0,y);
    }

    private void initContent(){
        replyCnt.setText(String.valueOf(comment.getReplyCnt()));
        userHeader.setImageUrl(MyEnvironment.serverBasePath
                +"loadUserHeader?userId="+comment.getUserId());
        userName.setText(comment.getUserName());
        commentDate.setText(FormatUtil.DateToString(comment.getDate()));
        commentContent.setText(comment.getContent());
        if(comment.getGooded()==0){
            good.setImageBitmap(noGoodBitmap);
        }else{
            good.setImageBitmap(goodBitmap);
        }
        goodCnt.setText(String.valueOf(comment.getGoodCnt()));
        initReplies();
    }

    private void initReplies(){
        List<Integer> data=new ArrayList<>();
        data.add(Integer.valueOf(userId));
        data.add(comment.getId());
        new S2SHttpUtil(this,gson.toJson(data),
                MyEnvironment.serverBasePath+"getReply",mHandler).call(BasicCode.GET_REPLY_END);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public String getUserId() {
        return userId;
    }

    public MyHandler getHandler() {
        return mHandler;
    }

    public void replyTo(Reply reply){
        selectReply=reply;
        replyContent.setHint("回复 @"+reply.getUserName()+"：");
        replyContent.requestFocus();
        InputMethodUtil.showSoftKeyboard(this,replyContent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            View v=getCurrentFocus();
            if(!InputMethodUtil.isClickView(send,ev)&&!InputMethodUtil.isClickEditText(v,ev)){
                InputMethodUtil.hideSoftKeyboard(this);
                selectReply=null;
                replyContent.setHint("留下无敌的评论...(最长1000个字)");
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_comment:
                if(replyContent.getText().length()==0){
                    Toast.makeText(this, "还没输入内容", Toast.LENGTH_SHORT).show();
                }else if(replyContent.getText().length()>1000){
                    Toast.makeText(this, "长度过长", Toast.LENGTH_SHORT).show();
                }else{
                    if(selectReply==null){
                        Reply reply=new Reply();
                        reply.setUserId(Integer.valueOf(userId));
                        reply.setContent(replyContent.getText().toString());
                        reply.setReplyTo(comment.getUserId());
                        reply.setCommentId(comment.getId());

                        new S2SHttpUtil(this,gson.toJson(reply),
                                MyEnvironment.serverBasePath+"addReply",mHandler).call(BasicCode.SEND_REPLY_END);

                    }else{
                        Reply reply=new Reply();
                        reply.setContent(replyContent.getText().toString());
                        reply.setUserId(Integer.valueOf(userId));
                        reply.setReplyTo(selectReply.getUserId());
                        reply.setCommentId(comment.getId());

                        new S2SHttpUtil(this,gson.toJson(reply),
                                MyEnvironment.serverBasePath+"addReply",mHandler).call(BasicCode.SEND_REPLY_END);
                    }

                    replyContent.setHint("留下无敌的评论...(最长1000个字)");
                    replyContent.setText("");
                    InputMethodUtil.hideSoftKeyboard(this);
                }
                break;

            case R.id.good:
                Map<String,Integer> data=new HashMap<>();
                data.put("userId",Integer.valueOf(userId));
                data.put("commentId",comment.getId());
                new S2SHttpUtil(this,gson.toJson(data),
                        MyEnvironment.serverBasePath+"goodToCommentOrReply",
                        mHandler).call(BasicCode.TO_GOOD_END_NO_RV);
                break;
        }
    }

    static class MyHandler extends Handler{
        private AllReplyActivity activity;

        MyHandler(AllReplyActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_REPLY_END:
                    List<Reply> data= activity.gson.fromJson((String)msg.obj,
                            new TypeToken<List<Reply>>(){}.getType());
                    activity.replyCnt.setText(String.valueOf(data.size()));
                    activity.adapter.setDataList(data);
                    activity.adapter.notifyDataSetChanged();
                    break;
                case BasicCode.SEND_REPLY_END:
                    activity.initReplies();
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
                            AllReplyAdapter.CHANGE_GOOD);
                    break;
                case BasicCode.TO_GOOD_END_NO_RV:
                    goodCnt= (String) msg.obj;

                    activity.comment.setGooded(Integer.valueOf(goodCnt));

                    if(goodCnt.equals("0")){
                        activity.comment.setGoodCnt(
                                activity.comment.getGoodCnt()-1
                        );
                    }else{
                        activity.comment.setGoodCnt(
                                activity.comment.getGoodCnt()+1
                        );
                    }

                    if(activity.comment.getGooded()==0){
                        activity.good.setImageBitmap(activity.noGoodBitmap);
                    }else{
                        activity.good.setImageBitmap(activity.goodBitmap);
                    }
                    activity.goodCnt.setText(String.valueOf(activity.comment.getGoodCnt()));
                    break;
            }
        }
    }
}
