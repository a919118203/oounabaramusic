package com.oounabaramusic.android.util;

import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    public static int getUserId(SharedPreferences sp){
        return Integer.valueOf(sp.getString("userId","-1"));
    }

    public static boolean isLogin(SharedPreferences sp){
        return sp.getBoolean("login",false);
    }
}
