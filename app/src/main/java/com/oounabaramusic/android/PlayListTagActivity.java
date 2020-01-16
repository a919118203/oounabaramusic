package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oounabaramusic.android.adapter.MyPlayListTagAdapter;
import com.oounabaramusic.android.adapter.OtherPlayListTagAdapter;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlayListTagActivity extends BaseActivity {

    private List<String> titles;
    private Map<String,List<String>> tags;
    private List<RecyclerView.Adapter> adapters;
    private LinearLayout content;

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
        initTag();

        adapters=new ArrayList<>();
        content=findViewById(R.id.content);

        for(int i=0;i<titles.size();i++){
            LinearLayout ll= (LinearLayout) LayoutInflater
                    .from(this)
                    .inflate(R.layout.activity_play_list_tag_item,
                            (ViewGroup) getWindow().getDecorView(),
                            false);

            TextView title=ll.findViewById(R.id.title);
            title.setText(titles.get(i));
            RecyclerView rv=ll.findViewById(R.id.recycler_view);
            RecyclerView.Adapter adapter;
            if(i==0){
                adapter=new MyPlayListTagAdapter(this,tags.get(titles.get(i)));
            }else{
                adapter=new OtherPlayListTagAdapter(this,tags.get(titles.get(i)),i);
            }
            adapters.add(adapter);
            rv.setAdapter(adapter);
            rv.setLayoutManager(new GridLayoutManager(this,4));

            content.addView(ll);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void initTag() {
        titles=new ArrayList<>();
        tags=new HashMap<>();
        List<String> item=null;

        titles.add("我的歌单广场");
        item=new ArrayList<>();
        item.add("推荐");
        tags.put(titles.get(tags.size()),item);

        titles.add("语种");
        item=new ArrayList<>();
        item.add("华语");
        item.add("日语");
        item.add("欧美");
        item.add("粤语");
        item.add("韩语");
        tags.put(titles.get(tags.size()),item);

        titles.add("风格");
        item=new ArrayList<>();
        item.add("流行");
        item.add("摇滚");
        item.add("民谣");
        item.add("电子");
        item.add("舞曲");
        item.add("说唱");
        item.add("轻音乐");
        item.add("爵士");
        item.add("乡村");
        item.add("民族");
        item.add("古风");
        tags.put(titles.get(tags.size()),item);

        titles.add("场景");
        item=new ArrayList<>();
        item.add("清晨");
        item.add("夜晚");
        item.add("学习");
        item.add("工作");
        item.add("午休");
        item.add("下午茶");
        item.add("地铁");
        item.add("驾车");
        item.add("运动");
        item.add("旅行");
        item.add("散步");
        item.add("酒吧");
        tags.put(titles.get(tags.size()),item);

        titles.add("情感");
        item=new ArrayList<>();
        item.add("怀旧");
        item.add("清新");
        item.add("浪漫");
        item.add("伤感");
        item.add("治愈");
        item.add("放松");
        item.add("孤独");
        item.add("感动");
        item.add("兴奋");
        item.add("快乐");
        item.add("安静");
        item.add("思念");
        tags.put(titles.get(tags.size()),item);
    }
}
