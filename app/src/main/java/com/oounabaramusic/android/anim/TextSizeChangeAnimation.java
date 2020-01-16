package com.oounabaramusic.android.anim;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.TextView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.util.LogUtil;

/**
 * 字体和风格变大变小的动画
 */
public class TextSizeChangeAnimation {

    private TextView tv;
    private Context context;
    private ObjectAnimator toBig,toSmall;
    private float big,small;
    private int selectColor,unSelectColor;

    public TextSizeChangeAnimation(Context context,TextView tv){
        this.tv=tv;
        this.context=context;

        big=DensityUtil.px2sp(context,context.getResources().getDimension(R.dimen.font_big_size));
        small=DensityUtil.px2sp(context,context.getResources().getDimension(R.dimen.font_medium_size));
        selectColor=context.getResources().getColor(R.color.positive);
        unSelectColor=context.getResources().getColor(R.color.top_tool_bar_un_select);
        initAnimation();
    }

    private void initAnimation() {
        toBig= ObjectAnimator.ofFloat(tv,"textSize",small,big);
        toBig.setDuration(250);

        toSmall= ObjectAnimator.ofFloat(tv,"textSize",big,small);
        toSmall.setDuration(250);
    }


    public void toBig(){
        tv.setTextColor(selectColor);
        tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        toBig.start();
    }

    public void toSmall(){
        tv.setTextColor(unSelectColor);
        tv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        toSmall.start();
    }
}
