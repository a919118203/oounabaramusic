package com.oounabaramusic.android.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class LogUtil {
    static private boolean _switch=false;
    static public void printLog(String content){
        if(_switch){
            Log.d("Mogeko", content);
        }
    }

    static public void printLog(String content, Context context){
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

}
