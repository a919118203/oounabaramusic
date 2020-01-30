package com.oounabaramusic.android.util;

import android.os.Environment;

public class MyEnvironment {

    public static String fileBasePath = Environment.getExternalStorageDirectory()+"/OounabaraMusic/";

    public static String cachePath = Environment.getExternalStorageDirectory()+"/OounabaraMusic/cache/";

    public static String serverBasePath ="http://192.168.1.7:8080/OounabaraMusic/";

    public static String databaseName ="oounabaramusic.db";

}
