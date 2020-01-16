package com.oounabaramusic.android.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

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

    public LinksTextViewAndViewPager(Context context, List<TextView> textViews, final ViewPager vp){
        animations=new ArrayList<>();
        for(int i=0;i<textViews.size();i++){
            final int finalI = i;
            TextView tv=textViews.get(i);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select(finalI);
                    vp.setCurrentItem(finalI);
                }
            });
            animations.add(new TextSizeChangeAnimation(context,tv));
        }
        link();
    }

    private void link() {

    }

    public void select(int position){
        if(position==currentPosition){
            animations.get(position).toBig();
        }else{
            animations.get(position).toBig();
            animations.get(currentPosition).toSmall();
            currentPosition=position;
        }
    }
}
