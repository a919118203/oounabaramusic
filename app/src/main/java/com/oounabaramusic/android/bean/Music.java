package com.oounabaramusic.android.bean;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oounabaramusic.android.util.LogUtil;
import com.oounabaramusic.android.util.MyEnvironment;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Music implements Serializable {

    private int id;
    private String musicName;    //音乐名
    private String singerName;   //歌手名
    private String singerId;     //歌手id
    private String filePath;     //文件路径
    private int  duration;       //时长
    private long  fileSize;      //文件大小
    private String md5;          //判断是不是同一个文件
    private int downloadStatus;  //下载状态     0：已下载完成   1：还没下载  2：正在下载 3:不是下载文件
    private int isServer;        //音乐类型     0 本地音乐 1 服务器音乐 2:待判定

    public Music(){}

    //服务器存的结构和手机的不一样，转换一下
    public Music(String serverJson){
        Gson gson=new Gson();

        Map<String,String> jsonData=
                gson.fromJson(serverJson,new TypeToken<Map<String,String>>(){}.getType());

        List<Map<String,String>> singers=
                gson.fromJson(jsonData.get("singers"),new TypeToken<List<Map<String,String>>>(){}.getType());

        StringBuilder singerName=new StringBuilder();
        StringBuilder singerId=new StringBuilder();

        for(int i=0;i<singers.size();i++){
            if(i>0){
                singerName.append("/");
                singerId.append("/");
            }
            singerName.append(singers.get(i).get("singerName"));
            singerId.append(singers.get(i).get("id"));
        }

        setId(Integer.valueOf(Objects.requireNonNull(jsonData.get("id"))));
        setMd5(jsonData.get("md5"));
        setMusicName(jsonData.get("musicName"));
        setSingerName(singerName.toString());
        setSingerId(singerId.toString());
        setDownloadStatus(3);          //不是下载文件 ，http式的地址
        setIsServer(1);
        setFilePath(MyEnvironment.serverBasePath+"music/"+jsonData.get("fileName"));
        setFileSize(Long.valueOf(jsonData.get("fileSize")));

        LogUtil.printLog(getFileSize()+"");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getSingerId() {
        return singerId;
    }

    public void setSingerId(String singerId) {
        this.singerId = singerId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getIsServer() {
        return isServer;
    }

    public void setIsServer(int isServer) {
        this.isServer = isServer;
    }

    @NonNull
    @Override
    public String toString() {
        return "[id = "+id+",musicName = "+musicName+",singerName = "
                +singerName+",singerId = "+singerId+",filePath = "
                +filePath+",duration = "
                +duration+",fileSize = "+fileSize+",md5 = "
                +md5+",downloadStatus = "+downloadStatus+",isServer = "+isServer+"]";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Music){
            return ((Music)obj).getMd5().equals(md5);
        }

        return super.equals(obj);
    }

    public Music cloneItem() {
        Music clone=new Music();
        clone.setId(id);
        clone.setFilePath(filePath);
        clone.setDownloadStatus(downloadStatus);
        clone.setMusicName(musicName);
        clone.setIsServer(isServer);
        clone.setMd5(md5);
        clone.setSingerId(singerId);
        clone.setSingerName(singerName);
        clone.setDuration(duration);
        clone.setFileSize(fileSize);
        return clone;
    }
}
