package com.oounabaramusic.android.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Lrc implements Serializable {
    private List<Long> times;
    private List<String> content;
    private int offset;

    public List<Long> getTimes() {
        return times;
    }

    public void setTimes(List<Long> times) {
        this.times = times;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public static Lrc getSimple(String hint){
        Lrc lrc=new Lrc();
        List<String> a=new ArrayList<>();
        List<Long> b=new ArrayList<>();

        b.add(0L);
        a.add("header");

        b.add((long) Integer.MAX_VALUE);
        a.add(hint);

        b.add((long) Integer.MAX_VALUE);
        a.add("foot");

        lrc.setContent(a);
        lrc.setTimes(b);
        return lrc;
    }
}
