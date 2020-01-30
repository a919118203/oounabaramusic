package com.oounabaramusic.android.dao;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class HistoricalQueryDao extends BaseDao {

    public HistoricalQueryDao(Context context) {
        super(context);
    }

    public List<String> selectLimitSearch(int limit){
        Cursor cursor=db.rawQuery(SqlSelectString.SELECT_HISTORICAL_QUERY,new String[]{limit+""});
        List<String> result=new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                result.add(cursor.getString(cursor.getColumnIndex("search_text")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public void insertHistoricalQuery(String item){
        deleteBySearchText(item);
        db.execSQL(SqlInsertString.INSERT_HISTORICAL_QUERY_TBL,new String[]{item});
    }

    public void deleteAll(){
        db.execSQL(SqlDeleteString.DELETE_HISTORICAL_QUERY,new String[]{});
    }

    public void deleteBySearchText(String searchText){
        db.execSQL(SqlDeleteString.DELETE_BY_SEARCH_TEXT,new String[]{searchText,searchText});
    }
}
