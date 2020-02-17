package com.oounabaramusic.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SingerActivity;
import com.oounabaramusic.android.bean.Singer;
import com.oounabaramusic.android.fragment.MCSingerFragment;
import com.oounabaramusic.android.okhttputil.SingerClassificationHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;
import com.oounabaramusic.android.widget.popupwindow.MyBottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MCSingerAdapter extends RecyclerView.Adapter<MCSingerAdapter.ViewHolder> {

    private BaseActivity activity;
    private MCSingerFragment fragment;
    private List<Singer> dataList;

    public MCSingerAdapter(BaseActivity activity,MCSingerFragment fragment){
        this.fragment=fragment;
        this.activity=activity;
        dataList=new ArrayList<>();
    }

    private View createContentView() {

        return null;
    }

    public void setDataList(List<Singer> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_mc_singer,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Singer item = dataList.get(position);

        holder.singerCover.setImageUrl(MyEnvironment.serverBasePath+
                "music/loadSingerCover?singerId="+item.getId());
        holder.singerName.setText(item.getSingerName());
        holder.info.setText(item.getCountry()+","+item.getType());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyCircleImageView singerCover;
        TextView singerName,info;
        ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            singerCover=itemView.findViewById(R.id.singer_cover);
            singerName=itemView.findViewById(R.id.singer_name);
            info= itemView.findViewById(R.id.info);
            delete=itemView.findViewById(R.id.delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity, SingerActivity.class);
                    intent.putExtra("singer",dataList.get(getAdapterPosition()));
                    intent.putExtra("followed",dataList.get(getAdapterPosition()).getFollowed());
                    activity.startActivity(intent);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog=new AlertDialog.Builder(activity)
                            .setTitle("确定要取消关注吗？")
                            .setNegativeButton("不",null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SingerClassificationHttpUtil.cancelFollowSinger(
                                            activity,
                                            activity.sp.getString("userId","-1"),
                                            dataList.get(getAdapterPosition()).getId(),
                                            fragment.getHandler());
                                }
                            })
                            .create();

                    dialog.getWindow().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.layout_card_2));
                    dialog.show();

                }
            });
        }
    }
}
