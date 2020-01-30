package com.oounabaramusic.android.dao;

public class SqlDeleteString {

    //本地音乐
    public static final String DELETE_ALL_LOCAL_MUSIC=
            "delete from "+SqlTableString.LOCAL_MUSIC_TBL;

    public static final String DELETE_MUSIC_BY_MD5=
            String.format("delete from %s where md5 = ?",SqlTableString.LOCAL_MUSIC_TBL);


    //历史记录
    public static final String DELETE_HISTORICAL_QUERY=
            String.format("delete from %s",SqlTableString.HISTORICAL_QUERY_TBL);

    public static final String DELETE_BY_SEARCH_TEXT=
            String.format("delete from %s where exists(select * from %s where search_text = ?) and search_text = ?",SqlTableString.HISTORICAL_QUERY_TBL,SqlTableString.HISTORICAL_QUERY_TBL);
}
