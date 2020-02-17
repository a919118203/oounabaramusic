package com.oounabaramusic.android.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.DownloadManagementActivity;
import com.oounabaramusic.android.LocalMusicActivity;
import com.oounabaramusic.android.MyCollectionActivity;
import com.oounabaramusic.android.PlayListManagementActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.RecentlyPlayedActivity;
import com.oounabaramusic.android.ResumePlayListActivity;
import com.oounabaramusic.android.adapter.FavoritePlayListAdapter;
import com.oounabaramusic.android.adapter.MyPlayListAdapter;
import com.oounabaramusic.android.anim.OpenListAnimation;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.PlayListHttpUtil;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.widget.popupwindow.MyBottomSheetDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainMyFragment extends Fragment implements View.OnClickListener{

    private MyBottomSheetDialog dialogMyPlaylistMenu;
    private MyBottomSheetDialog dialogFavoritePlaylistMenu;
    private OpenListAnimation myPlaylistAnim,favoritePlaylistAnim;
    private BaseActivity activity;
    private View rootView;


    private MyPlayListAdapter myPlayListAdapter;
    private RecyclerView myPlayListRv;
    private FavoritePlayListAdapter favoritePlayListAdapter;
    private RecyclerView favoritePlayListRv;
    private ProgressBar pbMy;
    private ProgressBar pbFa;

    private SharedPreferences sp;
    private Handler handler;

    public MainMyFragment(BaseActivity activity){
        this.activity=activity;
        sp= PreferenceManager.getDefaultSharedPreferences(activity);
        handler=new MainMyHandler(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(R.layout.fragment_main_my,container,false);
            init(rootView);
        }
        return rootView;
    }

    /**
     * 重新加载我的歌单
     */
    @Override
    public void onResume() {
        super.onResume();
        loadMyPlayList(SharedPreferencesUtil.getUserId(sp)+"");
        loadFavoritePlaylist(SharedPreferencesUtil.getUserId(sp));
    }

    private void init(View view) {
        view.findViewById(R.id.my_playlist_menu).setOnClickListener(this);
        view.findViewById(R.id.favorite_playlist_menu).setOnClickListener(this);
        view.findViewById(R.id.local_music).setOnClickListener(this);
        view.findViewById(R.id.recently_played).setOnClickListener(this);
        view.findViewById(R.id.download_manager).setOnClickListener(this);
        view.findViewById(R.id.my_collection).setOnClickListener(this);
        view.findViewById(R.id.add_playlist).setOnClickListener(this);
        view.findViewById(R.id.my_playlist).setOnClickListener(this);
        view.findViewById(R.id.favorite_playlist).setOnClickListener(this);
        pbMy=view.findViewById(R.id.load_my);
        pbFa=view.findViewById(R.id.load_favorite);

        myPlaylistAnim=new OpenListAnimation(view.findViewById(R.id.open_my_playlist),activity);
        favoritePlaylistAnim=new OpenListAnimation(view.findViewById(R.id.open_favorite_playlist),activity);

        myPlayListRv=view.findViewById(R.id.my_playlist_recycler_view);
        myPlayListRv.setAdapter(myPlayListAdapter=new MyPlayListAdapter(activity,this));
        myPlayListRv.setLayoutManager(new LinearLayoutManager(activity));

        favoritePlayListRv=view.findViewById(R.id.favorite_playlist_recycler_view);
        favoritePlayListRv.setAdapter(favoritePlayListAdapter=new FavoritePlayListAdapter(activity,this));
        favoritePlayListRv.setLayoutManager(new LinearLayoutManager(activity));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dialogMyPlaylistMenu=new MyBottomSheetDialog(activity);
        dialogMyPlaylistMenu.setContentView(createMyContentView());

        dialogFavoritePlaylistMenu =new MyBottomSheetDialog(activity);
        dialogFavoritePlaylistMenu.setContentView(createFavoriteContentView());
    }


    /**
     * 收藏的歌单的菜单
     * @return
     */
    private View createFavoriteContentView() {
        View view=LayoutInflater.from(getContext()).inflate(R.layout.pw_favorite_playlist_menu, (ViewGroup) activity.getWindow().getDecorView(),false);

        //点击管理歌单
        view.findViewById(R.id.menu_management_playlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sp.getBoolean("login",false)){
                    Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }

                int userId = SharedPreferencesUtil.getUserId(sp);

                Intent intent=new Intent(activity, PlayListManagementActivity.class);
                intent.putExtra("type",PlayListManagementActivity.COLLECTION_PLAY_LIST);
                activity.startActivity(intent);
                dialogFavoritePlaylistMenu.dismiss();
            }
        });

        return view;
    }

    /**
     * 创建的歌单的菜单
     * @return
     */
    private View createMyContentView() {
        View view=LayoutInflater.from(getContext()).inflate(
                R.layout.pw_my_playlist_menu,
                (ViewGroup) activity.getWindow().getDecorView(),
                false);

        //点击创建新歌单
        view.findViewById(R.id.menu_add_playlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sp.getBoolean("login",false)){
                    Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                showDialog();
                dialogMyPlaylistMenu.dismiss();
            }
        });

        //点击管理歌单
        view.findViewById(R.id.menu_management_playlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sp.getBoolean("login",false)){
                    Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=new Intent(activity, PlayListManagementActivity.class);

                int userId = SharedPreferencesUtil.getUserId(sp);
                List<PlayList> dataList = myPlayListAdapter.getDataList();
                intent.putExtra("type",PlayListManagementActivity.MY_PLAY_LIST);
                activity.startActivity(intent);
                dialogMyPlaylistMenu.dismiss();
            }
        });

        //点击恢复歌单
        view.findViewById(R.id.menu_restore_playlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sp.getBoolean("login",false)){
                    Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=new Intent(activity, ResumePlayListActivity.class);
                activity.startActivity(intent);
                dialogMyPlaylistMenu.dismiss();
            }
        });

        return view;
    }

    @Override
    public void onClick(View view) {
        String userId=sp.getString("userId","-1");
        Intent intent;
        switch (view.getId()){
            case R.id.my_playlist_menu:            //创建的歌单中的菜单按钮
                dialogMyPlaylistMenu.show();
                break;
            case R.id.favorite_playlist_menu:      //收藏的歌单中的菜单按钮
                dialogFavoritePlaylistMenu.show();
                break;
            case R.id.add_playlist:           //创建的歌单中的添加按钮（＋）
                showDialog();
                break;

            case R.id.local_music:           //点击本地音乐
                intent=new Intent(activity, LocalMusicActivity.class);
                activity.startActivity(intent);
                break;

            case R.id.recently_played:      //点击最近播放
                if(!sp.getBoolean("login",false)){
                    Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
                    break;
                }

                intent=new Intent(activity, RecentlyPlayedActivity.class);
                activity.startActivity(intent);
                break;

            case R.id.download_manager:      //点击下载管理
                intent=new Intent(activity, DownloadManagementActivity.class);
                activity.startActivity(intent);
                break;

            case R.id.my_collection:        //点击我的收藏
                if(!sp.getBoolean("login",false)){
                    Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
                    break;
                }

                intent=new Intent(activity, MyCollectionActivity.class);
                activity.startActivity(intent);
                break;
            case R.id.my_playlist:           //点击创建的歌单
                if(!sp.getBoolean("login",false)){
                    Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
                    break;
                }

                myPlaylistAnim.changeStatus();
                loadMyPlayList(userId);
                break;
            case R.id.favorite_playlist:     //点击收藏的歌单
                if(!sp.getBoolean("login",false)){
                    Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
                    break;
                }

                favoritePlaylistAnim.changeStatus();
                loadFavoritePlaylist(SharedPreferencesUtil.getUserId(sp));
                break;
        }
    }

    public void loadMyPlayList(String userId){
        if(myPlaylistAnim.getStatus()==OpenListAnimation.STATUS_OPEN){
            if(myPlayListAdapter.getItemCount()!=0){
                myPlayListRv.setVisibility(View.VISIBLE);
            }else{
                pbMy.setVisibility(View.VISIBLE);
            }

            Map<String,Integer> data = new HashMap<>();
            data.put("userId",Integer.valueOf(userId));
            data.put("start",-1);

            new S2SHttpUtil(
                    activity,
                    new Gson().toJson(data),
                    MyEnvironment.serverBasePath+"findPlayListByUser",
                    handler)
                    .call(PlayListHttpUtil.MESSAGE_FIND_MY_PLAY_LIST_END);
        }else{
            myPlayListRv.setVisibility(View.GONE);
        }
    }

    public void loadFavoritePlaylist(int userId){
        if(favoritePlaylistAnim.getStatus()==OpenListAnimation.STATUS_OPEN){
            if(favoritePlayListAdapter.getItemCount()!=0){
                favoritePlayListRv.setVisibility(View.VISIBLE);
            }else{
                pbFa.setVisibility(View.VISIBLE);
            }

            Map<String,Integer> data = new HashMap<>();
            data.put("userId",userId);
            data.put("start",-1);

            new S2SHttpUtil(
                    activity,
                    new Gson().toJson(data),
                    MyEnvironment.serverBasePath+"getCollectPlayList",
                    handler)
            .call(BasicCode.GET_CONTENT);
        }else{
            favoritePlayListRv.setVisibility(View.GONE);
        }
    }

    private void showDialog() {
        if(!sp.getBoolean("login",false)){
            Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        View contentView=LayoutInflater.from(activity).inflate(R.layout.alertdialog_add_playlist, (ViewGroup) activity.getWindow().getDecorView(),false);
        final EditText ct=contentView.findViewById(R.id.dialog_playlist_name);
        final TextView tv=contentView.findViewById(R.id.dialog_edit_count);

        final AlertDialog dialog=new AlertDialog.Builder(activity)
                .setTitle("新建歌单")
                .setView(contentView)
                .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!sp.getBoolean("login",false)){
                            Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        PlayListHttpUtil.createPlayList(
                                activity,
                                sp.getString("userId","-1"),
                                ct.getText().toString(),
                                handler);
                    }
                })
                .setNegativeButton("取消",null)
                .show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(false);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity,R.color.negative));

        ct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(ct.getText().toString().length()>0&&ct.getText().toString().length()<=40){
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(true);
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity,R.color.blue));
                }else{
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(false);
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity,R.color.negative));
                }

                tv.setText(new String(ct.getText().toString().length()+"/40"));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    static class MainMyHandler extends Handler{
        MainMyFragment fragment;
        MainMyHandler(MainMyFragment fragment){
            this.fragment=fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PlayListHttpUtil.MESSAGE_FIND_MY_PLAY_LIST_END:
                    fragment.pbMy.setVisibility(View.GONE);
                    fragment.myPlayListRv.setVisibility(View.VISIBLE);
                    Map<String,String> data =new Gson().fromJson((String)msg.obj,
                            new TypeToken<Map<String,String>>(){}.getType());

                    List<PlayList> dataList=new Gson().fromJson(data.get("playLists"),
                            new TypeToken<List<PlayList>>(){}.getType());
                    PlayList myLove = dataList.remove(dataList.size()-1);
                    dataList.add(0,myLove);

                    fragment.myPlayListAdapter.setDataList(
                            new LinkedList<PlayList>(dataList));
                    break;
                case PlayListHttpUtil.MESSAGE_CREATE_END:

                    PlayList item=new Gson().fromJson(
                            (String)msg.obj,
                            new TypeToken<PlayList>(){}.getType());
                    fragment.myPlayListAdapter.addData(item);
                    break;

                case BasicCode.GET_CONTENT:
                    fragment.pbFa.setVisibility(View.GONE);
                    fragment.favoritePlayListRv.setVisibility(View.VISIBLE);
                    data = new Gson().fromJson((String)msg.obj,
                            new TypeToken<Map<String,String>>(){}.getType());

                    dataList=new Gson().fromJson(data.get("playLists"),
                            new TypeToken<List<PlayList>>(){}.getType());
                    fragment.favoritePlayListAdapter.setDataList(dataList);
                    break;
            }
        }
    }
}
