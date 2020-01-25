package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oounabaramusic.android.EditPlayListInfoActivity;
import com.oounabaramusic.android.PlayListActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.widget.popupwindow.MyBottomSheetDialog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyPlayListAdapter extends RecyclerView.Adapter<MyPlayListAdapter.ViewHolder> {

    private Activity activity;
    private MyBottomSheetDialog spw;

    private TextView popWindowTitle;

    public MyPlayListAdapter(Activity activity){
        this.activity=activity;
        spw=new MyBottomSheetDialog(activity);
        spw.setContentView(createContentView());
    }

    private View createContentView() {
        View view=LayoutInflater.from(activity).inflate(R.layout.pw_my_playlist_item_menu, (ViewGroup) activity.getWindow().getDecorView(),false);

        //下载
        view.findViewById(R.id.item_menu_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        //编辑歌单信息
        view.findViewById(R.id.item_menu_edit_playlist_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, EditPlayListInfoActivity.class);
                activity.startActivity(intent);
                spw.dismiss();
            }
        });

        //删除
        view.findViewById(R.id.item_menu_delete_playlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        popWindowTitle=view.findViewById(R.id.menu_title);
        return view;
    }

    private void showDialog(){
        new AlertDialog.Builder(activity).show();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_play_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 21;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView playListCover;
        TextView playListName;
        TextView playListCnt;
        ImageView playListItemMenu;
        ViewHolder(@NonNull View itemView) {
            super(itemView);


            playListCover=itemView.findViewById(R.id.playlist_cover);
            playListName=itemView.findViewById(R.id.playlist_name);
            playListCnt=itemView.findViewById(R.id.playlist_cnt);
            playListItemMenu=itemView.findViewById(R.id.my_playlist_item_menu);
            playListItemMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spw.show();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity, PlayListActivity.class);
                    activity.startActivity(intent);
                }
            });
        }
    }
}
