package com.oounabaramusic.android.adapter;

import android.annotation.SuppressLint;
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
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyImageView;
import com.oounabaramusic.android.widget.popupwindow.MyBottomSheetDialog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyPlayListAdapter extends RecyclerView.Adapter<MyPlayListAdapter.ViewHolder> {

    private Activity activity;
    private MyBottomSheetDialog spw;
    private LinkedList<PlayList> dataList;
    private int popupPosition;

    private TextView popWindowTitle;

    public MyPlayListAdapter(Activity activity){
        this.activity=activity;
        spw=new MyBottomSheetDialog(activity);
        spw.setContentView(createContentView());
        dataList=new LinkedList<>();
    }

    public void setDataList(LinkedList<PlayList> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void addData(PlayList item){
        dataList.add(0,item);
        notifyDataSetChanged();
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayList item=dataList.get(position);

        holder.playListCover.setImageUrl(MyEnvironment.serverBasePath+
                "loadPlayListCover?playListId="+
                item.getId());
        holder.playListName.setText(item.getPlayListName());
        holder.playListCnt.setText(item.getCnt()+"首");
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        MyImageView playListCover;
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
                    popupPosition=getAdapterPosition();
                    spw.show();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity, PlayListActivity.class);
                    intent.putExtra("playList",dataList.get(getAdapterPosition()));
                    activity.startActivity(intent);
                }
            });
        }
    }
}
