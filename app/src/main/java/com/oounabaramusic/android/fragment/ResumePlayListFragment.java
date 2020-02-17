package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.ResumePlayListAdapter;
import com.oounabaramusic.android.bean.PlayList;
import com.oounabaramusic.android.code.BasicCode;
import com.oounabaramusic.android.okhttputil.S2SHttpUtil;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ResumePlayListFragment extends BaseFragment implements View.OnClickListener{

    private BaseActivity activity;
    private View rootView;
    private ResumePlayListAdapter adapter;

    private TextView delete,resume;

    public ResumePlayListFragment(BaseActivity activity){
        this.activity=activity;
        setTitle("歌单");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView=LayoutInflater.from(activity).inflate(R.layout.fragment_resume_item,container,false);
            init(rootView);
        }
        return rootView;
    }

    private void init(View view) {
        RecyclerView rv=view.findViewById(R.id.recycler_view);
        rv.setAdapter(adapter=new ResumePlayListAdapter(activity));
        rv.setLayoutManager(new LinearLayoutManager(activity));


        delete=view.findViewById(R.id.delete);
        resume=view.findViewById(R.id.resume);

        delete.setOnClickListener(this);
        resume.setOnClickListener(this);

        Drawable deleteIcon=getResources().getDrawable(R.mipmap.delete);
        deleteIcon.setBounds(0,0, DensityUtil.dip2px(activity,30),DensityUtil.dip2px(activity,30));
        delete.setCompoundDrawables(null,deleteIcon,null,null);

        Drawable resumeIcon=getResources().getDrawable(R.mipmap.resume);
        resumeIcon.setBounds(0,0, DensityUtil.dip2px(activity,30),DensityUtil.dip2px(activity,30));
        resume.setCompoundDrawables(null,resumeIcon,null,null);

        initContent();
    }

    private void initContent(){
        int userId= Integer.valueOf(activity.sp.getString("userId","-1"));
        new S2SHttpUtil(activity,activity.gson.toJson(userId),
                MyEnvironment.serverBasePath+"getDeletedPlayList",new MyHandler(this))
                .call(BasicCode.GET_DELETE_PLAY_LIST_END);
    }

    @Override
    public void onClick(View v) {
        if(adapter.getSelected().size()==0){
            Toast.makeText(activity, "请先选择", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (v.getId()){
            case R.id.delete:
                List<Integer> data=adapter.getSelected();
                new S2SHttpUtil(activity,activity.gson.toJson(data),
                        MyEnvironment.serverBasePath+"completelyDeletePlayList",
                        new MyHandler(this))
                .call(BasicCode.DELETE_PLAY_LIST_END);
                break;
            case R.id.resume:
                data=adapter.getSelected();
                new S2SHttpUtil(activity,activity.gson.toJson(data),
                        MyEnvironment.serverBasePath+"resumePlayList",
                        new MyHandler(this))
                        .call(BasicCode.RESUME_PLAY_LIST_END);
                break;
        }
    }

    static class MyHandler extends Handler{
        ResumePlayListFragment fragment;
        MyHandler(ResumePlayListFragment fragment){
            this.fragment=fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BasicCode.GET_DELETE_PLAY_LIST_END:
                    List<PlayList> data= new Gson().fromJson((String)msg.obj,
                            new TypeToken<List<PlayList>>(){}.getType());
                    fragment.adapter.setDataList(data);
                    break;

                case BasicCode.DELETE_PLAY_LIST_END:
                    Toast.makeText(fragment.getActivity(), "完全删除成功", Toast.LENGTH_SHORT).show();
                    fragment.activity.finish();
                    break;

                case BasicCode.RESUME_PLAY_LIST_END:
                    Toast.makeText(fragment.getActivity(), "恢复成功", Toast.LENGTH_SHORT).show();
                    fragment.activity.finish();
                    break;
            }
        }
    }
}
