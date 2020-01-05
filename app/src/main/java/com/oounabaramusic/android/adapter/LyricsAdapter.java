package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.util.LogUtil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LyricsAdapter extends RecyclerView.Adapter<LyricsAdapter.ViewHolder> {

    private Activity activity;
    private int current=1;
    private int[] heights;
    private int[] heightSum;
    private int headerHeight;  //整个列表中，为了让线能穿过每句歌词的中间，头和尾都应用这个高度的View来填充
    private int cnt;

    public LyricsAdapter(final Activity activity,int headerHeight){
        this.activity=activity;
        this.headerHeight=headerHeight;
        heights=new int[100];
        heightSum=new int[100];
        cnt=0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_lyrics,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position==0){
            holder.foot.setVisibility(View.GONE);
            holder.header.setVisibility(View.VISIBLE);
            holder.lyrics.setVisibility(View.GONE);

            //holder.header.getLayoutParams().height=headerHeight;
        }else if(position==99){
            holder.foot.setVisibility(View.VISIBLE);
            holder.header.setVisibility(View.GONE);
            holder.lyrics.setVisibility(View.GONE);
        }else{
            holder.foot.setVisibility(View.GONE);
            holder.header.setVisibility(View.GONE);
            holder.lyrics.setVisibility(View.VISIBLE);
        }

        if(current==position){
            holder.lyrics.setTextColor(activity.getResources().getColor(R.color.colorAccent));
        }else{
            holder.lyrics.setTextColor(activity.getResources().getColor(R.color.negative));
        }
    }

    @Override
    public int getItemCount() {
        return 100;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        View itemView;
        TextView lyrics;
        View header,foot;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.itemView=itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.printLog(header.getLayoutParams().height+"");
                }
            });

            lyrics=itemView.findViewById(R.id.lyrics);
            header=itemView.findViewById(R.id.header);
            foot=itemView.findViewById(R.id.foot);

            itemView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    itemView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int position=getLayoutPosition();
                    if(position>=1&&heights[position]==0){
                        heights[position]=itemView.getHeight();
                        heightSum[position]=heights[position]+heightSum[position-1];
                        cnt++;

                       // LogUtil.printLog(""+Arrays.toString(heightSum));
                    }
                    return true;
                }
            });

            header.getLayoutParams().height=headerHeight;
            foot.getLayoutParams().height=headerHeight;
        }
    }

    public int getHeight(int index){
        return heightSum[index];
    }

    public int search(int v){
        int i=0;
        for(;i<cnt;i++){
            if(heightSum[i]>v)
                return i;
        }
        return i;
    }

    public void setCurrent(int current) {
        this.current = current>98?98:current;
    }

    public int getCurrent() {
        return current;
    }
}
