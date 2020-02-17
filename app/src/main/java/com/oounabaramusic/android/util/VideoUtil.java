package com.oounabaramusic.android.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import java.util.HashMap;

public class VideoUtil {

    public static Bitmap getVideoCover(String path){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        if(path.contains("http")){
            mmr.setDataSource(path,new HashMap<String, String>());
        }else{
            mmr.setDataSource(path);
        }
        return  mmr.getFrameAtTime();
    }

    public static int getVideoLen(String path){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        if(path.contains("http")){
            mmr.setDataSource(path,new HashMap<String, String>());
        }else{
            mmr.setDataSource(path);
        }
        return  Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
    }
}
