package com.oounabaramusic.android.util;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.oounabaramusic.android.MainActivity;
import com.oounabaramusic.android.service.MusicPlayService;

import java.util.ArrayList;
import java.util.List;

public class ActivityManager {
    private static List<Activity> activities=new ArrayList<>();
    private static int cnt=0;

    public static void addActivity(Activity activity){
        activities.add(activity);
        if(cnt==0){
            Intent intent=new Intent(activity, MusicPlayService.class);
            activity.startService(intent);
            cnt++;
        }
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
        if(activity instanceof MainActivity){
            Intent intent=new Intent(activity, MusicPlayService.class);
            activity.stopService(intent);
        }
    }
}
