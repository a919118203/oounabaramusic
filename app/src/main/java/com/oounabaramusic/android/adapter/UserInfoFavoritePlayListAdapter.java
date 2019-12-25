package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserInfoFavoritePlayListAdapter extends RecyclerView.Adapter<UserInfoFavoritePlayListAdapter.ViewHolder> {

    Activity activity;

    public UserInfoFavoritePlayListAdapter(Activity activity){
        this.activity=activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.recyclerview_item_playlist,parent,false);
        return init(view);
    }

    private ViewHolder init(View view) {
        view.findViewById(R.id.my_playlist_item_menu).setVisibility(View.GONE);


        ViewHolder viewHolder=new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}