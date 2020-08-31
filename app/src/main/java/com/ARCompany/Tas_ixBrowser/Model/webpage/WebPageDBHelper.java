package com.ARCompany.Tas_ixBrowser.Model.webpage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WebPageDBHelper extends SQLiteOpenHelper {

    //information fo database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TasixDB.db";
    public static final String TABLE_NAME = "WebPage";
    public static final String COLUMN_ID = "WebPageID";
    public static final String COLUMN_URL = "WebPageUrl";


    public WebPageDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        String CREATE_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT )", TABLE_NAME, COLUMN_ID, COLUMN_URL);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT )", TABLE_NAME, COLUMN_ID, COLUMN_URL);
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public List<WebPage> loadWebPages() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        List<WebPage> webPages = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String url = cursor.getString(1);
            webPages.add(new WebPage(id, url));
        }
        cursor.close();
        db.close();
        return webPages;
    }

    public WebPage getLastWebPage() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToLast();
        int id = cursor.getInt(0);
        String url = cursor.getString(1);
        WebPage webpage = new WebPage(id, url);
        cursor.close();
        db.close();
        return webpage;
    }

    public boolean addWebPage(String url) {
        boolean result = true;
        ContentValues values = new ContentValues();
        values.put(COLUMN_URL, url);
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.insert(TABLE_NAME, null, values);
        } catch (Exception ex) {
            result = false;
        }
        db.close();
        return result;
    }

    public void deleteWebPage(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "="+id, null);
    }

    public boolean updateWebPage(int id, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id);
        values.put(COLUMN_URL, url);
        return db.update(TABLE_NAME, values, String.format("%s=%d", COLUMN_ID, id), null) > 0;
    }
}
