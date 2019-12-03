package com.oounabaramusic.android.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.util.StatusBarUtil;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainMyFragment extends Fragment implements View.OnClickListener{

    private ImageView myPlaylistMenu,favoritePlaylistMenu;
    private PopupWindow pw;
    private View pwDismiss;//用于实现背景全变灰

    public MainMyFragment(View pwDismiss){
        this.pwDismiss=pwDismiss;
    }

    public MainMyFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main_my,null);
        init(view);
        return view;
    }
    private void init(View view) {
        myPlaylistMenu=view.findViewById(R.id.my_playlist_menu);
        favoritePlaylistMenu=view.findViewById(R.id.favorite_playlist_menu);

        myPlaylistMenu.setOnClickListener(this);
        favoritePlaylistMenu.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.my_playlist_menu:
                if(pwDismiss!=null){
                    pwDismiss.setVisibility(View.VISIBLE);
                }
                showPopupMenu();
                break;
            case R.id.favorite_playlist_menu:
                break;
            case R.id.menu_add_playlist:
                break;
            case R.id.popup_window_dismiss:
                pw.dismiss();
                break;
        }
    }

    private void showPopupMenu() {
        View contentView=LayoutInflater.from(getContext()).inflate(R.layout.popupwindow_my_playlist_menu,null);
        contentView.findViewById(R.id.menu_add_playlist).setOnClickListener(this);
        View rootView=LayoutInflater.from(getContext()).inflate(R.layout.activity_main,null);

        pw=new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pw.setFocusable(true);
        pw.setTouchable(true);
        pw.showAtLocation(rootView, Gravity.BOTTOM,0,0);
        pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(pwDismiss!=null){
                    pwDismiss.setVisibility(View.GONE);
                }
            }
        });
    }
}
