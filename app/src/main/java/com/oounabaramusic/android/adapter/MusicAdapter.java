package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oounabaramusic.android.MusicPlayActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.widget.popupwindow.MyPopupWindow;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private Activity activity;
    private MyPopupWindow spw;

    public MusicAdapter(Activity activity){
        this.activity=activity;
        spw=new MyPopupWindow(activity,createContentView());
    }

    private View createContentView() {
        return LayoutInflater.from(activity).inflate(R.layout.pw_music_menu, (ViewGroup) activity.getWindow().getDecorView(),false);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_music,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.index.setText(String.valueOf(position+1));
    }

    @Override
    public int getItemCount() {
        return 30;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView musicName,musicSinger,index;
        ImageView musicMenu,musicCover;
        TextView titleMusicSinger,MusicSinger,titleMusicName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity, MusicPlayActivity.class);
                    activity.startActivity(intent);
                }
            });

            musicName=itemView.findViewById(R.id.music_name);
            musicSinger=itemView.findViewById(R.id.music_singer);
            index=itemView.findViewById(R.id.index);
            musicMenu=itemView.findViewById(R.id.music_menu);
            titleMusicSinger=spw.getContentView().findViewById(R.id.music_singer);

            musicMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spw.showPopupWindow();
                }
            });
        }
    }
}
