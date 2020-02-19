package com.oounabaramusic.android.widget.popupwindow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.bean.MyImage;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.PlayListHttpUtil;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.widget.customview.MyImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PlayListDialog {
    private int[] musicId;
    private String[] musicMd5;
    private AlertDialog dialog;
    private Context context;
    private List<PlayList> dataList;
    private DialogInterface.OnDismissListener listener;

    public PlayListDialog(Context context,int userId,int... musicId){
        this.context=context;
        this.musicId=musicId;
        this.musicMd5=null;
        getPlayList(userId);
    }

    public PlayListDialog(Context context,int userId,String... musicMd5){
        this.context=context;
        this.musicMd5=musicMd5;
        this.musicId=null;
        getPlayList(userId);
    }

    public PlayListDialog(DialogInterface.OnDismissListener listener , Context context,int userId,int... musicId){
        this.listener=listener;
        this.context=context;
        this.musicId=musicId;
        this.musicMd5=null;
        getPlayList(userId);
    }

    public PlayListDialog(DialogInterface.OnDismissListener listener ,Context context,int userId,String... musicMd5){
        this.listener=listener;
        this.context=context;
        this.musicMd5=musicMd5;
        this.musicId=null;
        getPlayList(userId);
    }

    private void getPlayList(int userId){
        Map<String,Integer> data = new HashMap<>();
        data.put("userId",userId);
        data.put("start",-1);

        new S2SHttpUtil(
                context,
                new Gson().toJson(data),
                MyEnvironment.serverBasePath+"findPlayListByUser",
                new MHandler(this))
                .call(BasicCode.GET_CONTENT);
    }

    private void initDialog() {
        if(dataList.size()==0){
            Toast.makeText(context, "请先创建歌单", Toast.LENGTH_SHORT).show();
            return;
        }

        RecyclerView rv=new RecyclerView(context);

        rv.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {


            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(context).inflate(R.layout.rv_item_play_list,parent,false);
                return new MyViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                holder.playListCnt.setText(dataList.get(position).getCnt()+"");
                holder.playListName.setText(dataList.get(position).getPlayListName());
                holder.playListCover.setImage(new MyImage(
                        MyImage.TYPE_PLAY_LIST_COVER,
                        dataList.get(position).getId()));
            }

            @Override
            public int getItemCount() {
                return dataList.size();
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(context));

        dialog=new AlertDialog.Builder(context)
                .setTitle("选择歌单")
                .setView(rv)
                .create();

        if(listener!=null){
            dialog.setOnDismissListener(listener);
        }

        dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.layout_card_2));
        dialog.show();

        //必须在show之后更改大小
        Window window=dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height=DensityUtil.dip2px(context,400);
        window.setAttributes(lp);
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        MyImageView playListCover;
        TextView playListName,playListCnt;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.findViewById(R.id.my_playlist_item_menu).setVisibility(View.GONE);
            playListCover=itemView.findViewById(R.id.playlist_cover);
            playListName=itemView.findViewById(R.id.playlist_name);
            playListCnt=itemView.findViewById(R.id.playlist_cnt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> data=new ArrayList<>();
                    data.add(dataList.get(getAdapterPosition()).getId()+"");

                    if(musicId!=null){
                        data.add("musicIdList");
                        for(int a:musicId){
                            data.add(a+"");
                        }
                    }

                    if(musicMd5!=null){
                        data.add("musicMd5List");
                        data.addAll(Arrays.asList(musicMd5));
                    }

                    LogUtil.printLog("json:  "+new Gson().toJson(data));

                    PlayListHttpUtil.addToPlayList(context,new Gson().toJson(data),new Handler());//可以加 添加失败的代码
                    dialog.dismiss();
                }
            });
        }
    }

    static class MHandler extends Handler{
        PlayListDialog dialog;
        MHandler(PlayListDialog dialog){
            this.dialog=dialog;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:

                    Map<String,String> data = new Gson().fromJson((String)msg.obj,
                            new TypeToken<Map<String,String>>(){}.getType());

                    dialog.dataList= new Gson().fromJson(data.get("playLists"),
                            new TypeToken<List<PlayList>>(){}.getType());
                    PlayList myLove = dialog.dataList.remove(dialog.dataList.size()-1);
                    dialog.dataList.add(0,myLove);

                    dialog.initDialog();
                    break;
            }
        }
    }
}
