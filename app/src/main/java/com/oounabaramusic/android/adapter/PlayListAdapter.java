package com.oounabaramusic.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oounabaramusic.android.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlayListViewHolder> {

    private Context context;

    public PlayListAdapter(Context context){
        this.context=context;
    }


    @NonNull
    @Override
    public PlayListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.recyclerview_item_playlist,parent,false);
        return new PlayListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class PlayListViewHolder extends RecyclerView.ViewHolder{

        ImageView playlistCover;//歌单封面
        TextView playlistName;//歌单的名字
        TextView playlistCnt;//歌单中歌曲的数目
        ImageView menu;//菜单
        View view;//item，用于添加点击事件

        PlayListViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            playlistCover=itemView.findViewById(R.id.playlist_cover);
            playlistName=itemView.findViewById(R.id.playlist_name);
            playlistCnt=itemView.findViewById(R.id.playlist_cnt);
            menu=itemView.findViewById(R.id.my_playlist_item_menu);

            menu.setVisibility(View.GONE);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
