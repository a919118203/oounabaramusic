package com.oounabaramusic.android.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.oounabaramusic.android.util.MyEnvironment;

public class BaseDao {
    protected MySQLiteOpenHelper helper;
    protected SQLiteDatabase db;

    public BaseDao(Context context){
        helper=new MySQLiteOpenHelper(context, MyEnvironment.databaseName,null,8);
        db=helper.getWritableDatabase();
    }
}
