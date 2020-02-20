package com.oounabaramusic.android.util;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.oounabaramusic.android.BaseActivity;
import com.oounabaramusic.android.anim.TextSizeChangeAnimation;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.ViewPager;

/**
 * MainActivity的顶部专用
 */
public class LinksTextViewAndViewPager {
    private List<TextSizeChangeAnimation> animations;
    private int currentPosition=0;
    private BaseActivity activity;
    private ViewPager vp;


    public LinksTextViewAndViewPager(BaseActivity activity, List<TextView> textViews, final ViewPager vp){
        this.activity=activity;
        this.vp=vp;
        animations=new ArrayList<>();
        for(int i=0;i<textViews.size();i++){
            final int finalI = i;
            TextView tv=textViews.get(i);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select(finalI);
                }
            });
            animations.add(new TextSizeChangeAnimation(activity,tv));
        }
    }

    public void select(int position){
        if(position==1&&!SharedPreferencesUtil.isLogin(activity.sp)){
            Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        if(position==currentPosition){
            animations.get(position).toBig();
        }else{
            animations.get(position).toBig();
            animations.get(currentPosition).toSmall();
            currentPosition=position;
        }
        vp.setCurrentItem(position);
    }
}
