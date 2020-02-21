package com.oounabaramusic.android.service;

import android.os.Handler;

public class BaseHandler extends Handler {
    private boolean die;

    public void toDie(){
        die=true;
    }

    public boolean isDie(){
        return die;
    }

    public void resurrection(){
        die=false;
    }
}
