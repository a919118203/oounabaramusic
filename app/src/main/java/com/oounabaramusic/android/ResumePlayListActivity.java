package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.adapter.ResumeMusicAdapter;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.fragment.BaseFragment;
import com.oounabaramusic.android.fragment.MusicDeletedPlayListFragment;
import com.oounabaramusic.android.fragment.ResumePlayListFragment;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.StatusBarUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ResumePlayListActivity extends BaseActivity implements View.OnClickListener{

    public final int MODE_NORMAL=0;
    public final int MODE_RESUME_MUSIC=1;
    private int mode=MODE_NORMAL;
    private List<BaseFragment> fragments;
    private LinearLayout resumeMusicLayout;
    private TabLayout tabLayout;
    private ResumeMusicAdapter adapter;
    private RecyclerView rv;
    private ViewPager vp;

    private TextView delete,resume;
    private PlayList playList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_play_list);
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
        fragments=new ArrayList<>();
        fragments.add(new ResumePlayListFragment(this));
        fragments.add(new MusicDeletedPlayListFragment(this));

        delete=findViewById(R.id.delete);
        resume=findViewById(R.id.resume);
        delete.setOnClickListener(this);
        resume.setOnClickListener(this);
        tabLayout=findViewById(R.id.tab_layout);
        vp=findViewById(R.id.view_pager);
        resumeMusicLayout=findViewById(R.id.fragment_layout);
        resumeMusicLayout.setVisibility(View.GONE);
        rv=findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new ResumeMusicAdapter(this));
        rv.setLayoutManager(new LinearLayoutManager(this));

        vp.setAdapter(new ViewPagerAdapter());
        tabLayout.setupWithViewPager(vp);


        initDrawable();
    }

    /**
     * 给布局下端的文字加上图标
     */

    private void initDrawable() {
        TextView delete=findViewById(R.id.delete);
        TextView resume=findViewById(R.id.resume);

        Drawable deleteIcon=getResources().getDrawable(R.mipmap.delete);
        deleteIcon.setBounds(0,0, DensityUtil.dip2px(this,30),DensityUtil.dip2px(this,30));
        delete.setCompoundDrawables(null,deleteIcon,null,null);

        Drawable resumeIcon=getResources().getDrawable(R.mipmap.resume);
        resumeIcon.setBounds(0,0, DensityUtil.dip2px(this,30),DensityUtil.dip2px(this,30));
        resume.setCompoundDrawables(null,resumeIcon,null,null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mode!=MODE_NORMAL){
            switchMode(MODE_NORMAL);
            return true;
        }
        finish();
        return true;
    }

    public void switchMode(int mode){
        this.mode=mode;
        if(mode==MODE_NORMAL){
            resumeMusicLayout.setVisibility(View.GONE);
            vp.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            playList=null;
        }else{
            resumeMusicLayout.setVisibility(View.VISIBLE);
            vp.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
        }
    }

    public void setPlayList(PlayList playList) {
        this.playList = playList;
        new S2SHttpUtil(this,
                gson.toJson(playList),
                MyEnvironment.serverBasePath+"findDelMusicByPlayList",
                new MyHandler(this))
        .call(BasicCode.GET_DELETE_MUSIC);
    }

    @Override
    public void onClick(View v) {
        if(adapter.getSelect().size()==0){
            Toast.makeText(this, "请先选择", Toast.LENGTH_SHORT).show();
            return;
        }

        switch(v.getId()){
            case R.id.delete:
                List<Integer> data=new ArrayList<>();
                data.add(playList.getId());
                data.addAll(adapter.getSelect());

                new S2SHttpUtil(this,
                        gson.toJson(data),
                        MyEnvironment.serverBasePath+"deleteMusic",
                        new MyHandler(this))
                        .call(BasicCode.DELETE_MUSIC_END);
                break;
            case R.id.resume:
                data=new ArrayList<>();
                data.add(playList.getId());
                data.addAll(adapter.getSelect());

                new S2SHttpUtil(this,
                        gson.toJson(data),
                        MyEnvironment.serverBasePath+"resumeMusic",
                        new MyHandler(this))
                .call(BasicCode.RESUME_MUSIC_END);
                break;
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        ViewPagerAdapter() {
            super(ResumePlayListActivity.this.getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
        private ResumePlayListActivity activity;
        MyHandler(ResumePlayListActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_DELETE_MUSIC:
                    List<Music> data=new ArrayList<>();

                    List<String> json=activity.gson.fromJson((String)msg.obj,
                            new TypeToken<List<String>>(){}.getType());

                    for(String item:json){
                        data.add(new Music(item));
                    }
                    activity.adapter.setDataList(data);
                    break;
                case BasicCode.RESUME_MUSIC_END:
                    Toast.makeText(activity, "恢复完成", Toast.LENGTH_SHORT).show();
                    activity.finish();
                    break;

                case BasicCode.DELETE_MUSIC_END:
                    Toast.makeText(activity, "删除完成", Toast.LENGTH_SHORT).show();
                    activity.finish();
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(mode!=MODE_NORMAL){
                switchMode(MODE_NORMAL);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
