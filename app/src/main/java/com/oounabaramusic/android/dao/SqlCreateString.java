package com.oounabaramusic.android.dao;

public class SqlCreateString {
    public static final String CREATE_LOCAL_MUSIC_TBL=
            "create table local_music_tbl(" +
            "id integer primary key autoincrement," +
            "music_name text,"                      + //音乐名
            "singer_name text,"                      + //歌手名
            "singer_id integer,"                     + //歌手id
            "file_path text,"                        + //文件路径
            "cover_path text,"                       + //封面路径
            "duration integer,"                     + //时长
            "file_size integer,"                     + //文件大小
            "md5 text,"                             + //判断是不是同一个文件  当id来用
            "download_status integer,"               + //下载状态     0：已下载完成   1：还没下载  2：正在下载\n" +
            "is_server integer)";                      //是否是服务器中的音乐   0：不是 1：是  2：待判定
}
