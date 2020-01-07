package com.oounabaramusic.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.oounabaramusic.android.DownloadManagementActivity;
import com.oounabaramusic.android.R;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DMMusicChoiceAdapter extends RecyclerView.Adapter<DMMusicChoiceAdapter.ViewHolder> {

    private DownloadManagementActivity activity;
    private boolean[] selected;
    private int selectedCnt;

    public DMMusicChoiceAdapter(DownloadManagementActivity activity){
        this.activity=activity;
        selected=new boolean[1000];
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_dm_music,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.cb.setChecked(selected[position]);
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public void clearSelected(){
        selected=new boolean[1000];
        selectedCnt=0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CheckBox cb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cb.setChecked(!cb.isChecked());
                }
            });

            cb=itemView.findViewById(R.id.checkbox);
            cb.setVisibility(View.VISIBLE);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(selected[getAdapterPosition()]==isChecked)
                        return;

                    selected[getAdapterPosition()]=isChecked;
                    if(isChecked){
                        selectedCnt++;
                    }else{
                        selectedCnt--;
                    }
                    Objects.requireNonNull(activity.getSupportActionBar()).setTitle("已选择"+selectedCnt+"项");
                }
            });
            itemView.findViewById(R.id.menu).setVisibility(View.GONE);
        }
    }
}
