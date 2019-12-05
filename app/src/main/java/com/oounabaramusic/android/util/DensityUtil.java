package com.oounabaramusic.android.util;


import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;

public class DensityUtil {
    /**
     * dp 转成px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转成dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     * @param activity  活动
     * @return          屏幕宽度
     */
    public static int getDisplayWidth(Activity activity){
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        return point.x;
    }

    /**
     * 获取屏幕高度
     * @param activity  活动
     * @return          屏幕高度
     */
    public static int getDisplayHeight(Activity activity){
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        return point.y;
    }
}