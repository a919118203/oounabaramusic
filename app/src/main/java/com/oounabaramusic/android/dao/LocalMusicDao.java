package com.oounabaramusic.android.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.oounabaramusic.android.bean.Music;

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
        List<Music> musics=new ArrayList<>();
        Music item;
        Cursor cursor=db.rawQuery(SqlSelectString.SELECT_ALL_LOCAL_MUSIC,null);
        if(cursor.moveToFirst()){
            do{
                item=new Music();
                item.setMusicName(cursor.getString(cursor.getColumnIndex("music_name")));
                item.setSingerName(cursor.getString(cursor.getColumnIndex("singer_name")));
                item.setMd5(cursor.getString(cursor.getColumnIndex("md5")));
                item.setFilePath(cursor.getString(cursor.getColumnIndex("file_path")));
                item.setFileSize(cursor.getLong(cursor.getColumnIndex("file_size")));
                item.setSingerName(cursor.getString(cursor.getColumnIndex("singer_name")));
                item.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
                musics.add(item);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return musics;
    }

    public void deleteAll(){
        db.execSQL(SqlDeleteString.DELETE_ALL_LOCAL_MUSIC);
    }

    public boolean md5IsExists(String md5){
        Cursor cursor=db.rawQuery(SqlSelectString.SELECT_MUSIC_BY_MD5,new String[]{md5});
        boolean f=cursor.moveToFirst();
        cursor.close();
        return f;
    }

    public List<Music> selectMusicByMusicName(String musicName){
        List<Music> musics=new ArrayList<>();
        Music item;
        Cursor cursor = db.rawQuery(SqlSelectString.SELECT_MUSIC_BY_MUSIC_NAME,new String[]{"%"+musicName+"%"});
        if(cursor.moveToFirst()){
            do{
                item=new Music();
                item.setMusicName(cursor.getString(cursor.getColumnIndex("music_name")));
                item.setSingerName(cursor.getString(cursor.getColumnIndex("singer_name")));
                item.setMd5(cursor.getString(cursor.getColumnIndex("md5")));
                item.setFilePath(cursor.getString(cursor.getColumnIndex("file_path")));
                item.setFileSize(cursor.getLong(cursor.getColumnIndex("file_size")));
                item.setSingerName(cursor.getString(cursor.getColumnIndex("singer_name")));
                item.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
                musics.add(item);
            }while(cursor.moveToNext());
        }
        cursor.close();

        return musics;
    }

    /**
     * 第一个   歌曲名
     * 第二个   歌手名
     * 第三个   路径
     * 第四个   md5
     * @param item
     */
    public void updateFileNameByMd5(String... item){
        db.execSQL(SqlUpdateString.UPDATE_FILE_NAME_BY_MD5,item);
    }

    public void deleteMusicByMd5(String md5){
        db.execSQL(SqlDeleteString.DELETE_MUSIC_BY_MD5,new String[]{md5});
    }
}
