package com.oounabaramusic.android.dao;

public class SqlSelectString {
    public static final String SELECT_ALL_LOCAL_MUSIC=
            String.format("select * from %s",SqlTableString.LOCAL_MUSIC_TBL);

    public static final String SELECT_ALL_NEED_CHECK=
            String.format("select md5 from %s where is_server = 2",SqlTableString.LOCAL_MUSIC_TBL);

    public static final String SELECT_MUSIC_BY_MD5=
            String.format("select * from %s where md5 = ?",SqlTableString.LOCAL_MUSIC_TBL);

    public static final String SELECT_MUSIC_BY_MUSIC_NAME=
            String.format("select * from %s where music_name like ?",SqlTableString.LOCAL_MUSIC_TBL);
}
