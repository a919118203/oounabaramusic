package com.oounabaramusic.android.util;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.oounabaramusic.android.MainActivity;
import com.oounabaramusic.android.MusicPlayActivity;
import com.oounabaramusic.android.service.DownloadService;
import com.oounabaramusic.android.service.MusicPlayService;

import java.util.ArrayList;
import java.util.List;

public class ActivityManager {
    private static List<Activity> activities=new ArrayList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);

        if(activity instanceof MainActivity){
            activity.startService(new Intent(activity,MusicPlayService.class));
            activity.startService(new Intent(activity, DownloadService.class));
        }
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
        if(activity instanceof MainActivity){
            activity.stopService(new Intent(activity,MusicPlayService.class));
            activity.stopService(new Intent(activity, DownloadService.class));
        }
    }
}
