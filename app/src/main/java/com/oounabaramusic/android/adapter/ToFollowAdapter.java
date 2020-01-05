package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.oounabaramusic.android.MessageActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.UserInfoActivity;
import com.oounabaramusic.android.widget.popupwindow.MyPopupWindow;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ToFollowAdapter extends RecyclerView.Adapter<ToFollowAdapter.ViewHolder> {

    private Activity activity;

    public ToFollowAdapter(Activity activity){
        this.activity=activity;
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

        PopupWindow pw;

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

            pw=new MyPopupWindow(activity,createContentView());
            itemView.findViewById(R.id.friend_menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM,0,0);
                }
            });


        }

        private View createContentView() {
            View view=LayoutInflater.from(activity).inflate(R.layout.popupwindow_friend_menu, (ViewGroup) activity.getWindow().getDecorView(),false);

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
    }
}
