package com.oounabaramusic.android.util;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;

public class InputMethodUtil {


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

    public static void showSoftKeyboard(Activity activity){
        View view=activity.getWindow().peekDecorView();
        if(view!=null){
            InputMethodManager imm= (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);
        }
    }

    public static boolean isClickEditText(@NonNull View v, MotionEvent me){
        if(v instanceof EditText){
            int[] position=new int[2];
            v.getLocationInWindow(position);

            int x=v.getWidth(),
                y=v.getHeight();

            return me.getX() > position[0] && me.getX() < x && me.getY() > position[1] && me.getY() < y;
        }
        return false;
    }
}
