package com.oounabaramusic.android.dao;

public class SqlUpdateString {
    public static final String UPDATE_FILE_NAME_BY_MD5=
            String.format("update %s set music_name = ? , singer_name = ? , file_path = ? where md5 = ?",SqlTableString.LOCAL_MUSIC_TBL);
}
