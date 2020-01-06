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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.oounabaramusic.android.adapter.ResumeMusicAdapter;
import com.oounabaramusic.android.fragment.BaseFragment;
import com.oounabaramusic.android.fragment.MusicDeletedPlayListFragment;
import com.oounabaramusic.android.fragment.ResumePlayListFragment;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.StatusBarUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ResumePlayListActivity extends BaseActivity {

    public final int MODE_NORMAL=0;
    public final int MODE_RESUME_MUSIC=1;
    private int mode=MODE_NORMAL;
    private List<BaseFragment> fragments;
    private LinearLayout resumeMusicLayout;
    private TabLayout tabLayout;
    private ResumeMusicAdapter adapter;
    private RecyclerView rv;
    private ViewPager vp;

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
        }else{
            resumeMusicLayout.setVisibility(View.VISIBLE);
            vp.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);

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
