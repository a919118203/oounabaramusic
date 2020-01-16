package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.oounabaramusic.android.adapter.HistoryRecordAdapter;
import com.oounabaramusic.android.fragment.BaseFragment;
import com.oounabaramusic.android.fragment.SearchMusicFragment;
import com.oounabaramusic.android.fragment.SearchPlayListFragment;
import com.oounabaramusic.android.fragment.SearchSingerFragment;
import com.oounabaramusic.android.fragment.SearchUserFragment;
import com.oounabaramusic.android.fragment.SearchVideoFragment;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchActivity extends BaseActivity {

    private HistoryRecordAdapter historyRecordAdapter;
    private TabLayout tl;
    private ViewPager vp;
    private List<BaseFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        StatusBarUtil.setWhiteStyleStatusBar(this);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        fragments=new ArrayList<>();
        fragments.add(new SearchMusicFragment(this));
        fragments.add(new SearchVideoFragment(this));
        fragments.add(new SearchSingerFragment(this));
        fragments.add(new SearchPlayListFragment(this));
        fragments.add(new SearchUserFragment(this));


        tl=findViewById(R.id.tab_layout);
        vp=findViewById(R.id.view_pager);
        vp.setAdapter(new ViewPagerAdapter());
        tl.setupWithViewPager(vp);

        RecyclerView hr=findViewById(R.id.hr_recycler_view);
        hr.setAdapter(historyRecordAdapter=new HistoryRecordAdapter(this));
        hr.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        EditText et=findViewById(R.id.search_content);
        et.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH) {
                    LogUtil.printLog("!!!!!");
                }
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_singer,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.search_singer:
                intent=new Intent(this,SingerClassificationActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        public ViewPagerAdapter() {
            super(SearchActivity.this.getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
}
