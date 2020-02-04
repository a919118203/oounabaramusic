package com.oounabaramusic.android.widget.popupwindow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.Music;

import java.util.List;

public class DownloadDialog {

    private AlertDialog dialog;
    private BaseActivity context;
    private List<Music> dataList;

    public DownloadDialog(BaseActivity context, List<Music> dataList){
        this.context=context;
        this.dataList=dataList;

        initDialog();
    }

    private void initDialog() {
        dialog = new AlertDialog.Builder(context)
                .setTitle("确定要下载这"+dataList.size()+"首歌吗？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(Music item:dataList){
                            context.getDownloadBinder().addTask(item);
                        }
                    }
                })
                .create();

        dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.layout_card_2));
        dialog.show();
    }
}
