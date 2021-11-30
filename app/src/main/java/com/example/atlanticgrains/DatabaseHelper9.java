package com.example.atlanticgrains;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper9 extends SQLiteOpenHelper {
    public  static  final String DATABASE_NAME = "db_issueProducctionn.db";
    public  static  final String TABLE_NAME = "tbl_isssuee";
    public  static  final String COL1 = "id";
    public  static  final String COL_2 = "URL";
    public  static  final String COL_3 = "method";
    public  static  final String COL_4 = "response";
    public  static  final String COL_5 = "from_module";
    public  static  final String COL_6 = "date_created";
    public  static  final String COL_7 = "base_id";
    public DatabaseHelper9(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT,URL TEXT, method TEXT,response TEXT, from_module TEXT, date_created TEXT,base_id INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getAllData(){
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return cursor;
    }

    public boolean insertData(String sURL, String method, String fromModule, String response, String dateCreated, int baseID){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, sURL);
        contentValues.put(COL_3, method);
        contentValues.put(COL_4, response);
        contentValues.put(COL_5, fromModule);
        contentValues.put(COL_6, dateCreated);
        contentValues.put(COL_7, baseID);
        long resultQuery = db.insert(TABLE_NAME,null,contentValues);
        boolean result;
        result = resultQuery != -1;
        return result;
    }

    public void truncateTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.execSQL("VACUUM");
    }

    public Integer countItems(String fromModule){
        int resultPrice = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor result = db.rawQuery("SELECT COUNT(id) FROM " + TABLE_NAME + " WHERE from_module='" + fromModule + "';", null);
        if(result.moveToFirst()){
            do{
                resultPrice = Integer.parseInt(result.getString(0));
            }
            while (result.moveToNext());
        }
        return resultPrice;
    }
}
