package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

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
import com.oounabaramusic.android.widget.popupwindow.MyPopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainMyFragment extends Fragment implements View.OnClickListener{

    private MyPopupWindow pwMyPlaylistMenu;
    private MyPopupWindow pwFavoritePlaylistMenu;
    private OpenListAnimation myPlaylistAnim,favoritePlaylistAnim;
    private MyPlayListAdapter myPlayListAdapter;
    private RecyclerView myPlayListRv;
    private FavoritePlayListAdapter favoritePlayListAdapter;
    private RecyclerView favoritePlayListRv;
    private Activity activity;

    public MainMyFragment(Activity activity){
        this.activity=activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main_my,container,false);
        init(view);
        return view;
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

        myPlaylistAnim=new OpenListAnimation(view.findViewById(R.id.open_my_playlist),activity);
        favoritePlaylistAnim=new OpenListAnimation(view.findViewById(R.id.open_favorite_playlist),activity);

        myPlayListRv=view.findViewById(R.id.my_playlist_recycler_view);
        myPlayListRv.setAdapter(myPlayListAdapter=new MyPlayListAdapter(activity));
        myPlayListRv.setLayoutManager(new LinearLayoutManager(activity));

        favoritePlayListRv=view.findViewById(R.id.favorite_playlist_recycler_view);
        favoritePlayListRv.setAdapter(favoritePlayListAdapter=new FavoritePlayListAdapter(activity));
        favoritePlayListRv.setLayoutManager(new LinearLayoutManager(activity));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pwMyPlaylistMenu=new MyPopupWindow(activity,createMyContentView());
        pwFavoritePlaylistMenu=new MyPopupWindow(activity,createFavoriteContentView());
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
                Intent intent=new Intent(activity, PlayListManagementActivity.class);
                activity.startActivity(intent);
                pwFavoritePlaylistMenu.dismiss();
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
                showDialog();
                pwMyPlaylistMenu.dismiss();
            }
        });

        //点击管理歌单
        view.findViewById(R.id.menu_management_playlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, PlayListManagementActivity.class);
                activity.startActivity(intent);
                pwMyPlaylistMenu.dismiss();
            }
        });

        //点击恢复歌单
        view.findViewById(R.id.menu_restore_playlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, ResumePlayListActivity.class);
                activity.startActivity(intent);
                pwMyPlaylistMenu.dismiss();
            }
        });

        return view;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.my_playlist_menu:            //创建的歌单中的菜单按钮
                pwMyPlaylistMenu.showPopupWindow();
                break;
            case R.id.favorite_playlist_menu:      //收藏的歌单中的菜单按钮
                pwFavoritePlaylistMenu.showPopupWindow();
                break;
            case R.id.add_playlist:           //创建的歌单中的添加按钮（＋）
                showDialog();
                break;

            case R.id.local_music:           //点击本地音乐
                intent=new Intent(activity, LocalMusicActivity.class);
                activity.startActivity(intent);
                break;

            case R.id.recently_played:      //点击最近播放
                intent=new Intent(activity, RecentlyPlayedActivity.class);
                activity.startActivity(intent);
                break;

            case R.id.download_manager:      //点击下载管理
                intent=new Intent(activity, DownloadManagementActivity.class);
                activity.startActivity(intent);
                break;

            case R.id.my_collection:        //点击我的收藏
                intent=new Intent(activity, MyCollectionActivity.class);
                activity.startActivity(intent);
                break;
            case R.id.my_playlist:           //点击创建的歌单
                myPlaylistAnim.changeStatus();
                myPlayListRv.setVisibility(myPlaylistAnim.getStatus()==OpenListAnimation.STATUS_OPEN?View.VISIBLE:View.GONE);
                break;
            case R.id.favorite_playlist:     //点击收藏的歌单
                favoritePlaylistAnim.changeStatus();
                favoritePlayListRv.setVisibility(favoritePlaylistAnim.getStatus()==OpenListAnimation.STATUS_OPEN?View.VISIBLE:View.GONE);
                break;
        }
    }

    private void showDialog() {

        View contentView=LayoutInflater.from(activity).inflate(R.layout.alterdialog_add_playlist, (ViewGroup) activity.getWindow().getDecorView(),false);
        final EditText ct=contentView.findViewById(R.id.dialog_playlist_name);
        final TextView tv=contentView.findViewById(R.id.dialog_edit_count);

        final AlertDialog dialog=new AlertDialog.Builder(activity)
                .setTitle("新建歌单")
                .setView(contentView)
                .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
}
