package com.oounabaramusic.android.dao;

public class SqlUpdateString {
    public static final String UPDATE_FILE_NAME_BY_MD5=
            String.format("update %s set music_name = ? , singer_name = ? , file_path = ? where md5 = ?",SqlTableString.LOCAL_MUSIC_TBL);

    public static final String UPDATE_NO_IS_SERVER_BY_MD5 =
            String.format("update %s set is_server = ?  where md5 = ?",SqlTableString.LOCAL_MUSIC_TBL);

    public static final String UPDATE_IS_SERVER_BY_MD5 =
            String.format("update %s set is_server = ? , music_name = ? ,singer_name = ? ,singer_id = ? ,download_status = 1 where md5 = ?",SqlTableString.LOCAL_MUSIC_TBL);

    public static final String UPDATE_MUSIC_NAME_BY_MD5=
            String.format("update %s set music_name = ? , singer_name = ? where md5 = ?",SqlTableString.LOCAL_MUSIC_TBL);

    public static final String UPDATE_DOWNLOAD_STATUS_BY_MD5=
            String.format("update %s set download_status = ? where md5 = ?",SqlTableString.LOCAL_MUSIC_TBL);

    public static final String UPDATE_DURATION_BY_MD5=
            String.format("update %s set duration = ? where md5 = ?",SqlTableString.LOCAL_MUSIC_TBL);
}
