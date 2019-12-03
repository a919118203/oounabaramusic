package com.oounabaramusic.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.oounabaramusic.android.LocalMusicActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.ShowPopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainMyFragment extends Fragment implements View.OnClickListener{

    private ImageView myPlaylistMenu,favoritePlaylistMenu;
    private LinearLayout localMusic,recentlyPlayed,downloadManager,myCollection;
    private ShowPopupWindow pwMyPlaylistMenu;
    private ShowPopupWindow pwFavoritePlaylistMenu;
    private FrameLayout rootView;

    public MainMyFragment(FrameLayout rootView){
        this.rootView=rootView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main_my,null);
        init(view);
        return view;
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

    private void init(View view) {
        myPlaylistMenu=view.findViewById(R.id.my_playlist_menu);
        favoritePlaylistMenu=view.findViewById(R.id.favorite_playlist_menu);
        localMusic=view.findViewById(R.id.local_music);
        recentlyPlayed=view.findViewById(R.id.recently_played);
        downloadManager=view.findViewById(R.id.download_manager);
        myCollection=view.findViewById(R.id.my_collection);

        myPlaylistMenu.setOnClickListener(this);
        favoritePlaylistMenu.setOnClickListener(this);
        localMusic.setOnClickListener(this);
        recentlyPlayed.setOnClickListener(this);
        downloadManager.setOnClickListener(this);
        myCollection.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.my_playlist_menu:
                pwMyPlaylistMenu.showPopupMenu();
                break;
            case R.id.favorite_playlist_menu:
                pwFavoritePlaylistMenu.showPopupMenu();
                break;
            case R.id.menu_add_playlist:
                showDialog();
                break;
            case R.id.local_music:
                intent=new Intent(getActivity(), LocalMusicActivity.class);
                getActivity().startActivity(intent);
                break;
        }
    }

    private void showDialog() {

    }
}
