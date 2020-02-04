package com.oounabaramusic.android.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.oounabaramusic.android.DownloadManagementActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Music;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DMMusicChoiceAdapter extends RecyclerView.Adapter<DMMusicChoiceAdapter.ViewHolder> {

    private DownloadManagementActivity activity;
    private boolean[] selected;
    private int selectedCnt;
    private List<Music> dataList;

    public DMMusicChoiceAdapter(DownloadManagementActivity activity){
        this.activity=activity;
        dataList=activity.getLocalMusicDao().selectAllDownloadEnd();
        selected=new boolean[dataList.size()];
        selectedCnt=0;
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
        holder.musicName.setText(dataList.get(position).getMusicName());
        holder.musicSinger.setText(dataList.get(position).getSingerName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void clearSelected(){
        selected=new boolean[dataList.size()];
        selectedCnt=0;
    }

    public void selectAll(){
        for(int i=0;i<selected.length;i++){
            selected[i]=true;
        }
        selectedCnt=selected.length;
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("已选择"+selectedCnt+"项");
        activity.setSelectAllVisible(false);
        activity.setCancelSelectAll(true);
        notifyDataSetChanged();
    }

    public void cancelSelectAll(){
        selected=new boolean[dataList.size()];
        selectedCnt=0;
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("已选择"+selectedCnt+"项");
        activity.setSelectAllVisible(true);
        activity.setCancelSelectAll(false);
        notifyDataSetChanged();
    }

    public List<Music> getSelectedData(){
        List<Music> result=new ArrayList<>();
        for(int i=0;i<selected.length;i++){
            if(selected[i]){
                result.add(dataList.get(i));
            }
        }
        return result;
    }

    public void refresh(){
        dataList=activity.getLocalMusicDao().selectAllDownloadEnd();
        selected=new boolean[dataList.size()];
        selectedCnt=0;
        notifyDataSetChanged();
    }

    private AlertDialog dialog;
    private CheckBox cb;
    public void showDeleteMusicDialog(final List<Music> items){
        if(dialog==null){
            View view=LayoutInflater.from(activity).inflate(
                    R.layout.alertdialog_delete_local_music,
                    (ViewGroup) activity.getWindow().getDecorView(),false);
            cb=view.findViewById(R.id.checkbox);


            dialog=new AlertDialog.Builder(activity)
                    .setTitle("确定删除音乐")
                    .setView(view)
                    .setNegativeButton("取消",null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for(Music item:items){
                                activity.getLocalMusicDao().deleteMusicByMd5(item.getMd5());

                                File file=new File(item.getFilePath());

                                if(cb.isChecked()){
                                    if(file.exists()){
                                        file.delete();
                                    }
                                }
                            }

                            refresh();
                        }
                    })
                    .create();

            dialog.getWindow().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.layout_card_2));
        }

        dialog.show();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CheckBox cb;
        TextView musicName,musicSinger;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cb.setChecked(!cb.isChecked());
                }
            });

            cb=itemView.findViewById(R.id.checkbox);
            musicName=itemView.findViewById(R.id.music_name);
            musicSinger=itemView.findViewById(R.id.music_singer);
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

                    if(selectedCnt==dataList.size()){
                        activity.setCancelSelectAll(true);
                        activity.setSelectAllVisible(false);
                    }else{
                        activity.setCancelSelectAll(false);
                        activity.setSelectAllVisible(true);
                    }
                    Objects.requireNonNull(activity.getSupportActionBar()).setTitle("已选择"+selectedCnt+"项");
                }
            });
            itemView.findViewById(R.id.menu).setVisibility(View.GONE);
        }
    }
}
