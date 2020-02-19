package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oounabaramusic.android.PlayListActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlayListFragmentAdapter extends RecyclerView.Adapter<PlayListFragmentAdapter.ViewHolder> {

    private Activity activity;
    private List<PlayList> dataList;

    public PlayListFragmentAdapter(Activity activity){
        this.activity=activity;
        dataList=new ArrayList<>();
    }

    public void setDataList(List<PlayList> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_play_list_fragment,parent,false);
        int width= DensityUtil.getDisplayWidth(activity);
        view.findViewById(R.id.playlist_cover).getLayoutParams().width= (width-DensityUtil.dip2px(activity,60))/3;
        view.findViewById(R.id.playlist_cover).requestLayout();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayList item = dataList.get(position);

        holder.cover.setImage(new MyImage(MyImage.TYPE_PLAY_LIST_COVER,item.getId()));
        holder.name.setText(item.getPlayListName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyImageView cover;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cover=itemView.findViewById(R.id.playlist_cover);
            name=itemView.findViewById(R.id.name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayListActivity.startActivity(activity,
                            dataList.get(getAdapterPosition()).getId());
                }
            });
        }
    }
}
