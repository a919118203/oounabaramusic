package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.adapter.ManagementPlayListAdapter;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.List;
import java.util.Map;

public class PlayListManagementActivity extends BaseActivity {

    public static final int MY_PLAY_LIST=0;
    public static final int COLLECTION_PLAY_LIST=1;
    private ManagementPlayListAdapter adapter;
    private boolean isMy;  //判断是自己创建的歌单 还是收藏的歌单

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_management);
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
        RecyclerView rv=findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new ManagementPlayListAdapter(this));
        rv.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getSelected().size()!=0){
                    showDeleteDialog();
                }else{
                    Toast.makeText(PlayListManagementActivity.this,
                            "请先选择歌单", Toast.LENGTH_SHORT).show();
                }
            }
        });

        initContent();
    }

    private void initContent(){
        Intent intent = getIntent();
        int userId = SharedPreferencesUtil.getUserId(sp);
        int type = intent.getIntExtra("type",0);
        if(type==MY_PLAY_LIST){
            isMy=true;
        }else{
            isMy=false;
        }

        if(isMy){
            new S2SHttpUtil(
                    this,
                    userId+"",
                    MyEnvironment.serverBasePath+"findPlayListByUser",
                    new MyHandler(this))
                    .call(BasicCode.GET_CONTENT);
        }else{
            new S2SHttpUtil(
                    this,
                    userId+"",
                    MyEnvironment.serverBasePath+"getCollectPlayList",
                    new MyHandler(this))
                    .call(BasicCode.GET_CONTENT);
        }
    }

    private void showDeleteDialog(){
        AlertDialog dialog=new AlertDialog.Builder(this)
                .setTitle("确定要删除歌单吗？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<Integer> data=adapter.getSelected();
                        int userId = SharedPreferencesUtil.getUserId(sp);

                        if(data.size()!=0){

                            //如果是自己创建的话就删除歌单
                           if(isMy){
                               new S2SHttpUtil(PlayListManagementActivity.this,
                                       gson.toJson(data),
                                       MyEnvironment.serverBasePath+"deletePlayList",
                                       new MyHandler(PlayListManagementActivity.this)).call(BasicCode.DELETE_PLAY_LIST_END);

                           //如果是收藏的话就取消收藏
                           }else{
                               data.add(0,userId);
                               new S2SHttpUtil(PlayListManagementActivity.this,
                                       gson.toJson(data),
                                       MyEnvironment.serverBasePath+"cancelCollectPlayList",
                                       new MyHandler(PlayListManagementActivity.this)).call(BasicCode.DELETE_PLAY_LIST_END);
                           }
                        }
                    }
                })
                .create();

        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.layout_card_2));
        dialog.show();
    }

    public void setTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    static class MyHandler extends Handler{
        PlayListManagementActivity activity;
        public MyHandler(PlayListManagementActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case BasicCode.DELETE_PLAY_LIST_END:
                    Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show();
                    activity.finish();
                    break;

                case BasicCode.GET_CONTENT:
                    Map<String,String> data = new Gson().fromJson((String) msg.obj,
                            new TypeToken<Map<String,String>>(){}.getType());

                    List<PlayList> dataList = new Gson().fromJson(data.get("playLists"),
                            new TypeToken<List<PlayList>>(){}.getType());

                    if(activity.isMy){
                        //移除“我喜欢的音乐”歌单
                        dataList.remove(dataList.size()-1);
                    }
                    activity.adapter.setDataList(dataList);
                    break;
            }
        }
    }
}
