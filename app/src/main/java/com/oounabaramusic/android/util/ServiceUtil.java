package com.oounabaramusic.android.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class ServiceUtil {
    public static boolean ServiceIsRunning(Context context , String serviceName){
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo>  info = activityManager.getRunningServices(Integer.MAX_VALUE);

        for(ActivityManager.RunningServiceInfo i:info){
            if(i.service.getClassName().equals(serviceName)){
                return true;
            }
        }

        return false;
    }
}
