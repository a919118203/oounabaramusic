package com.oounabaramusic.android.anim;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.oounabaramusic.android.R;

public class OpenListAnimation {
    public static final int STATUS_OPEN=1;
    public static final int STATUS_CLOSE=2;
    private Animation openListAnim,closeListAnim;
    private View view;
    private int status=STATUS_CLOSE;

    public OpenListAnimation(View view, Context context){
        this.view=view;
        openListAnim= AnimationUtils.loadAnimation(context, R.anim.anim_list_open);
        closeListAnim=AnimationUtils.loadAnimation(context,R.anim.anim_list_close);
    }

    public int getStatus() {
        return status;
    }

    public void changeStatus(){
        if(status==STATUS_OPEN){
            status=STATUS_CLOSE;
            view.startAnimation(closeListAnim);
        }else if(status==STATUS_CLOSE){
            status=STATUS_OPEN;
            view.startAnimation(openListAnim);
        }
    }
}
