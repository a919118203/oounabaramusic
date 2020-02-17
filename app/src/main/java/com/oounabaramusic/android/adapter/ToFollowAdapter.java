package com.oounabaramusic.android.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.MessageActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.UserInfoActivity;
import com.oounabaramusic.android.bean.User;
import com.oounabaramusic.android.fragment.ToFollowFragment;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;
import com.oounabaramusic.android.widget.popupwindow.MyBottomSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ToFollowAdapter extends RecyclerView.Adapter<ToFollowAdapter.ViewHolder> {

    private BaseActivity activity;
    private ToFollowFragment fragment;
    private MyBottomSheetDialog pw;
    private List<User> dataList;
    private int popupPosition;

    private Bitmap m,f;

    private TextView pwName;

    public ToFollowAdapter(BaseActivity activity,ToFollowFragment fragment){
        this.activity=activity;
        this.fragment=fragment;
        dataList=new ArrayList<>();
        pw=new MyBottomSheetDialog(activity);
        pw.setContentView(createContentView());

        m= BitmapFactory.decodeResource(activity.getResources(),R.mipmap.sex_m);
        f= BitmapFactory.decodeResource(activity.getResources(),R.mipmap.sex_f);
    }

    public void setDataList(List<User> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    private View createContentView() {
        View view=LayoutInflater.from(activity).inflate(R.layout.pw_friend_menu, (ViewGroup) activity.getWindow().getDecorView(),false);

        view.findViewById(R.id.send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User item = dataList.get(popupPosition);

                MessageActivity.startActivity(activity,item.getId(),item.getUserName());
                pw.dismiss();
            }
        });

        view.findViewById(R.id.cancel_follow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Integer> data = new HashMap<>();
                data.put("from",SharedPreferencesUtil.getUserId(activity.sp));
                data.put("to",dataList.get(popupPosition).getId());

                new S2SHttpUtil(
                        activity,
                        new Gson().toJson(data),
                        MyEnvironment.serverBasePath+"toFollowUser",
                        new MyHandler(ToFollowAdapter.this))
                .call(popupPosition);

                pw.dismiss();
            }
        });

        pwName=view.findViewById(R.id.name);
        return view;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_follow,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User item = dataList.get(position);

        holder.name.setText(item.getUserName());
        holder.introduction.setText(item.getIntroduction());
        if(item.getSex().equals("男")){
            holder.sex.setImageBitmap(m);
        }else{
            holder.sex.setImageBitmap(f);
        }
        holder.header.setImageUrl(MyEnvironment.serverBasePath+
                "loadUserHeader?userId="+item.getId());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyCircleImageView header;
        TextView name,introduction;
        ImageView sex;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity, UserInfoActivity.class);
                    intent.putExtra("userId",
                            dataList.get(getAdapterPosition()).getId());
                    activity.startActivity(intent);
                }
            });

            if(SharedPreferencesUtil.getUserId(activity.sp)==fragment.getUserId()){
                View menu = itemView.findViewById(R.id.friend_menu);
                menu.setVisibility(View.VISIBLE);
                menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupPosition=getAdapterPosition();
                        pwName.setText(dataList.get(popupPosition).getUserName());
                        pw.show();
                    }
                });
            }

            header=itemView.findViewById(R.id.user_header);
            name=itemView.findViewById(R.id.friend_name);
            introduction=itemView.findViewById(R.id.friend_introduction);
            sex=itemView.findViewById(R.id.friend_sex);

        }
    }

    static class MyHandler extends Handler{
        ToFollowAdapter adapter;
        MyHandler(ToFollowAdapter adapter){
            this.adapter=adapter;
        }

        @Override
        public void handleMessage(Message msg) {
            int index = msg.what;
            Map<String,Integer> data = new Gson().fromJson((String)msg.obj,
                    new TypeToken<Map<String,Integer>>(){}.getType());

            int followed = data.get("followed");
            if(followed==0){
                adapter.dataList.remove(index);
                adapter.notifyItemRemoved(index);
            }else{
                Toast.makeText(adapter.activity, "取关失败，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
