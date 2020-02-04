package com.oounabaramusic.android.widget.popupwindow;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SingerActivity;

import androidx.appcompat.app.AlertDialog;

public class SingerDialog {

    private Context context;
    private String[] singerNames,singerIds;
    private ListView lv;
    private AlertDialog dialog;
    private int mySingerId=-1;

    public SingerDialog(Context context,String singerNames,String singerIds,int... mySingerId){
        this.context=context;
        this.singerNames=singerNames.split("/");
        this.singerIds=singerIds.split("/");

        if(mySingerId.length!=0){
           this.mySingerId=mySingerId[0];
        }

        init();
    }

    private void init() {
        lv=new ListView(context);
        lv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        lv.setAdapter(new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                singerNames));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toSingerActivity(Integer.valueOf(singerIds[position]));
                dialog.dismiss();
            }
        });

        dialog=new AlertDialog.Builder(context)
                .setView(lv)
                .create();

        dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.layout_card_2));
        dialog.show();
    }

    /**
     * 前往SingerActivity
     * @param singerId
     */
    private  void toSingerActivity(int singerId){
        if(singerId==mySingerId){
            return ;
        }

        Intent intent=new Intent(context, SingerActivity.class);
        intent.putExtra("singerId",singerId);
        context.startActivity(intent);
    }

}
