package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.ForwardActivity;
import com.oounabaramusic.android.PostActivity;
import com.oounabaramusic.android.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserInfoPostAdapter extends RecyclerView.Adapter<UserInfoPostAdapter.ViewHolder> {

    private Activity activity;

    public UserInfoPostAdapter(Activity activity){
        this.activity=activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_post,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.findViewById(R.id.post_comment).setOnClickListener(this);
            itemView.findViewById(R.id.post_comment_1).setOnClickListener(this);
            itemView.findViewById(R.id.post_forward_1).setOnClickListener(this);
            itemView.findViewById(R.id.post_forward).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()){
                case R.id.post_comment:
                case R.id.post_comment_1:
                    intent=new Intent(activity, PostActivity.class);
                    activity.startActivity(intent);
                    break;
                case R.id.post_forward:
                case R.id.post_forward_1:
                    intent=new Intent(activity, ForwardActivity.class);
                    activity.startActivity(intent);
                    break;
            }
        }
    }
}
