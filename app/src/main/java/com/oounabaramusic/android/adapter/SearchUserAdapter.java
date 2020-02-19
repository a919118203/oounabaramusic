package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.SearchActivity;
import com.oounabaramusic.android.UserInfoActivity;
import com.oounabaramusic.android.bean.User;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.MyEnvironment;
import com.oounabaramusic.android.util.SharedPreferencesUtil;
import com.oounabaramusic.android.widget.customview.MyCircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder> {

    private static final String CHANGE_FOLLOW="a";
    private SearchActivity activity;
    private List<User> dataList;

    private boolean end;
    private SwipeRefreshLayout srl;
    private int selectPosition;
    private boolean following;

    public SearchUserAdapter(SearchActivity activity,SwipeRefreshLayout srl){
        this.activity=activity;
        this.srl=srl;
        dataList=new ArrayList<>();
    }

    private void setDataList(List<User> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void addDataList(List<User> dataList){
        if(dataList==null||dataList.isEmpty()){
            return;
        }
        int start = this.dataList.size();
        int len = dataList.size();
        this.dataList.addAll(dataList);
        notifyItemRangeInserted(start,len);
    }

    public void getContent(){
        srl.setRefreshing(true);

        Map<String,String> data = new HashMap<>();
        data.put("searchText",activity.getSearchText());
        data.put("start","0");
        data.put("mainUserId", SharedPreferencesUtil.getUserId(activity.sp)+"");

        new S2SHttpUtil(
                activity,
                new Gson().toJson(data),
                MyEnvironment.serverBasePath+"searchUser",
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT);
    }

    public void getNextContent(){
        if(srl.isRefreshing()||end){
            return;
        }

        srl.setRefreshing(true);

        Map<String,String> data = new HashMap<>();
        data.put("searchText",activity.getSearchText());
        data.put("start",dataList.size()+"");
        data.put("mainUserId", SharedPreferencesUtil.getUserId(activity.sp)+"");

        new S2SHttpUtil(
                activity,
                new Gson().toJson(data),
                MyEnvironment.serverBasePath+"searchUser",
                new MyHandler(this))
                .call(BasicCode.GET_CONTENT_2);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.rv_item_search_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User item = dataList.get(position);

        holder.cover.setImageUrl(MyEnvironment.serverBasePath+
                "loadUserHeader?userId="+item.getId());
        holder.introduction.setText(item.getIntroduction());
        holder.name.setText(item.getUserName());
        if(item.getFollowed()){
            holder.toFollow.setVisibility(View.GONE);
            holder.followed.setVisibility(View.VISIBLE);
        }else{
            holder.toFollow.setVisibility(View.VISIBLE);
            holder.followed.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()){
            onBindViewHolder(holder,position);
        }else{
            String option = (String) payloads.get(0);
            switch (option){
                case CHANGE_FOLLOW:
                    holder.toFollow.setVisibility(View.GONE);
                    holder.followed.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyCircleImageView cover;
        TextView name,introduction;
        TextView followed,toFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cover=itemView.findViewById(R.id.user_cover);
            name=itemView.findViewById(R.id.user_name);
            introduction=itemView.findViewById(R.id.introduction);
            followed=itemView.findViewById(R.id.followed);
            toFollow=itemView.findViewById(R.id.to_follow);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserInfoActivity.startActivity(activity,
                            dataList.get(getAdapterPosition()).getId());
                }
            });

            toFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(following){
                        Toast.makeText(activity, "正在关注上一个人，请稍等！", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    following=true;
                    selectPosition=getAdapterPosition();
                    Map<String,Integer> data =new HashMap<>();
                    data.put("from", SharedPreferencesUtil.getUserId(activity.sp));
                    data.put("to",dataList.get(getAdapterPosition()).getId());

                    new S2SHttpUtil(
                            activity,
                            new Gson().toJson(data),
                            MyEnvironment.serverBasePath+"toFollowUser",
                            new MyHandler(SearchUserAdapter.this))
                            .call(BasicCode.TO_FOLLOW_USER);
                }
            });
        }
    }

    static class MyHandler extends Handler {
        SearchUserAdapter adapter;
        MyHandler(SearchUserAdapter adapter){
            this.adapter=adapter;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_CONTENT:
                    List<User> dataList = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<User>>(){}.getType());
                    adapter.end=false;
                    adapter.setDataList(dataList);
                    break;

                case BasicCode.GET_CONTENT_2:
                    dataList = new Gson().fromJson((String) msg.obj,
                            new TypeToken<List<User>>(){}.getType());
                    if(dataList.isEmpty()){
                        adapter.end=true;
                    }
                    adapter.addDataList(dataList);
                    break;

                case BasicCode.TO_FOLLOW_USER:
                    Map<String,Integer> data = new Gson().fromJson((String) msg.obj,
                            new TypeToken<Map<String,Integer>>(){}.getType());
                    int followed = data.get("followed");
                    if(followed>0){
                        adapter.dataList.get(adapter.selectPosition).setFollowed(true);
                        adapter.notifyItemChanged(adapter.selectPosition,CHANGE_FOLLOW);
                    }else{
                        Toast.makeText(adapter.activity, "关注失败，请再试一次", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            adapter.srl.setRefreshing(false);
        }
    }
}
