package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.widget.popupwindow.MyPopupWindow;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RPMusicNormalAdapter extends RecyclerView.Adapter<RPMusicNormalAdapter.ViewHolder> {

    private Activity activity;
    private MyPopupWindow spw;

    public RPMusicNormalAdapter(Activity activity){
        this.activity=activity;
        spw=new MyPopupWindow(activity,createContentView());
    }

    private View createContentView() {
        return LayoutInflater.from(activity).inflate(R.layout.pw_music_menu, (ViewGroup) activity.getWindow().getDecorView(),false);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_rp_music,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView menu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spw.showPopupWindow();
                }
            });
        }
    }
}
