package com.oounabaramusic.android.widget.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.oounabaramusic.android.R;

import java.lang.reflect.Field;

/**
 * 废弃
 */

public class MyPopupWindow extends PopupWindow {

    private Activity activity;

    public MyPopupWindow(Activity activity,View contentView,int gravity){
        super(contentView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        this.activity=activity;
        init();
        setContentView(createContentView(contentView,gravity));
    }

    public MyPopupWindow(Activity activity,View contentView){
        super(contentView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        this.activity=activity;
        init();
        setContentView(createContentView(contentView,Gravity.BOTTOM));
    }

    private View createContentView(View contentView,int gravity) {
        FrameLayout layout=new FrameLayout(activity);
        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutParams);
        layout.setBackgroundColor(activity.getResources().getColor(R.color.alpha_black_background));

        FrameLayout.LayoutParams fllp= (FrameLayout.LayoutParams) contentView.getLayoutParams();
        fllp.gravity=gravity;

        layout.addView(contentView);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPopupWindow.this.dismiss();
            }
        });
        return layout;
    }

    private void init() {
        fillScreen(this);
    }

    public void showPopupWindow(){
        showAtLocation(activity.getWindow().getDecorView(),Gravity.BOTTOM,0,0);
    }

    private void fillScreen(PopupWindow pw){
        try {
            Field field=PopupWindow.class.getDeclaredField("mLayoutInScreen");
            field.setAccessible(true);
            field.set(pw,true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
