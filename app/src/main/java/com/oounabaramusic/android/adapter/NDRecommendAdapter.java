package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.util.DensityUtil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NDRecommendAdapter extends RecyclerView.Adapter<NDRecommendAdapter.ViewHolder> {

    private Activity activity;

    public NDRecommendAdapter(Activity activity){
        this.activity=activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_nd_recommend,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position==0){
            holder.itemView.setPadding(DensityUtil.dip2px(activity,10),0,0,0);
        }else if(position==9){
            holder.itemView.setPadding(0,0,DensityUtil.dip2px(activity,10),0);
        }else{
            holder.itemView.setPadding(0,0,0,0);
        }
        holder.itemView.requestLayout();
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView=itemView;
        }
    }
}
