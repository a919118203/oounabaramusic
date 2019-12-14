package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.util.ShowPopupWindow;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private Activity activity;
    private ShowPopupWindow spw;

    public MusicAdapter(Activity activity, FrameLayout rootView){
        this.activity=activity;
        spw=new ShowPopupWindow(createContentView(rootView),rootView);
    }

    private View createContentView(ViewGroup rootView) {
        View contentView=LayoutInflater.from(activity).inflate(R.layout.popupwindow_music_menu,rootView,false);

        return contentView;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.recyclerview_item_music,parent,false);
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
                    spw.showPopupMenu();
                }
            });
        }
    }
}
