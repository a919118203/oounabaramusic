package com.oounabaramusic.android.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.oounabaramusic.android.bean.Music;
import com.oounabaramusic.android.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class LocalMusicDao extends BaseDao {

    public LocalMusicDao(Context context) {
        super(context);
    }

    public long insertLocalMusic(Music item){
        ContentValues cv=new ContentValues();
        cv.put("music_name",item.getMusicName());
        cv.put("singer_name",item.getSingerName());
        cv.put("singer_id",item.getSingerId());
        cv.put("file_path",item.getFilePath());
        cv.put("cover_path",item.getCoverPath());
        cv.put("duration",item.getDuration());
        cv.put("file_size",item.getFileSize());
        cv.put("md5",item.getMd5());
        cv.put("download_status",item.getDownloadStatus());
        cv.put("is_server",item.getIsServer());
        return db.insert(SqlTableString.LOCAL_MUSIC_TBL,null,cv);
    }

    public List<Music> selectAllLocalMusic(){
        List<Music> music=new ArrayList<>();
        Cursor cursor=db.rawQuery(SqlSelectString.SELECT_ALL_LOCAL_MUSIC,null);
        if(cursor.moveToFirst()){
            do{
                LogUtil.printLog(
                        cursor.getInt(cursor.getColumnIndex("id"))+
                                cursor.getString(cursor.getColumnIndex("music_name"))+
                                cursor.getString(cursor.getColumnIndex("singer_name"))+
                                cursor.getInt(cursor.getColumnIndex("singer_id"))+
                                cursor.getString(cursor.getColumnIndex("file_path"))+
                                cursor.getString(cursor.getColumnIndex("cover_path"))+
                                cursor.getInt(cursor.getColumnIndex("duration"))+
                                cursor.getInt(cursor.getColumnIndex("file_size"))+
                                cursor.getString(cursor.getColumnIndex("md5"))+
                                cursor.getInt(cursor.getColumnIndex("download_status"))+
                                cursor.getInt(cursor.getColumnIndex("is_server"))

                );
            }while(cursor.moveToNext());
        }
        cursor.close();
        return music;
    }
}
