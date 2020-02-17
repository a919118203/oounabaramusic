package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oounabaramusic.android.PlayListTagActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.PlayListSmallTag;
import com.oounabaramusic.android.util.DensityUtil;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OtherPlayListTagAdapter extends RecyclerView.Adapter<OtherPlayListTagAdapter.ViewHolder> {

    private static final String ACTIVATION_TAG="a";
    private PlayListTagActivity activity;
    private List<PlayListSmallTag> tags;
    private boolean[] selected;

    private Drawable add;

    public OtherPlayListTagAdapter(PlayListTagActivity activity,List<PlayListSmallTag> tags){
        this.activity=activity;
        this.tags=tags;
        selected=new boolean[tags.size()];
        initDrawable();
    }

    private void initDrawable(){
        add=activity.getDrawable(R.mipmap.add);
        Objects.requireNonNull(add)
                .setBounds(
                        DensityUtil.dip2px(activity,10),
                        0,
                        DensityUtil.dip2px(activity,20),
                        DensityUtil.dip2px(activity,10));
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
        holder.tag.setEnabled(!selected[position]);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()){
            onBindViewHolder(holder, position);
        }else{

            String option = (String) payloads.get(0);

            switch (option){
                case ACTIVATION_TAG:
                    holder.tag.setEnabled(!selected[position]);
                    break;
            }
        }
    }

    /**
     * 将标签变回可用
     * @param tag
     */
    public void activationTag(PlayListSmallTag tag){
        int index = tags.indexOf(tag);
        selected[index]=false;
        notifyItemChanged(index,ACTIVATION_TAG);
    }

    /**
     * 将标签变回不可用
     * @param tag
     */
    public void selectTag(PlayListSmallTag tag){
        int index = tags.indexOf(tag);
        selected[index]=true;
        notifyItemChanged(index,ACTIVATION_TAG);
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
            tag.setCompoundDrawables(add,null,null,null);
            tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tag.setEnabled(false);
                    selected[getAdapterPosition()]=true;
                    activity.addTag(tags.get(getAdapterPosition()));
                }
            });
        }
    }
}
