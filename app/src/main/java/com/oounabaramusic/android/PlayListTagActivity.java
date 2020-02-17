package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.adapter.MyPlayListAdapter;
import com.oounabaramusic.android.adapter.MyPlayListTagAdapter;
import com.oounabaramusic.android.adapter.OtherPlayListTagAdapter;
import com.oounabaramusic.android.bean.PlayListBigTag;
import com.oounabaramusic.android.bean.PlayListSmallTag;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.okhttputil.TagHttpUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlayListTagActivity extends BaseActivity {

    private List<PlayListBigTag> bigTags;
    private List<RecyclerView.Adapter> adapters;
    private LinearLayout content;

    private List<PlayListSmallTag> myTags;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_tag);
        StatusBarUtil.setWhiteStyleStatusBar(this);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        adapters=new ArrayList<>();
        userId = Integer.valueOf(sp.getString("userId","-1"));

        content=findViewById(R.id.content);

        Intent intent=getIntent();
        myTags=(ArrayList<PlayListSmallTag>)intent.getSerializableExtra("tags");

        //初始化自己的歌单标签
        LinearLayout ll= (LinearLayout) LayoutInflater
                .from(this)
                .inflate(R.layout.activity_play_list_tag_item,
                        (ViewGroup) getWindow().getDecorView(),
                        false);
        TextView title=ll.findViewById(R.id.title);
        title.setText("我的标签");
        RecyclerView rv=ll.findViewById(R.id.recycler_view);
        RecyclerView.Adapter adapter;
        rv.setAdapter(adapter = new MyPlayListTagAdapter(this,
                myTags));
        adapters.add(adapter);
        rv.setLayoutManager(new GridLayoutManager(this,4));
        content.addView(ll);

        //获取并初始化服务器歌单标签
        initTag();
    }

    public void addTag(PlayListSmallTag tag){
        ((MyPlayListTagAdapter)adapters.get(0)).addTag(tag);
    }

    public void removeTag(PlayListSmallTag tag){

        //寻找对应大标签的位置
        int index = bigTags.indexOf(tag);

        //第一个为我的标签 所以+1
        ((OtherPlayListTagAdapter)adapters.get(index+1)).activationTag(tag);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.save:
                List<PlayListSmallTag> dataList =
                        ((MyPlayListTagAdapter)adapters.get(0)).getTags();

                List<Integer> data = new ArrayList<>();
                data.add(userId);
                for(PlayListSmallTag tag:dataList){
                    data.add(tag.getId());
                }

                new S2SHttpUtil(
                        this,
                        gson.toJson(data),
                        MyEnvironment.serverBasePath+"saveUserPlayListTag",
                        new MyHandler(this))
                .call(BasicCode.SAVE_TO_SERVER);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_play_list_tag,menu);
        return true;
    }

    private void initTag() {
        if(bigTags==null){
            TagHttpUtil.getPlayListTag(this,new MyHandler(this));
            return;
        }

        //初始化歌单标签
        for(int i=0;i<bigTags.size();i++){
            LinearLayout ll= (LinearLayout) LayoutInflater
                    .from(this)
                    .inflate(R.layout.activity_play_list_tag_item,
                            (ViewGroup) getWindow().getDecorView(),
                            false);

            PlayListBigTag item=bigTags.get(i);

            TextView title=ll.findViewById(R.id.title);
            title.setText(item.getName());
            RecyclerView rv=ll.findViewById(R.id.recycler_view);
            RecyclerView.Adapter adapter;

            adapter=new OtherPlayListTagAdapter(this,item.getTags());

            adapters.add(adapter);
            rv.setAdapter(adapter);
            rv.setLayoutManager(new GridLayoutManager(this,4));

            content.addView(ll);
        }

        //将我已拥有的标签设为已选择
        for(PlayListSmallTag tag:myTags){
            //寻找对应大标签的位置
            int index = bigTags.indexOf(tag);

            //第一个为我的标签 所以+1
            ((OtherPlayListTagAdapter)adapters.get(index+1)).selectTag(tag);
        }
    }

    static class MyHandler extends Handler{
        PlayListTagActivity activity;

        MyHandler(PlayListTagActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TagHttpUtil.MESSAGE_GET_PLAY_LIST_TAG_END:
                    activity.bigTags = new Gson().fromJson((String)msg.obj,
                            new TypeToken<List<PlayListBigTag>>(){}.getType());
                    activity.initTag();
                    break;

                case BasicCode.SAVE_TO_SERVER:
                    Toast.makeText(activity, "保存成功", Toast.LENGTH_SHORT).show();
                    activity.finish();
                    break;
            }
        }
    }
}
