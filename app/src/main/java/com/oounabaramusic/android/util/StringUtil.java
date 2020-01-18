package com.oounabaramusic.android.util;

public class StringUtil {
    public static boolean checkAllNumbers(String in){
        for(int i=0;i<in.length();i++){
            if(in.charAt(i)<'0'||in.charAt(i)>'9')
                return false;
        }
        return true;
    }
}
