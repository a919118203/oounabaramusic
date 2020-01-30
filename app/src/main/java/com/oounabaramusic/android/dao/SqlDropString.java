package com.oounabaramusic.android.dao;

public class SqlDropString {
    public static final String DROP_LOCAL_MUSIC_TBL=
            "drop table if exists " + SqlTableString.LOCAL_MUSIC_TBL;

    public static final String DROP_HISTORICAL_QUERY_TBL =
            "drop table if exists " + SqlTableString.HISTORICAL_QUERY_TBL;
}
