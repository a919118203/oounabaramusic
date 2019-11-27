package com.oounabaramusic.android.util;

import android.util.Log;

public class LogUtil {
    static private boolean _switch=true;
    static public void printLog(String tag,String content){
        if(_switch){
            Log.d(tag, content);
        }
    }
}
