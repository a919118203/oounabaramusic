package com.oounabaramusic.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.oounabaramusic.android.adapter.MusicPlayListAdapter;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.StatusBarUtil;
import com.oounabaramusic.android.widget.customview.PlayButton;
import com.oounabaramusic.android.widget.popupwindow.MyPopupWindow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BaseActivity extends AppCompatActivity {
    protected static final int CHOOSE_PHOTO=1;     //打开相册选择图片
    private MyPopupWindow musicPlayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initPlay();
    }

    private void initPlay() {
        musicPlayList=new MyPopupWindow(this,createContentView());

        if(findViewById(R.id.tool_current_play_layout)!=null){
            PlayButton pb=findViewById(R.id.music_play_button);
            pb.addOnClickPlayButtonListener(new PlayButton.OnClickPlayButtonListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onStop() {

                }
            });
            findViewById(R.id.music_play_list).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musicPlayList.showPopupWindow();
                }
            });

            findViewById(R.id.tool_current_play_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(BaseActivity.this, MusicPlayActivity.class);
                    BaseActivity.this.startActivity(intent);
                }
            });
        }
    }

    private View createContentView() {
        View view=LayoutInflater.from(this).inflate(R.layout.popupwindow_play_list, (ViewGroup) getWindow().getDecorView(),false);

        RecyclerView recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new MusicPlayListAdapter(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        return view;
    }

    protected void openAlbum(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }
}
