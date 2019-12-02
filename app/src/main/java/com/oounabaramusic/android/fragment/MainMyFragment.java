package com.oounabaramusic.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.oounabaramusic.android.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainMyFragment extends Fragment implements View.OnClickListener{

    private ImageView myPlaylistMenu,favoritePlaylistMenu;

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
                break;
            case R.id.favorite_playlist_menu:
                break;
        }
    }
}
