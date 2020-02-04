package com.oounabaramusic.android.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtil {
    public static String secondToString(int s){
        if(s==Integer.MAX_VALUE)
            return "00:00";

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
        if(second<10){
            result.append("0");
            result.append(second);
        }else{
            result.append(second);
        }
        return result.toString();
    }

    public static String secondToString(long s){
        return secondToString((int)s);
    }

    public static String numberToString(int num){
        if(num>10000){
            float f=(float) num/10000f;
            return f+"万";
        }
        return String.valueOf(num);
    }

    public static String progressFormat(long progress,long max){
        StringBuilder sb=new StringBuilder();
        sb.append(fileSizeFormat(progress));
        sb.append("/");
        sb.append(fileSizeFormat(max));
        return sb.toString();
    }

    private static final long KB= 1024L;
    private static final long MB= 1024L * 1024L;
    public static String fileSizeFormat(long fileSize){
        double result=fileSize;
        StringBuilder sb=new StringBuilder();
        if(fileSize>MB){
            result=(double)fileSize/(double) MB;
            sb.append(String.format("%.2f",result));
            sb.append("M");
        }else if(fileSize>KB){
            result=(double)fileSize/(double) KB;
            sb.append(String.format("%.2f",result));
            sb.append("KB");
        }else{
            sb.append(String.format("%.2f",result));
            sb.append("byte");
        }
        return sb.toString();
    }

    public static String DateToString(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}
