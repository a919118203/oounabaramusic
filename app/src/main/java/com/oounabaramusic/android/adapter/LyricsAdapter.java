package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.oounabaramusic.android.MusicPlayActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Lrc;
import com.oounabaramusic.android.util.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LyricsAdapter extends RecyclerView.Adapter<LyricsAdapter.ViewHolder> {

    private MusicPlayActivity activity;
    private int current=1;
    private int[] heights;
    private int headerHeight;  //整个列表中，为了让线能穿过每句歌词的中间，头和尾都应用这个高度的View来填充
    private int cnt;
    private Lrc lrc;

    public LyricsAdapter(MusicPlayActivity activity,int headerHeight){
        this.activity=activity;
        this.headerHeight=headerHeight;

        initDataList();

        heights=new int[lrc.getContent().size()];
        cnt=0;
    }

    private void initDataList() {
        lrc=new Lrc();
        lrc.setContent(new ArrayList<String>());
        lrc.setTimes(new ArrayList<Long>());
    }

    public void setLrc(Lrc lrc) {
        this.lrc = lrc;
        heights=new int[lrc.getContent().size()];
        cnt=0;

        LogUtil.printLog("setLrc");
        notifyDataSetChanged();
    }

    public Lrc getLrc() {
        return lrc;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_lyrics,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if(position==0){
            holder.foot.setVisibility(View.GONE);
            holder.header.setVisibility(View.VISIBLE);
            holder.lyrics.setVisibility(View.GONE);
        }else if(position==lrc.getContent().size()-1){
            holder.foot.setVisibility(View.VISIBLE);
            holder.header.setVisibility(View.GONE);
            holder.lyrics.setVisibility(View.GONE);
        }else{
            holder.foot.setVisibility(View.GONE);
            holder.header.setVisibility(View.GONE);
            holder.lyrics.setVisibility(View.VISIBLE);
        }

        holder.lyrics.setText(lrc.getContent().get(position));

        if(current==position){
            holder.lyrics.setTextColor(activity.getResources().getColor(R.color.colorAccent));
        }else{
            holder.lyrics.setTextColor(activity.getResources().getColor(R.color.negative));
        }

        holder.itemView.post(new Runnable() {
            @Override
            public void run() {
                if(position>=1&&holder.itemView.getHeight()!=0){
                    heights[position]=holder.itemView.getHeight();
                    cnt=position>cnt?position:cnt;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return lrc.getContent().size();
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
                    activity.switchMode();
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

                    return true;
                }
            });

            header.getLayoutParams().height=headerHeight;
            foot.getLayoutParams().height=headerHeight;
        }
    }

    public int getHeight(int index){
        int result=0;
        for(int i=0;i<heights.length&&i<=index;i++){
            result+=heights[i];
        }
        return result;
    }

    public int search(int v){
        int i=0;
        int height=0;
        for(;i<cnt;i++){
            if((height+=heights[i])>v)
                return i;
        }
        return i;
    }

    public int getIndex(long time){
        int ii= (int) (time+lrc.getOffset());
        for(int i=0;i<lrc.getTimes().size();i++){
            if(lrc.getTimes().get(i)>=ii){
                return i-1;
            }
        }
        return 1;
    }

    public void setCurrent(int current) {
        this.current = current>lrc.getContent().size()-1?lrc.getContent().size()-1:current;
    }

    public int getCurrent() {
        return current;
    }
}
