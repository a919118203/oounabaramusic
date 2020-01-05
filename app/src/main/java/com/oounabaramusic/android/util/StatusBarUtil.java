package com.oounabaramusic.android.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import com.oounabaramusic.android.R;

import java.lang.reflect.Method;

import androidx.annotation.RequiresApi;

/**
 * View.SYSTEM_UI_FLAG_LAYOUT_STABLE        不管状态栏的有无，activity都会空出状态栏的位置
 *
 * View.SYSTEM_UI_FLAG_FULLSCREEN           activity全屏显示，状态栏也显示，但是activity覆盖状态栏
 *
 * View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN    activity全屏显示，状态栏也显示，但是状态栏覆盖activity
 *
 * View.SYSTEM_UI_FLAG_HIDE_NAVIGATION      导航栏不显示，布局延伸到导航栏
 *
 * View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION    导航栏显示，布局延伸到导航栏
 */
public class StatusBarUtil {
    private boolean hasNavigationBar=false;

    /**
     * 设置状态栏为透明
     * @param activity
     */
    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     *
     * 将状态栏的内容设为深色&设置状态栏为透明
     * @param activity
     */
    public static void changeStatusBarContentColor(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|//设为淡色主题好让透明色的状态栏中的内容显示出来
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);//让activity占据全屏且显示状态栏，为了显示popupWindow的时候添加的一个View遮住全屏
            window.setStatusBarColor(Color.TRANSPARENT);//设置为透明色
        }
    }

    /**
     * 隐藏状态栏
     * @param f
     * @param activity
     */

    public static void hiddenStatusBar(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }


    /**
     * 利用反射获取状态栏高度
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 向下移动状态栏个长度(id必须为appBar)
     */
    public  static void moveDownStatusBar(final Activity activity){
        final View view=activity.findViewById(R.id.appbar);
        if(view!=null){
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {  //视图将要绘制时会调用这个监听事件，
                @Override
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);//会多次调用，没有特殊要求只需使用一次来获取视图的高度，所以remove
                    ViewGroup.LayoutParams params=view.getLayoutParams();
                    params.height=view.getHeight()+getStatusBarHeight(activity);
                    view.setPadding(
                            view.getPaddingLeft(),
                            view.getPaddingTop()+StatusBarUtil.getStatusBarHeight(activity),
                            view.getPaddingRight(),
                            view.getPaddingBottom());

                    ProgressBar pb;

                    return true;
                }
            });
        }
    }

    public  static void moveDownStatusBar(final Activity activity,int id){
        final View view=activity.findViewById(id);
        if(view!=null){
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {  //视图将要绘制时会调用这个监听事件，
                @Override
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);//会多次调用，没有特殊要求只需使用一次来获取视图的高度，所以remove
                    ViewGroup.LayoutParams params=view.getLayoutParams();
                    params.height=view.getHeight()+getStatusBarHeight(activity);
                    view.setPadding(
                            view.getPaddingLeft(),
                            view.getPaddingTop()+StatusBarUtil.getStatusBarHeight(activity),
                            view.getPaddingRight(),
                            view.getPaddingBottom());

                    ProgressBar pb;

                    return true;
                }
            });
        }
    }

    /**
     * 关闭输入法
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if(view!=null){
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void setSystemUiVisibility(Activity activity,int f){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            window.getDecorView().setSystemUiVisibility(f);
        }
    }


    /**
     * 获取是否存在NavigationBar
     * @param context
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNavigationBar;
    }
    /**
     * 获取虚拟功能键高度
     * @param context
     * @return
     */
    public static int getVirtualBarHeigh(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }
}
