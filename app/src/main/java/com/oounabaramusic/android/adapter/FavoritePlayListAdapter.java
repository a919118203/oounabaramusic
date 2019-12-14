package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oounabaramusic.android.EditPlayListInfoActivity;
import com.oounabaramusic.android.PlayListActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.util.ShowPopupWindow;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FavoritePlayListAdapter extends RecyclerView.Adapter<FavoritePlayListAdapter.ViewHolder> {

    private Activity activity;
    private FrameLayout rootView;

    public FavoritePlayListAdapter(Activity activity,FrameLayout rootView){
        this.activity=activity;
        this.rootView=rootView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.recyclerview_item_playlist,parent,false);
        return new FavoritePlayListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView playListCover;
        TextView playListName;
        TextView playListCnt;
        TextView popWindowTitle;
        ImageView playListItemMenu;
        ShowPopupWindow spw;
        ViewHolder(@NonNull View itemView) {
            super(itemView);

            spw=new ShowPopupWindow(createContentView(),rootView);

            playListCover=itemView.findViewById(R.id.playlist_cover);
            playListName=itemView.findViewById(R.id.playlist_name);
            playListCnt=itemView.findViewById(R.id.playlist_cnt);
            playListItemMenu=itemView.findViewById(R.id.my_playlist_item_menu);
            playListItemMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spw.showPopupMenu();
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

        private View createContentView() {
            View view=LayoutInflater.from(activity).inflate(R.layout.popupwindow_favorite_playlist_item_menu,null);

            //下载
            view.findViewById(R.id.item_menu_download).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
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
    }
}
