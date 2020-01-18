package com.oounabaramusic.android.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class BaseDao {
    protected MySQLiteOpenHelper helper;
    protected SQLiteDatabase db;

    public BaseDao(Context context){
        helper=new MySQLiteOpenHelper(context,"oounabaramusic.db",null,5);
        db=helper.getWritableDatabase();
    }
}
