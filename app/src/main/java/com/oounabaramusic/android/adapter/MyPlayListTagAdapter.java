package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.util.DensityUtil;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyPlayListTagAdapter extends RecyclerView.Adapter<MyPlayListTagAdapter.ViewHolder> {

    private Activity activity;
    private List<String> tags;
    private int width,height;

    public MyPlayListTagAdapter(Activity activity,List<String> tags){
        this.activity=activity;
        this.tags=tags;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_play_list_tag,parent,false);
        init(view);
        return new ViewHolder(view);
    }

    /**
     * 计算View的宽高，在左边添加drawable
     * @param view
     */
    private void init(View view) {
        if(width==0){
            int a= DensityUtil.getDisplayWidth(activity);
            int b=a-DensityUtil.dip2px(activity,80);
            width=b/4;
            height=width/2;
        }

        ViewGroup.LayoutParams lp=view.getLayoutParams();
        lp.width=this.width;
        lp.height=this.height;
        view.requestLayout();

        Drawable drawable=activity.getDrawable(R.mipmap.add);
        Objects.requireNonNull(drawable).setBounds(DensityUtil.dip2px(activity,10),0,DensityUtil.dip2px(activity,20),DensityUtil.dip2px(activity,10));

        TextView tv=view.findViewById(R.id.selectable);
        tv.setCompoundDrawables(drawable,null,null,null);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv1.setText(tags.get(position));
        holder.tv2.setText(tags.get(position));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv1,tv2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv1=itemView.findViewById(R.id.selectable);
            tv2=itemView.findViewById(R.id.not_selectable);
        }
    }
}
