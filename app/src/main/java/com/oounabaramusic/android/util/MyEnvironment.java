package com.oounabaramusic.android.util;

import android.os.Environment;

public class MyEnvironment {

    private static String basePath= Environment.getExternalStorageDirectory()+"/OounabaraMusic";

    private static String baseServerPath="http://192.168.1.8:8080/OounabaraMusic/";

    public static String getBasePath(){
        return basePath;
    }
}
