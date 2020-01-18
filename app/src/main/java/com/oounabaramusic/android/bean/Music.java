package com.oounabaramusic.android.bean;


import androidx.annotation.NonNull;

public class Music{

    private int id;
    private String musicName;    //音乐名
    private String singerName;   //歌手名
    private int singerId;        //歌手id
    private String filePath;     //文件路径
    private String coverPath;    //封面路径
    private int  duration;       //时长
    private long  fileSize;       //文件大小
    private String md5;          //判断是不是同一个文件
    private int downloadStatus;  //下载状态     0：已下载完成   1：还没下载  2：正在下载 3:不是下载文件
    private int isServer;        //是否是服务器中的音乐   0：不是 1：是  2：待判定

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

    public int getSingerId() {
        return singerId;
    }

    public void setSingerId(int singerId) {
        this.singerId = singerId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
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
                +filePath+",coverPath = "+coverPath+",duration = "
                +duration+",fileSize = "+fileSize+",md5 = "
                +md5+",downloadStatus = "+downloadStatus+",isServer = "+isServer+"]";
    }
}
