package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.UserInfoActivity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FollowedAdapter extends RecyclerView.Adapter<FollowedAdapter.ViewHolder> {

    private Activity activity;

    public FollowedAdapter(Activity activity){
        this.activity=activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.recyclerview_item_follow,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.findViewById(R.id.friend_menu).setVisibility(View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity, UserInfoActivity.class);
                    intent.putExtra("mode",UserInfoActivity.MODE_OTHER);
                    activity.startActivity(intent);
                }
            });
        }
    }
}
