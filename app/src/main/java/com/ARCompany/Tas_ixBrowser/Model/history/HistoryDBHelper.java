package com.ARCompany.Tas_ixBrowser.Model.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryDBHelper extends SQLiteOpenHelper {

    //information fo database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TasixDB.db";
    private static final String TABLE_NAME = "History";
    private static final String COLUMN_ID = "HistoryWebPageID";
    private static final String COLUMN_NAME = "HistoryWebPageName";
    private static final String COLUMN_URL = "HistoryWebPageUrl";
    private static final String COLUMN_DATE = "HistoryWebPageDate";

    private SimpleDateFormat iso8601Format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public HistoryDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT )", TABLE_NAME, COLUMN_ID,COLUMN_NAME, COLUMN_URL, COLUMN_DATE);
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT )", TABLE_NAME, COLUMN_ID,COLUMN_NAME, COLUMN_URL, COLUMN_DATE);
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public List<HistoryWebPage> loadHistory() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery(query, null);
        }catch (Exception ex){
            String s = "SELECT * FROM " + TABLE_NAME;
        }
        List<HistoryWebPage> webPages = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String url = cursor.getString(2);
            Date date = null;
            try {
                date = iso8601Format.parse(cursor.getString(3));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            webPages.add(new HistoryWebPage(id,name, url, date));
        }
        cursor.close();
        db.close();
        return webPages;
    }

    public boolean addHistory(String name, String url, Date date) {
        boolean result = true;
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_URL, url);
        values.put(COLUMN_DATE, iso8601Format.format(date));
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.insert(TABLE_NAME, null, values);
        } catch (Exception ex) {
            result = false;
        }
        db.close();
        return result;
    }

    public void deleteHistory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "="+id, null);
    }

    public void clearHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("DELETE FROM %s", TABLE_NAME));
    }
}
