package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.util.DensityUtil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainNowSquareAdapter extends RecyclerView.Adapter<MainNowSquareAdapter.ViewHolder> {

    private Activity activity;
    private int width;//每个item的宽度
    private int[] imageIds={R.mipmap.default_image,R.mipmap.title_page};

    public MainNowSquareAdapter(Activity activity){
        this.activity=activity;
        init();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_now_square,parent,false);
        return new ViewHolder(view);
    }

    private void init() {
        if(width==0){
            int a= DensityUtil.getDisplayWidth(activity);
            int b=a-DensityUtil.dip2px(activity,50);
            width=b/2;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int id=imageIds[position&1];

        Bitmap bitmap= BitmapFactory.decodeResource(activity.getResources(),id);
        int height=bitmap.getHeight();
        int width=bitmap.getWidth();
        ViewGroup.LayoutParams lp = holder.iv.getLayoutParams();
        lp.width=this.width;
        lp.height=(this.width*height)/width;
        holder.iv.requestLayout();
        holder.iv.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView iv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv=itemView.findViewById(R.id.cover);
        }
    }
}
