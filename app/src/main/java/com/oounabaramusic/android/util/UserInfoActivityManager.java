package com.oounabaramusic.android.util;

import android.app.Activity;

/**
 * 防止无限套娃
 */


public class UserInfoActivityManager {
    private static int top=0;
    private static Activity[] activities=new Activity[2];
    public static void addActivity(Activity activity){
        switch (top){
            case 0:
            case 1:
                activities[top++]=activity;
                break;
            case 2:
                activities[0].finish();
                activities[0]=activities[1];
                activities[1]=activity;
                break;
        }
    }

    public static void removeActivity(Activity activity){
        if(top>=1&&top<=2&&activity.equals(activities[top-1])){
            top--;
        }
    }
}
