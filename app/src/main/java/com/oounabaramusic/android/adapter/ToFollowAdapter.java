package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.MessageActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.UserInfoActivity;
import com.oounabaramusic.android.widget.popupwindow.MyBottomSheetDialog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ToFollowAdapter extends RecyclerView.Adapter<ToFollowAdapter.ViewHolder> {

    private Activity activity;
    private MyBottomSheetDialog pw;

    public ToFollowAdapter(Activity activity){
        this.activity=activity;
        pw=new MyBottomSheetDialog(activity);
        pw.setContentView(createContentView());
    }

    private View createContentView() {
        View view=LayoutInflater.from(activity).inflate(R.layout.pw_friend_menu, (ViewGroup) activity.getWindow().getDecorView(),false);

        view.findViewById(R.id.send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, MessageActivity.class);
                activity.startActivity(intent);
                pw.dismiss();
            }
        });

        return view;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_follow,parent,false);

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

            itemView.findViewById(R.id.friend_follow).setVisibility(View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity, UserInfoActivity.class);
                    intent.putExtra("mode",UserInfoActivity.MODE_OTHER);
                    activity.startActivity(intent);
                }
            });

            itemView.findViewById(R.id.friend_menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.show();
                }
            });


        }
    }
}
