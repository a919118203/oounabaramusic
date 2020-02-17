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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.adapter.MsgAdapter;
import com.oounabaramusic.android.bean.Message;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.InputMethodUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends BaseActivity implements View.OnClickListener{

    private MsgAdapter adapter;
    private RecyclerView rv;
    private int userId;
    private SwipeRefreshLayout srl;

    private EditText msgContent;
    private TextView send;

    private int oldMsgId; //最老的msgId

    private MenuItem toFollow;

    public static void startActivity(Context context,int userId,String userName){
        Intent intent = new Intent(context,MessageActivity.class);
        intent.putExtra("userId",userId);
        intent.putExtra("userName",userName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        StatusBarUtil.setWhiteStyleStatusBar(this);
        setAutoCloseInputMethod(false);


        Intent intent = getIntent();

        userId =intent.getIntExtra("userId",0);
        String userName = intent.getStringExtra("userName");
        oldMsgId= Integer.MAX_VALUE;


        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(userName);
        }

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_message_activiy,menu);
        toFollow=menu.getItem(0);

        initFollow();
        return true;
    }

    private void init() {

        rv = findViewById(R.id.message_list);
        rv.setAdapter(adapter=new MsgAdapter(this,userId));
        rv.setLayoutManager(new LinearLayoutManager(this));

        srl=findViewById(R.id.swipe_refresh);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMessage();
            }
        });

        msgContent=findViewById(R.id.message_edit);
        send=findViewById(R.id.send);

        send.setOnClickListener(this);
        send.requestFocus();
        InputMethodUtil.hideSoftKeyboard(this);

        loadMessage();
    }

    private void initFollow(){
        Map<String,Integer> data = new HashMap<>();
        data.put("from" , SharedPreferencesUtil.getUserId(sp));
        data.put("to",userId);

        new S2SHttpUtil(
                this,
                gson.toJson(data),
                MyEnvironment.serverBasePath+"isFollowed",
                new MyHandler(this))
        .call(BasicCode.IS_FOLLOWED);
    }

    private void loadMessage(){

        Message msg = new Message();
        msg.setMessageId(oldMsgId);
        msg.setFromId(userId);
        msg.setToId( SharedPreferencesUtil.getUserId(sp));
        msg.setLen(20);

        new S2SHttpUtil(
                this,
                gson.toJson(msg),
                MyEnvironment.serverBasePath+"getMessage",
                new MyHandler(this))
        .call(BasicCode.GET_CONTENT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.to_follow:

                toFollow.setEnabled(false);

                Map<String,Integer> data = new HashMap<>();
                data.put("from" , SharedPreferencesUtil.getUserId(sp));
                data.put("to",userId);

                new S2SHttpUtil(
                        this,
                        gson.toJson(data),
                        MyEnvironment.serverBasePath+"toFollowUser",
                        new MyHandler(this))
                .call(BasicCode.TO_FOLLOW_USER);

                break;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            View v=getCurrentFocus();
            if(!InputMethodUtil.isClickView(send,ev)&&!InputMethodUtil.isClickEditText(v,ev)){
                InputMethodUtil.hideSoftKeyboard(this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send:
                String content = msgContent.getText().toString();
                if(content.length()>400){
                    Toast.makeText(this, "一次最多400字", Toast.LENGTH_SHORT).show();
                    return;
                }

                send.setEnabled(false);
                srl.setRefreshing(true);

                if(content.length()<=0){
                    Toast.makeText(this, "请先输入内容", Toast.LENGTH_SHORT).show();
                    return;
                }

                Message msg = new Message();
                msg.setFromId(SharedPreferencesUtil.getUserId(sp));
                msg.setToId(userId);
                msg.setContent(content);

                new S2SHttpUtil(
                        this,
                        gson.toJson(msg),
                        MyEnvironment.serverBasePath+"sendMessage",
                        new MyHandler(this))
                .call(BasicCode.SEND_MESSAGE);
                break;
        }
    }

    static class MyHandler extends Handler{
        MessageActivity activity;
        MyHandler(MessageActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    Map<String,String> data = new Gson().fromJson((String) msg.obj,
                            new TypeToken<Map<String,String>>(){}.getType());

                    List<Message> dataList = new Gson().fromJson(data.get("messages"),
                            new TypeToken<List<Message>>(){}.getType());

                    if(!dataList.isEmpty()){
                        activity.oldMsgId=Math.min(activity.oldMsgId,dataList.get(dataList.size()-1).getMessageId());
                    }
                    activity.adapter.addDataList(dataList);
                    activity.rv.scrollToPosition(dataList.size()-1);
                    activity.srl.setRefreshing(false);
                    break;
                case BasicCode.SEND_MESSAGE:
                    try{
                        Message message = new Gson().fromJson((String) msg.obj,Message.class);
                        activity.adapter.addMessage(message);
                        activity.oldMsgId=Math.min(activity.oldMsgId,message.getMessageId());
                        activity.send.setEnabled(true);
                        activity.srl.setRefreshing(false);
                        activity.rv.scrollToPosition(activity.adapter.getItemCount()-1);
                        activity.msgContent.setText("");
                        InputMethodUtil.hideSoftKeyboard(activity);
                    }catch (Exception e){
                        Toast.makeText(activity, "发生错误，现只支持输入文本", Toast.LENGTH_SHORT).show();
                        activity.srl.setRefreshing(false);
                        activity.send.setEnabled(true);
                    }
                    break;
                case BasicCode.IS_FOLLOWED:
                    int followed = Integer.valueOf((String) msg.obj);
                    activity.toFollow.setVisible(followed==0);
                    break;

                case BasicCode.TO_FOLLOW_USER:
                    Map<String , Integer >d = new Gson().fromJson((String) msg.obj,
                            new TypeToken<Map<String,Integer>>(){}.getType());
                    if(d.get("followed")>0){
                        Toast.makeText(activity, "关注成功", Toast.LENGTH_SHORT).show();
                        activity.toFollow.setVisible(false);
                    }
                    activity.toFollow.setEnabled(true);
                    break;
            }
        }
    }
}
