package com.oounabaramusic.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ResumePlayListAdapter extends RecyclerView.Adapter<ResumePlayListAdapter.ViewHolder> {

    private Activity activity;
    private List<PlayList> dataList;
    private boolean[] selected;

    public ResumePlayListAdapter(Activity activity){
        this.activity=activity;
        dataList=new ArrayList<>();
        selected=new boolean[dataList.size()];
    }

    public void setDataList(List<PlayList> dataList) {
        this.dataList = dataList;
        selected=new boolean[dataList.size()];
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_resume_item,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayList item=dataList.get(position);

        holder.cb.setChecked(selected[position]);
        holder.itemName.setText(item.getPlayListName());
        holder.itemContent.setText(item.getCnt()+"首");
        holder.playListCover.setImage(new MyImage(MyImage.TYPE_PLAY_LIST_COVER,item.getId()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public List<Integer> getSelected(){
        List<Integer> ids=new ArrayList<>();
        for(int i=0;i<dataList.size();i++){
            if(selected[i]){
                ids.add(dataList.get(i).getId());
            }
        }
        return ids;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyImageView playListCover;
        LinearLayout playListInfo;
        TextView itemName,itemContent;
        CheckBox cb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            playListCover=itemView.findViewById(R.id.item_cover);
            playListInfo=itemView.findViewById(R.id.item_info);
            itemName=itemView.findViewById(R.id.item_name);
            itemContent=itemView.findViewById(R.id.item_content);
            cb=itemView.findViewById(R.id.item_check_box);

            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    selected[getAdapterPosition()]=isChecked;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cb.setChecked(!cb.isChecked());
                }
            });
        }
    }
}
