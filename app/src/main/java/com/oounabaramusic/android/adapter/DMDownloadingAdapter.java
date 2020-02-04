package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oounabaramusic.android.DownloadManagementActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.service.DownloadService;
import com.oounabaramusic.android.util.FormatUtil;
import com.oounabaramusic.android.util.LogUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DMDownloadingAdapter extends RecyclerView.Adapter<DMDownloadingAdapter.ViewHolder> {

    //开始下载，显示出进度条
    private static final String CHANGE_ITEM="a";

    //更新进度
    private static final String CHANGE_PROGRESS="b";

    private static final String CHANGE_STOP_TASK="c";

    private DownloadManagementActivity activity;
    private List<Music> dataList;
    private List<Boolean> downloadable;
    private long progress;
    private DMDownloadingHandler handler;

    public DMDownloadingAdapter(DownloadManagementActivity activity){
        this.activity=activity;
        dataList=activity.getDownloadBinder().getDownloadList();
        downloadable=activity.getDownloadBinder().getDownloadable();
        handler=new DMDownloadingHandler(this);
        activity.getDownloadBinder().addHandler(handler);
        activity.getDownloadBinder().addAdapter(this);
    }

    public void setDataList(List<Music> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public DownloadManagementActivity getActivity() {
        return activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_dm_downloading,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.musicName.setText(dataList.get(position).getMusicName());
        if(position==activity.getDownloadBinder().getPosition()){
            if(holder.pb.getVisibility()!=View.VISIBLE){
                holder.pb.setVisibility(View.VISIBLE);
            }
            holder.message.setText(FormatUtil.progressFormat(progress,
                    dataList.get(position).getFileSize()));
            holder.pb.setMax((int) dataList.get(position).getFileSize());
            holder.pb.setProgress((int) progress);
        }else{
            holder.pb.setVisibility(View.GONE);
            if(downloadable.get(position)){
                holder.message.setText("正在排队");
            }else{
                holder.message.setText("已暂停，点击开始下载");
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()){
            onBindViewHolder(holder,position);
        }else{

            String payload= (String) payloads.get(0);

            switch (payload){

                case CHANGE_PROGRESS:
                    if(activity.getDownloadBinder().getDownloadable().get(position)){
                        holder.message.setText(FormatUtil.progressFormat(progress,
                                dataList.get(position).getFileSize()));
                        holder.pb.setProgress((int) progress);
                    }else{
                        holder.pb.setVisibility(View.GONE);
                        holder.message.setText("已暂停，点击开始下载");
                    }
                    break;
                case CHANGE_ITEM:
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.message.setText(FormatUtil.progressFormat(progress,
                            dataList.get(position).getFileSize()));
                    holder.pb.setMax((int) dataList.get(position).getFileSize());
                    holder.pb.setProgress((int) progress);
                    break;
                case CHANGE_STOP_TASK:
                    holder.pb.setVisibility(View.GONE);
                    holder.message.setText("已暂停，点击开始下载");
                    break;
            }

        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView message,musicName;
        ProgressBar pb;
        ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            message=itemView.findViewById(R.id.message);
            pb=itemView.findViewById(R.id.progress_bar);
            delete=itemView.findViewById(R.id.delete);
            musicName=itemView.findViewById(R.id.music_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(activity.getDownloadBinder().getDownloadable().get(getAdapterPosition())){
                        activity.getDownloadBinder().stopTask(getAdapterPosition());
                    }else{
                        activity.getDownloadBinder().addTask(
                                dataList.get(getAdapterPosition()));
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.getDownloadBinder().deleteTask(getAdapterPosition());
                }
            });
        }
    }

    public static class DMDownloadingHandler extends Handler{

        DMDownloadingAdapter adapter;

        DMDownloadingHandler(DMDownloadingAdapter adapter){
            this.adapter=adapter;
        }

        @Override
        public void handleMessage(final Message msg) {

            switch (msg.what){
                case DownloadService.MESSAGE_PROGRESS_UP:
                    adapter.progress= (long) msg.obj;
                    adapter.notifyItemChanged(adapter.activity.getDownloadBinder().getPosition(),CHANGE_PROGRESS);
                    break;
                case DownloadService.MESSAGE_START_TASK:
                    adapter.progress=0;
                    adapter.notifyItemChanged(msg.arg1,CHANGE_ITEM);
                    break;
                case DownloadService.MESSAGE_STOP_DOWNLOAD:
                    adapter.notifyItemChanged(msg.arg1,CHANGE_STOP_TASK);
                    break;
                case DownloadService.MESSAGE_ADD_TO_TASK:
                    adapter.notifyDataSetChanged();
                    break;
            }

        }
    }
}
