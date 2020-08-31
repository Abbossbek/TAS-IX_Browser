package com.ARCompany.Tas_ixBrowser.Model.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.ARCompany.Tas_ixBrowser.Model.history.HistoryWebPage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoteDBHelper extends SQLiteOpenHelper {

    //information fo database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TasixDB.db";
    private static final String TABLE_NAME = "Note";
    private static final String COLUMN_ID = "NoteID";
    private static final String COLUMN_NAME = "NoteName";
    private static final String COLUMN_URL = "NoteUrl";

    public NoteDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT )", TABLE_NAME, COLUMN_ID,COLUMN_NAME, COLUMN_URL);
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT )", TABLE_NAME, COLUMN_ID,COLUMN_NAME, COLUMN_URL);
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public List<Note> loadNotes() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery(query, null);
        }catch (Exception ex){
            String s = "SELECT * FROM " + TABLE_NAME;
        }
        List<Note> notes = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String url = cursor.getString(2);

            notes.add(new Note(id,name, url));
        }
        cursor.close();
        db.close();
        return notes;
    }

    public boolean addNote(String name, String url) {
        boolean result = true;
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
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

    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "="+id, null);
    }

}
