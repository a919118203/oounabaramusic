package com.oounabaramusic.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.oounabaramusic.android.adapter.HistoryRecordAdapter;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.dao.HistoricalQueryDao;
import com.oounabaramusic.android.fragment.BaseFragment;
import com.oounabaramusic.android.fragment.SearchMusicFragment;
import com.oounabaramusic.android.fragment.SearchPlayListFragment;
import com.oounabaramusic.android.fragment.SearchSingerFragment;
import com.oounabaramusic.android.fragment.SearchUserFragment;
import com.oounabaramusic.android.fragment.SearchVideoFragment;
import com.oounabaramusic.android.okhttputil.SearchHttpUtil;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.InputMethodUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchActivity extends BaseActivity {

    public static final int MESSAGE_SEARCH=0;
    public static final int MESSAGE_SEARCH_MUSIC=1;

    private HistoryRecordAdapter historyRecordAdapter;
    private TabLayout tl;
    private ViewPager vp;
    private EditText et;
    private List<BaseFragment> fragments;

    private LinearLayout search;
    private LinearLayout searchResult;

    private HistoricalQueryDao historicalQueryDao;
    private Handler handler;
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
        historicalQueryDao=new HistoricalQueryDao(this);
        handler=new SearchHandler(this);

        fragments=new ArrayList<>();
        fragments.add(new SearchMusicFragment(this));
        fragments.add(new SearchVideoFragment(this));
        fragments.add(new SearchSingerFragment(this));
        fragments.add(new SearchPlayListFragment(this));
        fragments.add(new SearchUserFragment(this));

        et=findViewById(R.id.search_content);
        tl=findViewById(R.id.tab_layout);
        vp=findViewById(R.id.view_pager);
        search=findViewById(R.id.search);
        searchResult=findViewById(R.id.search_result);

        vp.setAdapter(new ViewPagerAdapter());
        tl.setupWithViewPager(vp);

        RecyclerView hr=findViewById(R.id.hr_recycler_view);
        hr.setAdapter(historyRecordAdapter=new HistoryRecordAdapter(this,historicalQueryDao.selectLimitSearch(10)));
        hr.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));


        //监听输入法的检索确认
        et.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH) {
                    String searchText=et.getText().toString();
                    if(searchText.equals("")){
                        searchText=et.getHint().toString();
                    }
                    search(searchText);
                }
                return true;
            }
        });

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SearchActivity.this.invalidateOptionsMenu();
            }
        });

        //删除历史记录
        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historicalQueryDao.deleteAll();
                historyRecordAdapter.setDataList(new ArrayList<String>());
            }
        });
    }

    private void search(String searchText){
        historicalQueryDao.insertHistoricalQuery(searchText);
        search.setVisibility(View.GONE);
        searchResult.setVisibility(View.VISIBLE);

        fragments.get(vp.getCurrentItem()).notifyFragment();
        SearchHttpUtil.searchMusic(this,searchText,handler);
    }

    private MenuItem deleteText;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_singer,menu);
        menu.getItem(0).setVisible(et.getText().toString().length()==0);
        menu.getItem(1).setVisible(et.getText().toString().length()!=0);
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
                if(searchResult.getVisibility()==View.VISIBLE){
                    historyRecordAdapter.setDataList(historicalQueryDao.selectLimitSearch(10));
                    search.setVisibility(View.VISIBLE);
                    searchResult.setVisibility(View.GONE);
                    invalidateOptionsMenu();
                }else{
                    finish();
                }
                break;
            case R.id.delete_text:
                et.setText("");
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(searchResult.getVisibility()==View.VISIBLE){
                historyRecordAdapter.setDataList(historicalQueryDao.selectLimitSearch(10));
                search.setVisibility(View.VISIBLE);
                searchResult.setVisibility(View.GONE);
                invalidateOptionsMenu();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            View v=getCurrentFocus();
            if(!InputMethodUtil.isClickEditText(v,ev)){
                InputMethodUtil.hideSoftKeyboard(SearchActivity.this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    static class SearchHandler extends Handler{

        private SearchActivity activity;

        SearchHandler(SearchActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_SEARCH:
                    activity.search((String) msg.obj);
                    break;
                case MESSAGE_SEARCH_MUSIC:
                    List<Music> dataList= (List<Music>) msg.obj;
                    ((SearchMusicFragment)activity.fragments.get(0)).setDataList(dataList);
                    break;
            }
        }
    }

    public Handler getHandler() {
        return handler;
    }
}
