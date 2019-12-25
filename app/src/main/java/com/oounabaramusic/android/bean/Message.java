package com.oounabaramusic.android.bean;

import android.graphics.Bitmap;

/**
 * Created by HuangXiaoyang on 2019/11/02.
 */

public class Message {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    private String content;
    private int type;
    private int contentType;
    public Message(String content, int type){
        this.content=content;
        this.type=type;
    }

    public String getContent() {
        return content;
    }


    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "content="+content+",type="+type;
    }
}
