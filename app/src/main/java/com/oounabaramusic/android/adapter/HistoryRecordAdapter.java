package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.os.Message;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SearchActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryRecordAdapter extends RecyclerView.Adapter<HistoryRecordAdapter.ViewHolder> {

    private SearchActivity activity;
    private List<String> dataList;

    public HistoryRecordAdapter(SearchActivity activity,List<String> dataList){
        this.activity=activity;
        this.dataList=dataList;
    }

    public void setDataList(List<String> dataList){
        this.dataList=dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_history_record,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv.setText(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv= (TextView) itemView;

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message=new Message();
                    message.what=SearchActivity.MESSAGE_SEARCH;
                    message.obj=dataList.get(getAdapterPosition());
                    activity.getHandler().sendMessage(message);
                }
            });
        }
    }
}
