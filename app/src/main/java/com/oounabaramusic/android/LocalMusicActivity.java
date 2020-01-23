package com.oounabaramusic.android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oounabaramusic.android.adapter.LocalMusicAdapter;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.dao.LocalMusicDao;
import com.oounabaramusic.android.okhttputil.HttpUtil;
import com.oounabaramusic.android.util.DigestUtils;
import com.oounabaramusic.android.util.InternetUtil;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LocalMusicActivity extends BaseActivity implements View.OnClickListener{

    public final static int TOOLBAR_MODE_EDIT=0;//检索编辑模式
    public final static int TOOLBAR_MODE_NORMAL=1;//普通模式
    public final static int TOOLBAR_MODE_MULTIPLE_CHOICE=2;//多选模式
    private int toolbarMode=TOOLBAR_MODE_NORMAL;//ToolBar现在的模式


    public static final int MESSAGE_SEARCH_START =0;
    public static final int MESSAGE_SEARCH_END =1;
    public static final int MESSAGE_CHECK_IS_SERVER_END =3;
    public static final int MESSAGE_CHANGE_TEXT_VIEW =2;
    public static final int MESSAGE_DELETE_MUSIC=4;

    private RecyclerView rv;//显示歌曲列表
    private LocalMusicAdapter adapter;//歌曲列表的适配器
    private EditText edit;//用于搜索歌曲列表的输入框
    private LinearLayout bottomToolBar;//多选模式时，显示在底部的菜单
    private LinearLayout nextPlay;//底部菜单的“下一首播放”
    private LinearLayout addToPlaylist;//底部菜单的“加入歌单”
    private LinearLayout delete;//底部菜单的“删除”
    private LinearLayout playAll;//ToolBar下方的播放全部
    private LinearLayout multipleChoice;//ToolBar下方的多选
    private RelativeLayout musicPlayTool;//画面下方的播放器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        StatusBarUtil.setTranslucentStatusAndDarkContent(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Toolbar toolBar=findViewById(R.id.local_music_toolbar);
        setSupportActionBar(toolBar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        init();
    }

    private void init() {

        View view=findViewById(R.id.outermost_layout);
        view.setPadding(
                view.getPaddingLeft(),
                view.getPaddingTop()+StatusBarUtil.getStatusBarHeight(this),
                view.getPaddingRight(),
                view.getPaddingBottom());

        rv=findViewById(R.id.local_music_recycler_view);
        adapter=new LocalMusicAdapter(this,localMusicDao.selectAllLocalMusic(),handler);
        rv.setAdapter(adapter);
        LinearLayoutManager llm=new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        edit=findViewById(R.id.local_music_edit);
        playAll=findViewById(R.id.local_music_play_all);
        multipleChoice=findViewById(R.id.local_music_multiple_choice);
        bottomToolBar=findViewById(R.id.bottom_toolbar);
        nextPlay=findViewById(R.id.bottom_tool_next_play);
        addToPlaylist=findViewById(R.id.bottom_tool_add_to_playlist);
        delete=findViewById(R.id.bottom_tool_delete);
        musicPlayTool=findViewById(R.id.tool_current_play_layout);

        playAll.setOnClickListener(this);
        multipleChoice.setOnClickListener(this);
        nextPlay.setOnClickListener(this);
        addToPlaylist.setOnClickListener(this);
        delete.setOnClickListener(this);

        //每当输入，都查询
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0){
                    adapter.setMusicList(new ArrayList<Music>());
                }else{
                    adapter.setMusicList(localMusicDao.selectMusicByMusicName(s+""));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 给adapter用来改变标题
     * @param title
     */
    public void setTitle(String title){
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_local_music,menu);
        for(int i=0;i<menu.size();i++){
            MenuItem item=menu.getItem(i);
            switch(item.getItemId()){
                case R.id.local_music_search_in_list:
                case R.id.local_music_search_in_file:
                    if(toolbarMode!=TOOLBAR_MODE_NORMAL){
                        item.setVisible(false);
                    }
                    break;
                case R.id.local_music_menu_multiple_choice:
                case R.id.local_music_menu_multiple_choice_cancel:
                    if(toolbarMode!=TOOLBAR_MODE_MULTIPLE_CHOICE){
                        item.setVisible(false);
                    }else{
                        if(adapter.checkAllSelect()){
                            if(item.getItemId()==R.id.local_music_menu_multiple_choice)
                                item.setVisible(false);
                        }else{
                            if(item.getItemId()==R.id.local_music_menu_multiple_choice_cancel)
                                item.setVisible(false);
                        }
                    }
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home://退出或退出edit模式
                if(toolbarMode!=TOOLBAR_MODE_NORMAL){
                    switchToolBar(TOOLBAR_MODE_NORMAL);
                    StatusBarUtil.hideSoftKeyboard(this);
                }else{
                    finish();
                }
                break;
            case R.id.local_music_search_in_list://检索list中的歌曲
                switchToolBar(TOOLBAR_MODE_EDIT);
                break;
            case R.id.local_music_search_in_file://扫描所有文件夹中的歌曲
                startSearch();
                break;
            case R.id.local_music_menu_multiple_choice:
                adapter.selectAll();
                adapter.notifyDataSetChanged();
                break;
            case R.id.local_music_menu_multiple_choice_cancel:
                adapter.selectAllCancel();
                adapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    //dialog的textView
    private TextView dialogTV;
    private Handler handler=new LocalMusicHandler(this);
    private List<String> filter;//过滤这些项目，无需再插入在表中

    /**
     * 开始扫描，显示dialog，开启线程
     */
    private void startSearch(){
        filter=new ArrayList<>();

        checkTableData();

        showSearchDialog();

        new Thread(new Runnable() {
            @Override
            public void run() {
                searchFile(Environment.getExternalStorageDirectory());
            }
        }).start();
    }

    /**
     * 检查表里存的数据的正确性，删除不正确的
     */
    private void checkTableData() {
        List<Music> musics=localMusicDao.selectAllLocalMusic();

        String filePath;
        String md5;
        for(Music m:musics){
            filePath=m.getFilePath();
            md5=m.getMd5();

            File file=new File(filePath);
            if(file.exists()){
                if(file.length()==m.getFileSize()){
                    if(md5.equals(DigestUtils.md5HexOfFile(file))){
                        filter.add(md5);
                    }else{
                        localMusicDao.deleteMusicByMd5(md5);
                    }
                }else{
                    localMusicDao.deleteMusicByMd5(md5);
                }
            }else{
                localMusicDao.deleteMusicByMd5(md5);
            }
        }
    }

    /**
     * 显示扫描文件的dialog
     */
    private AlertDialog dialog;

    private void showSearchDialog() {
        View view= LayoutInflater.from(this).inflate(R.layout.alertdialog_search_local_music, (ViewGroup) getWindow().getDecorView(),false);
        dialogTV=view.findViewById(R.id.text_view);

        dialog=new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();

        Window window=Objects.requireNonNull(dialog.getWindow());
        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.layout_card_2));
        dialog.show();

    }

    /**
     * 扫描全部音乐文件
     * 将所有的mp3结尾的且时长大于60s的文件的路径保存到local_music_tbl中
     */
    private void searchFile(File file) {
        handler.sendEmptyMessage(MESSAGE_SEARCH_START);

        File[] files=file.listFiles();
        for(File f:files){
            if(f.isDirectory()){
                searchFile(f);
                continue;
            }
            String fileName=f.getName();
            Message message=new Message();
            message.obj=fileName;
            message.what= MESSAGE_CHANGE_TEXT_VIEW;
            handler.sendMessage(message);

            //判断拥有后缀的文件
            String[] s=fileName.split("\\.");
            if(s.length>=2&&checkFormat(s[s.length-1].trim())){

                //判断时长
                MediaPlayer mp=new MediaPlayer();
                try {
                    mp.setDataSource(f.getPath());
                    mp.prepare();

                    //大于60s
                    if(mp.getDuration()>60000){
                        insertTbl(f,mp.getDuration());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        handler.sendEmptyMessage(MESSAGE_SEARCH_END);
    }

    /**
     *  将扫描出来的file插入到表中
     * @param f
     */
    private void insertTbl(File f,int duration) {
        Music item=new Music();
        item.setMd5(DigestUtils.md5HexOfFile(f));

        if(localMusicDao.md5IsExists(item.getMd5())){
            return;
        }

        item.setDownloadStatus(3);
        item.setDuration(duration/1000);
        item.setFilePath(f.getPath());
        item.setFileSize(f.length());
        item.setIsServer(2);

        String fileName=f.getName().split("\\.")[0];
        String[] strings=fileName.split("-");
        if(strings.length==2){
            item.setSingerName(strings[0].trim());
            item.setMusicName(strings[1].trim());
        }else{
            item.setMusicName(fileName);
            item.setSingerName("未知");
        }
        item.setSingerId(-1);

        localMusicDao.insertLocalMusic(item);
    }

    /**
     * 检查后缀是否为mp3
     * @param format
     * @return
     */
    private boolean checkFormat(String format) {
        return "mp3".equals(format);
    }

    /**
     * 本地音乐的handler
     */
    static class LocalMusicHandler extends Handler{
        private int cnt=0;
        private LocalMusicActivity activity;

        LocalMusicHandler(LocalMusicActivity activity){
            this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_SEARCH_START:
                    cnt++;
                    break;

                case MESSAGE_SEARCH_END:
                    cnt--;
                    if(cnt==0){
                        if(InternetUtil.checkNet(activity)){
                            HttpUtil.checkIsServer(activity.localMusicDao.selectAllNeedCheck(),this);
                        }else{
                            activity.dialog.dismiss();
                            activity.adapter.setMusicList(activity.localMusicDao.selectAllLocalMusic());
                            activity.adapter.notifyDataSetChanged();
                        }
                    }
                    break;

                case MESSAGE_CHECK_IS_SERVER_END:
                    if(msg.obj!=null){
                        Map<String,Integer> result= (HashMap<String,Integer>) msg.obj;
                        for(Map.Entry<String,Integer> entry: result.entrySet()){
                            activity.localMusicDao.updateIsServerByMd5(entry.getValue(),entry.getKey());
                        }
                    }
                    activity.dialog.dismiss();
                    activity.adapter.setMusicList(activity.localMusicDao.selectAllLocalMusic());
                    activity.adapter.notifyDataSetChanged();
                    break;

                case MESSAGE_CHANGE_TEXT_VIEW:
                    activity.dialogTV.setText((String)msg.obj);
                    break;

                case MESSAGE_DELETE_MUSIC:
                    activity.localMusicDao.deleteMusicByMd5((String) msg.obj);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.local_music_play_all://播放全部
                localMusicDao.deleteAll();
                break;
            case R.id.local_music_multiple_choice://多选
                switchToolBar(TOOLBAR_MODE_MULTIPLE_CHOICE);
                break;
            case R.id.bottom_tool_next_play:
                if(!adapter.checkSelected()){
                    Toast toast=Toast.makeText(this, "请选择歌曲", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM,0,200);
                    toast.show();
                }
                break;
            case R.id.bottom_tool_add_to_playlist:
                if(!adapter.checkSelected()){
                    Toast toast=Toast.makeText(this, "请选择歌曲", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM,0,200);
                    toast.show();
                }
                int a=1;
                break;
            case R.id.bottom_tool_delete:
                if(!adapter.checkSelected()){
                    Toast toast=Toast.makeText(this, "请选择歌曲", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM,0,200);
                    toast.show();
                }else{
                    adapter.showDeleteDialog();
                }
                break;
        }
    }

    /**
     * 切换toolbar的模式
     * @param mode
     */

    private void switchToolBar(int mode){
        int afterMode=toolbarMode;
        toolbarMode=mode;
        invalidateOptionsMenu();

        String actionBarTitle="";
        switch (mode){
            case TOOLBAR_MODE_NORMAL:
                playAll.setVisibility(View.VISIBLE);
                edit.setVisibility(View.GONE);
                bottomToolBar.setVisibility(View.GONE);
                musicPlayTool.setVisibility(View.VISIBLE);
                adapter.selectAllCancel(); //清空选中状态
                actionBarTitle="本地音乐";

                if(afterMode==TOOLBAR_MODE_EDIT){
                    adapter.setMusicList(localMusicDao.selectAllLocalMusic());
                    adapter.notifyDataSetChanged();
                }
                break;
            case TOOLBAR_MODE_EDIT:
                playAll.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                bottomToolBar.setVisibility(View.GONE);
                musicPlayTool.setVisibility(View.VISIBLE);
                adapter.setMusicList(new ArrayList<Music>());
                adapter.notifyDataSetChanged();
                break;
            case TOOLBAR_MODE_MULTIPLE_CHOICE:
                playAll.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                bottomToolBar.setVisibility(View.VISIBLE);
                musicPlayTool.setVisibility(View.GONE);
                actionBarTitle="已选择0项";
                break;
        }

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(actionBarTitle);
        }

        adapter.setToolBarMode(toolbarMode);
        adapter.notifyDataSetChanged();
    }


    /**
     * 如果为普通模式就直接退出，
     * 否则退出到普通模式
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(toolbarMode!=TOOLBAR_MODE_NORMAL){
                switchToolBar(TOOLBAR_MODE_NORMAL);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
