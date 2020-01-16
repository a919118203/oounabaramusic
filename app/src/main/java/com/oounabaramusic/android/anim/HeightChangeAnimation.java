package com.oounabaramusic.android.anim;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;

public class HeightChangeAnimation {
    private View v1,v2;
    private ValueAnimator toV1,toV2;
    private int v1h,v2h;

    public HeightChangeAnimation(final View v1, final View v2){
        this.v1=v1;
        v1.post(new Runnable() {
            @Override
            public void run() {
                setV1h(v1.getMeasuredHeight());
            }
        });

        this.v2=v2;

        v2.post(new Runnable() {
            @Override
            public void run() {
                setV2h(v2.getMeasuredHeight());
                v2.setVisibility(View.GONE);
            }
        });
    }

    private void setV1h(int v) {
        this.v1h = v;
        if(v1h!=0&&v2h!=0){
            initAnimation();
        }
    }

    private void setV2h(int v) {
        this.v2h = v;
        if(v1h!=0&&v2h!=0){
            initAnimation();
        }
    }

    private void setHeight(View view,int height){
        view.getLayoutParams().height=height;
        view.requestLayout();
    }

    private void initAnimation() {
        toV1= ValueAnimator.ofInt(v2h,v1h);
        toV1.setDuration(250);
        toV1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setHeight(v1, (Integer) animation.getAnimatedValue());
            }
        });
        toV1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                v1.setVisibility(View.VISIBLE);
                v2.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        toV2= ValueAnimator.ofInt(v1h,v2h);
        toV2.setDuration(250);
        toV2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setHeight(v1, (Integer) animation.getAnimatedValue());
            }
        });

        toV2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                v1.setVisibility(View.GONE);
                v2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    public void toV1(){
        toV1.start();
    }

    public void toV2(){
        toV2.start();
    }
}
