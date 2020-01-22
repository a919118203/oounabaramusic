package com.oounabaramusic.android.dao;

public class SqlDeleteString {
    public static final String DELETE_ALL_LOCAL_MUSIC=
            "delete from "+SqlTableString.LOCAL_MUSIC_TBL;

    public static final String DELETE_MUSIC_BY_MD5=
            String.format("delete from %s where md5 = ?",SqlTableString.LOCAL_MUSIC_TBL);
}
