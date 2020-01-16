package com.oounabaramusic.android.fragment;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.adapter.ResumePlayListAdapter;
import com.oounabaramusic.android.util.DensityUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ResumePlayListFragment extends BaseFragment {

    private Activity activity;
    private View rootView;
    private ResumePlayListAdapter adapter;

    public ResumePlayListFragment(Activity activity){
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


        TextView delete=view.findViewById(R.id.delete);
        TextView resume=view.findViewById(R.id.resume);


        Drawable deleteIcon=getResources().getDrawable(R.mipmap.delete);
        deleteIcon.setBounds(0,0, DensityUtil.dip2px(activity,30),DensityUtil.dip2px(activity,30));
        delete.setCompoundDrawables(null,deleteIcon,null,null);

        Drawable resumeIcon=getResources().getDrawable(R.mipmap.resume);
        resumeIcon.setBounds(0,0, DensityUtil.dip2px(activity,30),DensityUtil.dip2px(activity,30));
        resume.setCompoundDrawables(null,resumeIcon,null,null);
    }
}
