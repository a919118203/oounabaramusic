package com.oounabaramusic.android.dao;

public class SqlInsertString {

    //本地音乐
    public static final String INSERT_HISTORICAL_QUERY_TBL=
            String.format("insert into %s (search_text) values (?)",SqlTableString.HISTORICAL_QUERY_TBL);


}
