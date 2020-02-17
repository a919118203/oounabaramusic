package com.oounabaramusic.android.util;

public class DateUtil {
    private static int[] leapYear={0,31,29,31,30,31,30,31,31,30,31,30,31};
    private static int[] averageYear={0,31,28,31,30,31,30,31,31,30,31,30,31};

    public static boolean checkDate(int y,int m,int d){
        LogUtil.printLog(y+" "+m+" "+d);
        if(y<0){
            return false;
        }else if(y<5000){
            if(m<1){
                return false;
            }else if(m<13){
                if(y%4==0&&y%100!=0||y%400==0){
                    if(d>0&&d<leapYear[m]){
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    if(d>0&&d<averageYear[m]){
                        return true;
                    }else{
                        return false;
                    }
                }
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public static boolean checkDate(String y,String m,String d){
        return checkDate(Integer.valueOf(y),Integer.valueOf(m),Integer.valueOf(d));
    }
}
