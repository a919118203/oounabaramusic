package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oounabaramusic.android.PostActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.Post;
import com.oounabaramusic.android.fragment.MainNowSquareFragment;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;
import com.oounabaramusic.android.widget.customview.MyImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainNowSquareAdapter extends RecyclerView.Adapter<MainNowSquareAdapter.ViewHolder> {

    private Activity activity;
    private int width;//每个item的宽度
    private List<Post> dataList;
    private MainNowSquareFragment fragment;

    public MainNowSquareAdapter(Activity activity,MainNowSquareFragment fragment){
        this.activity=activity;
        this.fragment=fragment;
        dataList=new ArrayList<>();
        init();
    }

    public void setDataList(List<Post> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void addDataList(List<Post> dataList){
        if(dataList==null||dataList.isEmpty())
            return;

        int start=this.dataList.size();
        int len=dataList.size();
        this.dataList.addAll(dataList);
        notifyItemRangeInserted(start,len);
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
        Post item = dataList.get(position);

        holder.goodCnt.setText(String.valueOf(item.getGoodCnt()));
        holder.header.setImageUrl(MyEnvironment.serverBasePath+
                "loadUserHeader?userId="+item.getUserId());
        holder.content.setText(item.getContent());
        holder.name.setText(item.getUserName());

        if(item.getHasImage()){
            holder.iv.setVisibility(View.VISIBLE);
            holder.iv.setImage(new MyImage(MyImage.TYPE_POST_IMAGE,item.getId()));
        }else{
            holder.iv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyImageView iv;
        TextView content;
        MyCircleImageView header;
        TextView name;
        TextView goodCnt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.getLayoutParams().width=width;

            iv=itemView.findViewById(R.id.cover);
            iv.setSizeAdaptive(true,width);
            content=itemView.findViewById(R.id.content);
            header=itemView.findViewById(R.id.header);
            name=itemView.findViewById(R.id.name);
            goodCnt=itemView.findViewById(R.id.good_cnt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostActivity.startActivity(activity,dataList.get(getAdapterPosition()).getId());
                }
            });
        }
    }
}
