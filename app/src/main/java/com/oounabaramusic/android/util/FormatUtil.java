package com.oounabaramusic.android.util;

public class FormatUtil {
    public static String secondToString(int s){
        int minute=s/60;
        int second=s%60;

        StringBuilder result=new StringBuilder();
        if(minute<10){
            result.append("0");
            result.append(minute);
        }else{
            result.append(minute);
        }
        result.append(":");
        result.append(second);
        return result.toString();
    }
}
