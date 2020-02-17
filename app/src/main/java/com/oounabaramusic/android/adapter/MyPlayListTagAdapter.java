package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.oounabaramusic.android.PlayListTagActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.PlayListSmallTag;
import com.oounabaramusic.android.util.DensityUtil;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyPlayListTagAdapter extends RecyclerView.Adapter<MyPlayListTagAdapter.ViewHolder> {

    private PlayListTagActivity activity;
    private List<PlayListSmallTag> tags;

    private Drawable delete;

    public MyPlayListTagAdapter(PlayListTagActivity activity,List<PlayListSmallTag> tags){
        this.activity=activity;
        this.tags=tags;

        initDrawable();
    }

    private void initDrawable(){
        delete=activity.getDrawable(R.mipmap.delete_2);
        Objects.requireNonNull(delete).setBounds(
                DensityUtil.dip2px(activity,10),
                0,
                DensityUtil.dip2px(activity,20),
                DensityUtil.dip2px(activity,10));
    }

    public void addTag(PlayListSmallTag tag){
        tags.add(tag);
        notifyDataSetChanged();
    }

    public List<PlayListSmallTag> getTags() {
        return tags;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_play_list_tag,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayListSmallTag item = tags.get(position);

        holder.tag.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tag=itemView.findViewById(R.id.tag);
            tag.setCompoundDrawables(delete,null,null,null);
            tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tags.size()==3){
                        Toast.makeText(activity, "至少要有三个标签", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    PlayListSmallTag tag = tags.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    activity.removeTag(tag);
                }
            });
        }
    }
}
