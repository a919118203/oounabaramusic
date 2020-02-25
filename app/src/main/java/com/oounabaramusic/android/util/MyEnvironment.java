package com.oounabaramusic.android.util;

import android.os.Environment;

public class MyEnvironment {

    public static String fileBasePath = Environment.getExternalStorageDirectory()+"/OounabaraMusic/";

    public static String cachePath = Environment.getExternalStorageDirectory()+"/OounabaraMusic/cache/";

    public static String musicPath= Environment.getExternalStorageDirectory()+"/OounabaraMusic/music/";

    public static String musicLrc= Environment.getExternalStorageDirectory()+"/OounabaraMusic/lrc/";

    public static String serverBasePath ="http://192.168.1.6:8080/OounabaraMusic/";

    public static String databaseName ="oounabaramusic.db";

}
