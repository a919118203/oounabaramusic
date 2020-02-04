package com.oounabaramusic.android.dao;

public class SqlSelectString {

    //本地音乐
    public static final String SELECT_ALL_LOCAL_MUSIC=
            String.format("select * from %s where download_status = 0 or download_status = 1",SqlTableString.LOCAL_MUSIC_TBL);

    public static final String SELECT_ALL_NEED_CHECK=
            String.format("select md5 from %s where is_server = 2",SqlTableString.LOCAL_MUSIC_TBL);

    public static final String SELECT_MUSIC_BY_MD5=
            String.format("select * from %s where md5 = ?",SqlTableString.LOCAL_MUSIC_TBL);

    public static final String SELECT_MUSIC_BY_MUSIC_NAME=
            String.format("select * from %s where music_name like ?",SqlTableString.LOCAL_MUSIC_TBL);

    public static final String SELECT_IS_SERVER=
            String.format("select is_server from %s where md5 = ?",SqlTableString.LOCAL_MUSIC_TBL);

    public static final String SELECT_ALL_NEED_DOWNLOAD=
            String.format("select * from %s where download_status = 2",SqlTableString.LOCAL_MUSIC_TBL);

    public static final String SELECT_ALL_DOWNLOAD_END=
            String.format("select * from %s where download_status = 0",SqlTableString.LOCAL_MUSIC_TBL);



    //历史查询
    public static final String SELECT_HISTORICAL_QUERY=
            String.format("select * from %s order by id desc limit ?",SqlTableString.HISTORICAL_QUERY_TBL);


}
