package com.oounabaramusic.android.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

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
        cv.put("duration",item.getDuration());
        cv.put("file_size",item.getFileSize());
        cv.put("md5",item.getMd5());
        cv.put("download_status",item.getDownloadStatus());
        cv.put("is_server",item.getIsServer());
        return db.insert(SqlTableString.LOCAL_MUSIC_TBL,null,cv);
    }

    /**
     * 检索全部本地音乐，不包括下载中的音乐
     * @return
     */
    public List<Music> selectAllLocalMusic(){
        List<Music> musics=new ArrayList<>();
        Music item;
        Cursor cursor=db.rawQuery(SqlSelectString.SELECT_ALL_LOCAL_MUSIC,null);
        if(cursor.moveToFirst()){
            do{
                musics.add(parseCursorToMusic(cursor));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return musics;
    }

    /**
     * 检索全部本地音乐，包括下载中的音乐
     * @return
     */
    public List<Music> selectAllMusic(){
        List<Music> musics=new ArrayList<>();
        Music item;
        Cursor cursor=db.rawQuery(SqlSelectString.SELECT_ALL_MUSIC,null);
        if(cursor.moveToFirst()){
            do{
                musics.add(parseCursorToMusic(cursor));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return musics;
    }

    /**
     *
     * @return 返回所有需要判断是不是服务器音乐的音乐的md5值
     */
    public List<String> selectAllNeedCheck(){
        Cursor cursor=db.rawQuery(SqlSelectString.SELECT_ALL_NEED_CHECK,null);
        List<String> result=new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                result.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return result;
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

    public boolean isServer(String md5){
        Cursor cursor=db.rawQuery(SqlSelectString.SELECT_IS_SERVER,new String[]{md5});
        if(cursor.moveToFirst()){
            if(cursor.getInt(0)==1){
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public List<Music> selectMusicByMusicName(String musicName){
        List<Music> musics=new ArrayList<>();
        Music item;
        Cursor cursor = db.rawQuery(SqlSelectString.SELECT_MUSIC_BY_MUSIC_NAME,new String[]{"%"+musicName+"%"});
        if(cursor.moveToFirst()){
            do{
                musics.add(parseCursorToMusic(cursor));
            }while(cursor.moveToNext());
        }
        cursor.close();

        return musics;
    }

    public Music selectMusicByMd5(String md5){
        Music result=null;
        Cursor cursor = db.rawQuery(SqlSelectString.SELECT_MUSIC_BY_MD5,new String[]{md5});
        if(cursor.moveToFirst()){
            result=parseCursorToMusic(cursor);
        }
        cursor.close();
        return result;
    }

    public List<Music> selectMusicByMd5(List<String> data){
        List<Music> result=new ArrayList<>();
        Music item;
        for(String md5:data){
            Cursor cursor = db.rawQuery(SqlSelectString.SELECT_MUSIC_BY_MD5,new String[]{md5});
            if(cursor.moveToFirst()){
                result.add(parseCursorToMusic(cursor));
            }
            cursor.close();
        }
        return result;
    }

    private Music parseCursorToMusic(Cursor cursor){
        Music result=new Music();
        result.setMusicName(cursor.getString(cursor.getColumnIndex("music_name")));
        result.setSingerName(cursor.getString(cursor.getColumnIndex("singer_name")));
        result.setSingerId(cursor.getString(cursor.getColumnIndex("singer_id")));
        result.setMd5(cursor.getString(cursor.getColumnIndex("md5")));
        result.setFilePath(cursor.getString(cursor.getColumnIndex("file_path")));
        result.setFileSize(cursor.getLong(cursor.getColumnIndex("file_size")));
        result.setSingerName(cursor.getString(cursor.getColumnIndex("singer_name")));
        result.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
        result.setDownloadStatus(cursor.getInt(cursor.getColumnIndex("download_status")));
        result.setIsServer(cursor.getInt(cursor.getColumnIndex("is_server")));
        return result;
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

    /**
     * 第一个     音乐名
     * 第二个     歌手名
     * 第三个     md5
     * @param item
     */
    public void updateMusicNameByMd5(String... item){
        db.execSQL(SqlUpdateString.UPDATE_MUSIC_NAME_BY_MD5,item);
    }

    public void deleteMusicByMd5(String md5){
        db.execSQL(SqlDeleteString.DELETE_MUSIC_BY_MD5,new String[]{md5});
    }

    public void updateIsServerByMd5(String... update){

        if(update.length==2){
            db.execSQL(SqlUpdateString.UPDATE_NO_IS_SERVER_BY_MD5,update);
        }else if(update.length==5){
            db.execSQL(SqlUpdateString.UPDATE_IS_SERVER_BY_MD5,update);
        }
    }

    public void updateDownloadStatus(String md5,int status){
        db.execSQL(SqlUpdateString.UPDATE_DOWNLOAD_STATUS_BY_MD5,new String[]{status+"",md5});
    }

    public List<Music> selectAllNeedDownload(){
        List<Music> result=new ArrayList<>();
        Cursor cursor=db.rawQuery(SqlSelectString.SELECT_ALL_NEED_DOWNLOAD,new String[]{});
        if(cursor.moveToFirst()){
            do{
                result.add(parseCursorToMusic(cursor));
            }while(cursor.moveToNext());
        }
        return result;
    }

    public List<Music> selectAllDownloadEnd(){
        List<Music> result=new ArrayList<>();
        Cursor cursor=db.rawQuery(SqlSelectString.SELECT_ALL_DOWNLOAD_END,new String[]{});
        if(cursor.moveToFirst()){
            do{
                result.add(parseCursorToMusic(cursor));
            }while(cursor.moveToNext());
        }
        return result;
    }

    public void updateDuration(String md5,int duration){
        db.execSQL(SqlUpdateString.UPDATE_DURATION_BY_MD5,new String[]{duration+"",md5});
    }
}
