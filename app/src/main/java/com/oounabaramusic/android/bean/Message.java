package com.oounabaramusic.android.bean;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable{
    public static final int STRING=0;   //纯文本
    public static final int MUSIC=1;    //分享音乐

    private int messageId;
    private int fromId;
    private String fromUserName;
    private int toId;
    private String toUserName;
    private Date sendDate;
    private String content;

    private int dataType;
    private Music music;

    private int start;
    private int len;
    public int getMessageId() {
        return messageId;
    }
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
    public int getFromId() {
        return fromId;
    }
    public void setFromId(int fromId) {
        this.fromId = fromId;
    }
    public int getToId() {
        return toId;
    }
    public void setToId(int toId) {
        this.toId = toId;
    }
    public Date getSendDate() {
        return sendDate;
    }
    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public int getStart() {
        return start;
    }
    public void setStart(int start) {
        this.start = start;
    }
    public int getLen() {
        return len;
    }
    public void setLen(int len) {
        this.len = len;
    }
    public int getDataType() {
        return dataType;
    }
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }
    public Music getMusic() {
        return music;
    }
    public void setMusic(Music music) {
        this.music = music;
    }
    public String getFromUserName() {
        return fromUserName;
    }
    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }
    public String getToUserName() {
        return toUserName;
    }
    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }
}
