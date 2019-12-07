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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.oounabaramusic.android.LocalMusicActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.FavoritePlayListAdapter;
import com.oounabaramusic.android.adapter.MyPlayListAdapter;
import com.oounabaramusic.android.anim.OpenListAnimation;
import com.oounabaramusic.android.util.ShowPopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainMyFragment extends Fragment implements View.OnClickListener{

    private ShowPopupWindow pwMyPlaylistMenu;
    private ShowPopupWindow pwFavoritePlaylistMenu;
    private OpenListAnimation myPlaylistAnim,favoritePlaylistAnim;
    private FrameLayout rootView;
    private MyPlayListAdapter myPlayListAdapter;
    private RecyclerView myPlayListRv;
    private FavoritePlayListAdapter favoritePlayListAdapter;
    private RecyclerView favoritePlayListRv;
    private Activity activity;

    public MainMyFragment(FrameLayout rootView,Activity activity){
        this.rootView=rootView;
        this.activity=activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main_my,null);
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
        myPlayListRv.setAdapter(myPlayListAdapter=new MyPlayListAdapter(activity,rootView));
        myPlayListRv.setLayoutManager(new LinearLayoutManager(activity));

        favoritePlayListRv=view.findViewById(R.id.favorite_playlist_recycler_view);
        favoritePlayListRv.setAdapter(favoritePlayListAdapter=new FavoritePlayListAdapter(activity,rootView));
        favoritePlayListRv.setLayoutManager(new LinearLayoutManager(activity));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pwMyPlaylistMenu=new ShowPopupWindow(createMyContentView(),rootView);
        pwFavoritePlaylistMenu=new ShowPopupWindow(createFavoriteContentView(),rootView);
    }

    private View createFavoriteContentView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.popupwindow_favorite_playlist_menu,null);
    }

    private View createMyContentView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.popupwindow_my_playlist_menu,null);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.my_playlist_menu:            //创建的歌单中的菜单按钮
                pwMyPlaylistMenu.showPopupMenu();
                break;
            case R.id.favorite_playlist_menu:      //收藏的歌单中的菜单按钮
                pwFavoritePlaylistMenu.showPopupMenu();
                break;
            case R.id.add_playlist:           //创建的歌单中的添加按钮（＋）
                showDialog();
                break;

            case R.id.local_music:           //点击“本地音乐”
                intent=new Intent(activity, LocalMusicActivity.class);
                activity.startActivity(intent);
                break;
            case R.id.download_manager:      //点击“下载管理”
                break;
            case R.id.my_playlist:           //点击“创建的歌单”
                myPlaylistAnim.changeStatus();
                myPlayListRv.setVisibility(myPlaylistAnim.getStatus()==OpenListAnimation.STATUS_OPEN?View.VISIBLE:View.GONE);
                break;
            case R.id.favorite_playlist:     //点击“收藏的歌单”
                favoritePlaylistAnim.changeStatus();
                favoritePlayListRv.setVisibility(favoritePlaylistAnim.getStatus()==OpenListAnimation.STATUS_OPEN?View.VISIBLE:View.GONE);
                break;
        }
    }

    private void showDialog() {

        View contentView=LayoutInflater.from(activity).inflate(R.layout.alterdialog_add_playlist,null);
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
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity,R.color.font_negative));

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
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity,R.color.font_negative));
                }

                tv.setText(new String(ct.getText().toString().length()+"/40"));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
