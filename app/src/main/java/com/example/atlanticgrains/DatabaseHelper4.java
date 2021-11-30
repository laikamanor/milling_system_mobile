package com.example.atlanticgrains;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

public class DatabaseHelper4 extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "db44.db";
    public static final String TABLE_NAME = "tbl44";
    public  static  final String COL_1 = "id";
    public  static  final String COL_2 = "itemname";
    public  static  final String COL_3 = "quantity";
    public  static  final String COL_4 = "type";
    public  static  final String COL_5 = "status";
    public  static  final String COL_6 = "uom";
    public  static  final String COL_7 = "item_code";
    public  static  final String COL_8 = "fromWhse";
    public  static  final String COL_9 = "ToWhse";
//    public  static  final String COL_10 = "isDraft";
//    public  static  final String COL_11 = "header";
    public DatabaseHelper4(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT,itemname TEXT, quantity FLOAT,type TEXT,status INTEGER,uom TEXT,item_code TEXT,fromWhse TEXT,toWhse TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

//    public void deleteNonDraft(String type) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        int resultInt = db.delete(TABLE_NAME, "type = ? AND isDraft=0", new String[]{type});
//    }
//
//    public void updateInActiveDraft(String type) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_5, 1);
//        int resultInt = db.update(TABLE_NAME, contentValues, "type= ? AND isDraft=1", new String[]{type});
//        System.out.println("update int: " + resultInt);
//    }
//
//    public void updateQuantity2(String id, double quantity,String header) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=?;", new String[]{id});
//        JSONArray ja = new JSONArray();
//        while (cursor.moveToNext()) {
//            try {
//                JSONObject jo = new JSONObject();
//                jo.put("item_name", cursor.getString(1));
//                jo.put("quantity", quantity);
//                jo.put("type", cursor.getString(3));
//                jo.put("status", cursor.getInt(4));
//                jo.put("uom", cursor.getString(5));
//                jo.put("item_code", cursor.getString(6));
//                jo.put("fromWhse", cursor.getString(7));
//                jo.put("toWhse", cursor.getString(8));
//                jo.put("isDraft", cursor.getInt(9));
//                jo.put("header", cursor.getString(10));
//                ja.put(jo);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_5, 0);
//        db.update(TABLE_NAME, contentValues, "id= ? ", new String[]{id});
//
//        try {
//            for (int i = 0; i < ja.length(); i++) {
//                JSONObject jo = ja.getJSONObject(i);
//                contentValues = new ContentValues();
//                contentValues.put(COL_2, jo.getString("item_name"));
//                contentValues.put(COL_3, jo.getDouble("quantity"));
//                contentValues.put(COL_4, jo.getString("type"));
//                contentValues.put(COL_5, 1);
//                contentValues.put(COL_6, jo.getString("uom"));
//                contentValues.put(COL_7, jo.getString("item_code"));
//                contentValues.put(COL_8, jo.getString("fromWhse"));
//                contentValues.put(COL_9, jo.getString("toWhse"));
//                contentValues.put(COL_10, 0);
//                contentValues.put(COL_11, header);
//                System.out.println("header: " +header);
//                db.insert(TABLE_NAME, null, contentValues);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        System.out.println("dito????????");
//    }

//    public void removeItem(String id, String type) {
//        SQLiteDatabase db = this.getReadableDatabase();
////        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=?;", new String[]{id});
////        JSONArray ja = new JSONArray();
////        while (cursor.moveToNext()) {
////            try {
////                JSONObject jo = new JSONObject();
////                jo.put("item_name", cursor.getString(1));
////                jo.put("quantity", cursor.getDouble(2));
////                jo.put("type", cursor.getString(3));
////                jo.put("status", cursor.getInt(4));
////                jo.put("uom", cursor.getString(5));
////                jo.put("item_code", cursor.getString(6));
////                jo.put("fromWhse", cursor.getString(7));
////                jo.put("toWhse", cursor.getString(8));
////                jo.put("isDraft", cursor.getInt(9));
////                jo.put("header", cursor.getString(10));
////                ja.put(jo);
////            } catch (Exception ex) {
////                ex.printStackTrace();
////            }
////        }
//
//        if(countItems(type) <= 1){
//            db.delete(TABLE_NAME, "id = ?", new  String[] {id});
//        }else{
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(COL_5, 0);
//            db.update(TABLE_NAME, contentValues, "id= ? ", new String[]{id});
//        }
//    }

    public int isSameItemWhse(String itemName, String fromWhse, String toWhse){
        int id = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor result = db.rawQuery("SELECT id FROM " + TABLE_NAME + " WHERE itemname=? AND fromWhse=? AND toWhse=? AND status=1;", new String[]{itemName, fromWhse,toWhse});
        if(result.moveToFirst()){
            id = result.getInt(0);
        }
        return id;
    }

    public int isSameItemFromWhse(String itemName, String fromWhse){
        int id = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor result = db.rawQuery("SELECT id FROM " + TABLE_NAME + " WHERE itemname=? AND fromWhse=? AND status=1;", new String[]{itemName, fromWhse});
        if(result.moveToFirst()){
            id = result.getInt(0);
        }
        return id;
    }

    public boolean insertData(String itemName, Double quantity, String type, Integer status, String uom,String itemCode,String fromWhse,String toWhse){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, itemName);
        contentValues.put(COL_3, quantity);
        contentValues.put(COL_4, type);
        contentValues.put(COL_5, status);
        contentValues.put(COL_6, uom);
        contentValues.put(COL_7, itemCode);
        contentValues.put(COL_8, fromWhse);
        contentValues.put(COL_9, toWhse);
        long resultQuery = db.insert(TABLE_NAME,null,contentValues);
        boolean result;
        result = resultQuery != -1;
        return result;
    }

//    public boolean insertData(String itemName, Double quantity, String type, Integer status, String uom,String itemCode,String fromWhse,String toWhse){
//        SQLiteDatabase db = this.getReadableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_2, itemName);
//        contentValues.put(COL_3, quantity);
//        contentValues.put(COL_4, type);
//        contentValues.put(COL_5, status);
//        contentValues.put(COL_6, uom);
//        contentValues.put(COL_7, itemCode);
//        contentValues.put(COL_8, fromWhse);
//        contentValues.put(COL_9, toWhse);
//        contentValues.put(COL_10, 0);
//        contentValues.put(COL_11, "");
//        long resultQuery = db.insert(TABLE_NAME,null,contentValues);
//        boolean result;
//        result = resultQuery != -1;
//        return result;
//    }

    public boolean updateDraftByStatus(String type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_5, 1);
        int i = db.update(TABLE_NAME, contentValues, "type= ? AND isDraft=1", new String[]{type});
        return i > 0;
    }
//    public void deleteNonDraftsByType(String type){
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE isDraft=0 AND type='" + type + "';");
//        db.execSQL("VACUUM");
//    }

    public void deleteNonDrafts(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE isDraft=0");
        db.execSQL("VACUUM");
    }

    public void deleteDrafts(String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE isDraft=1 AND type='" + type + "';");
        db.execSQL("VACUUM");
    }
//
//    public boolean insertDataDrafts(String header, String type){
//        SQLiteDatabase db = this.getReadableDatabase();
//        boolean result = false;
//        long resultQuery = 0;
//        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE type='" + type + "' AND status=1;", null);
//        JSONArray ja = new JSONArray();
//        while (cursor.moveToNext()){
//            try{
//                JSONObject jo = new JSONObject();
//                jo.put("id", cursor.getInt(0));
//                jo.put("item_name", cursor.getString(1));
//                jo.put("quantity", cursor.getDouble(2));
//                jo.put("type", cursor.getString(3));
//                jo.put("status", cursor.getInt(4));
//                jo.put("uom", cursor.getString(5));
//                jo.put("item_code", cursor.getString(6));
//                jo.put("fromWhse", cursor.getString(7));
//                jo.put("toWhse", cursor.getString(8));
//                jo.put("isDraft", cursor.getInt(9));
//                jo.put("header", header);
//                ja.put(jo);
//            }catch (Exception ex){
//                ex.printStackTrace();
//            }
//        }
//        deleteByType(type);
//        try{
//            for(int i =0; i < ja.length();i++){
//                JSONObject jo = ja.getJSONObject(i);
////                if(jo.getInt("isDraft") == 1 && jo.getInt("status") == 1){
////                    ContentValues contentValues = new ContentValues();
////                    contentValues.put(COL_5, 0);
////                    db.update(TABLE_NAME, contentValues, "id=?", new String[]{String.valueOf(jo.getInt("id"))});
////                }else{
////
////                }
//
//                System.out.println("meron ba??????");
//                ContentValues contentValues = new ContentValues();
//                contentValues.put(COL_2, jo.getString("item_name"));
//                contentValues.put(COL_3, jo.getDouble("quantity"));
//                contentValues.put(COL_4, jo.getString("type"));
//                contentValues.put(COL_5, 1);
//                contentValues.put(COL_6, jo.getString("uom"));
//                contentValues.put(COL_7, jo.getString("item_code"));
//                contentValues.put(COL_8, jo.getString("fromWhse"));
//                contentValues.put(COL_9, jo.has("toWhse") ? !jo.isNull("toWhse") ? jo.getString("toWhse") : "" : "");
//                contentValues.put(COL_10, 1);
//                contentValues.put(COL_11, header);
//                resultQuery += db.insert(TABLE_NAME,null,contentValues);
//
//            }
//        }catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
////        int isDraft = 0;
////        Cursor cursor2 = db.rawQuery("SELECT isDraft FROM " + TABLE_NAME + " WHERE id = ? ", new String[]{id});
////        while (cursor2.moveToNext()){
////            isDraft = cursor2.getInt(0);
////        }
////
////        if(isDraft <= 0 && resultQuery > 0){
////            resultInt = db.delete(TABLE_NAME, "id = ?", new  String[] {id});
////        }else{
////
////        }
//
//        result = resultQuery > 0;
//        return result;
//    }

    public Integer deleteType(String type){
        int result;
        SQLiteDatabase db = this.getWritableDatabase();
        result = db.delete(TABLE_NAME, "type = ?", new  String[] {type});
        return result;
    }

    public Integer deleteData(String id,String type){
        int result;
        SQLiteDatabase db = this.getWritableDatabase();

        System.out.println("id: " + id);
        result = db.delete(TABLE_NAME, "id = ?", new  String[] {id});
        System.out.println("delete? " + result);
        return result;
    }

//    public Cursor getAllData(String type){
//        Cursor cursor;
//        SQLiteDatabase db = this.getWritableDatabase();
//        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE type='" + type + "' AND status=1;", null);
//        return cursor;
//    }

    public Cursor getAllData(String type){
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE type='" + type + "';", null);
        return cursor;
    }

//    public Cursor getAllDataDraft(String type){
//        Cursor cursor;
//        SQLiteDatabase db = this.getWritableDatabase();
//        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE type='" + type + "' AND isDraft=1", null);
//        System.out.println("SELECT * FROM " + TABLE_NAME + " WHERE type='" + type + "' AND status=1");
//        return cursor;
//    }

    public Cursor getAllWhereItem(String type,String itemName){
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE type='" + type + "' AND itemname='" +itemName + "';", null);
        return  cursor;
    }

//    public boolean updateQuantity(String id,double actual_quantity){
//        SQLiteDatabase db2 = this.getReadableDatabase();
//        double currentQuantity = 0.00;
//        @SuppressLint("Recycle") Cursor cursor = db2.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=?;", new String[]{id});
//        JSONArray ja = new JSONArray();
//        while (cursor.moveToNext()){
//            try{
////                currentQuantity =cursor.getDouble(2);
//                JSONObject jo = new JSONObject();
//                jo.put("item_name", cursor.getString(1));
//                jo.put("quantity", cursor.getDouble(2));
//                jo.put("type", cursor.getString(3));
//                jo.put("status", 1);
//                jo.put("uom", cursor.getString(5));
//                jo.put("item_code", cursor.getInt(6));
//                jo.put("fromWhse", cursor.getString(7));
//                jo.put("toWhse", cursor.getString(8));
//                jo.put("isDraft", cursor.getInt(9));
//                jo.put("header", cursor.getString(10));
//                ja.put(jo);
//            }catch (Exception ex){
//                ex.printStackTrace();
//            }
//        }
//        SQLiteDatabase db  = this.getWritableDatabase();
//        double finalQuantity = currentQuantity + actual_quantity;
//        long resultQuery = 0;
//        try{
//            for(int i =0; i < ja.length();i++){
//                JSONObject jo = ja.getJSONObject(i);
//                System.out.println("meron ba?");
//                ContentValues contentValues = new ContentValues();
//                contentValues.put(COL_2, jo.getString("item_name"));
//                contentValues.put(COL_3,finalQuantity);
//                contentValues.put(COL_4, jo.getString("type"));
//                contentValues.put(COL_5, 1);
//                contentValues.put(COL_6, jo.getString("uom"));
//                contentValues.put(COL_7, jo.getString("item_code"));
//                contentValues.put(COL_8, jo.getString("fromWhse"));
//                contentValues.put(COL_9, jo.getString("toWhse"));
//                contentValues.put(COL_10, 0);
//                contentValues.put(COL_11, "");
//                resultQuery += db.insert(TABLE_NAME,null,contentValues);
//            }
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//        int resultInt = 0;
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_5, 0);
//        resultInt = db.update(TABLE_NAME, contentValues, "id= ?", new String[]{id});
//        System.out.println("dito pre");
//        return resultInt > 0;
//    }

    public boolean updateQuantity(String id,double actual_quantity, boolean isAdd){

        double currentQty = 0.00;
        if(isAdd){
            SQLiteDatabase db2 = this.getWritableDatabase();
            Cursor cursor = db2.rawQuery("SELECT quantity FROM " + TABLE_NAME + " WHERE id=?", new String[]{id});
            if(cursor !=null) {
                if(cursor.moveToFirst()){
                    currentQty = cursor.getDouble(0);
                }
            }
        }
        double updatedQty = currentQty + actual_quantity;
        SQLiteDatabase db  = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_3, updatedQty);
        db.update(TABLE_NAME, contentValues, "id= ?", new String[]{id});
        return true;
    }

    public boolean updateQuantityWhse(String id,double actual_quantity,String whseName, String whseValue, boolean isAdd){

        double currentQty = 0.00;
        if(isAdd){
            SQLiteDatabase db2 = this.getWritableDatabase();
            Cursor cursor = db2.rawQuery("SELECT quantity FROM " + TABLE_NAME + " WHERE id=?", new String[]{id});
            if(cursor !=null) {
                if(cursor.moveToFirst()){
                    currentQty = cursor.getDouble(0);
                }
            }
        }
        double updatedQty = currentQty + actual_quantity;
        SQLiteDatabase db  = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_3, updatedQty);
        contentValues.put(whseName, whseValue);
        db.update(TABLE_NAME, contentValues, "id= ?", new String[]{id});
        return true;
    }

    public boolean updateTransferItem(String id,double actual_quantity,String fromWhse,String toWhse){
        SQLiteDatabase db  = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_3, actual_quantity);
        contentValues.put(COL_8, fromWhse);
        contentValues.put(COL_9, toWhse);
        db.update(TABLE_NAME, contentValues, "id= ?", new String[]{id});
        return true;
    }

//    public boolean updateQuantityWhse(String id,double actual_quantity,String whseName, String whseValue, String header, boolean isAdd){
//        SQLiteDatabase db  = this.getWritableDatabase();
//        SQLiteDatabase db2  = this.getReadableDatabase();
//
//        @SuppressLint("Recycle") Cursor cursor = db2.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=?;", new String[]{id});
//        JSONArray ja = new JSONArray();
//        while (cursor.moveToNext()) {
//            try {
////              currentQuantity =cursor.getDouble(2);
//                JSONObject jo = new JSONObject();
//                jo.put("item_name", cursor.getString(1));
//                jo.put("quantity", cursor.getDouble(2));
//                jo.put("type", cursor.getString(3));
//                jo.put("status", 1);
//                jo.put("uom", cursor.getString(5));
//                jo.put("item_code", cursor.getString(6));
//                jo.put("fromWhse", cursor.getString(7));
//                jo.put("toWhse", cursor.getString(8));
//                jo.put("isDraft", cursor.getInt(9));
//                jo.put("header", cursor.getString(10));
//                ja.put(jo);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_5, 0);
//        db.update(TABLE_NAME, contentValues, "id= ?", new String[]{id});
//        System.out.println("updated?");
//
//        long resultQuery = 0;
//        try{
//            for(int i =0; i < ja.length();i++){
//                JSONObject jo = ja.getJSONObject(i);
//                contentValues = new ContentValues();
//                contentValues.put(COL_2, jo.getString("item_name"));
//                if(isAdd){
//                    double val = actual_quantity + jo.getDouble("quantity");
//                    System.out.println("value: " + val);
//                    contentValues.put(COL_3,val);
//                }else {
//                    contentValues.put(COL_3, actual_quantity);
//                }
//                contentValues.put(COL_4, jo.getString("type"));
//                contentValues.put(COL_5, 1);
//                contentValues.put(COL_6, jo.getString("uom"));
//                contentValues.put(COL_7, jo.getString("item_code"));
//
//                if(whseName.equals("fromWhse")){
//                    contentValues.put(COL_8, whseValue);
//                }else if(whseName.equals("toWhse")){
//                    contentValues.put(COL_9, whseValue);
//                }
//                contentValues.put(COL_10, 0);
//                contentValues.put(COL_11, header);
//                System.out.println("header: " +header);
//                resultQuery += db.insert(TABLE_NAME,null,contentValues);
//            }
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//
//        return true;
//    }

//    public boolean updateTransferItem(String id,double actual_quantity,String fromWhse,String toWhse, String header){
//        SQLiteDatabase db  = this.getWritableDatabase();
//        SQLiteDatabase db2  = this.getReadableDatabase();
//
//        @SuppressLint("Recycle") Cursor cursor = db2.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=?;", new String[]{id});
//        JSONArray ja = new JSONArray();
//        while (cursor.moveToNext()){
//            try{
////                currentQuantity =cursor.getDouble(2);
//                JSONObject jo = new JSONObject();
//                jo.put("item_name", cursor.getString(1));
//                jo.put("quantity", cursor.getDouble(2));
//                jo.put("type", cursor.getString(3));
//                jo.put("status", 1);
//                jo.put("uom", cursor.getString(5));
//                jo.put("item_code", cursor.getString(6));
//                jo.put("fromWhse", cursor.getString(7));
//                jo.put("toWhse", cursor.getString(8));
//                jo.put("isDraft", cursor.getInt(9));
//                jo.put("header", cursor.getString(10));
//                ja.put(jo);
//            }catch (Exception ex){
//                ex.printStackTrace();
//            }
//        }
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_5, 0);
//        db.update(TABLE_NAME, contentValues, "id= ?", new String[]{id});
//
//        long resultQuery = 0;
//        try{
//            for(int i =0; i < ja.length();i++){
//                JSONObject jo = ja.getJSONObject(i);
//                System.out.println("meron ba?");
//                contentValues = new ContentValues();
//                contentValues.put(COL_2, jo.getString("item_name"));
//                contentValues.put(COL_3,actual_quantity);
//                contentValues.put(COL_4, jo.getString("type"));
//                contentValues.put(COL_5, 1);
//                contentValues.put(COL_6, jo.getString("uom"));
//                contentValues.put(COL_7, jo.getString("item_code"));
//                contentValues.put(COL_8, fromWhse);
//                contentValues.put(COL_9, toWhse);
//                contentValues.put(COL_10, 0);
//                contentValues.put(COL_11, header);
//                System.out.println("header: " +header);
//                resultQuery += db.insert(TABLE_NAME,null,contentValues);
//            }
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//        return true;
//    }

//    public Integer countItems(String type){
//        int resultPrice = 0;
//        SQLiteDatabase db = this.getReadableDatabase();
//        @SuppressLint("Recycle") Cursor result = db.rawQuery("SELECT COUNT(id) FROM " + TABLE_NAME + " WHERE type='" + type + "' AND status=1;", null);
//        if(result.moveToFirst()){
//            do{
//                resultPrice = Integer.parseInt(result.getString(0));
//            }
//            while (result.moveToNext());
//        }
//        return resultPrice;
//    }

    public Integer countItems(String type){
        int resultPrice = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor result = db.rawQuery("SELECT COUNT(id) FROM " + TABLE_NAME + " WHERE type='" + type + "';", null);
        if(result.moveToFirst()){
            do{
                resultPrice = Integer.parseInt(result.getString(0));
            }
            while (result.moveToNext());
        }
        return resultPrice;
    }

//    public boolean checkItem(String itemName,String type){
//        boolean result = false;
//        Cursor cursor = null;
//        try {
//            SQLiteDatabase db = this.getReadableDatabase();
//            cursor = db.rawQuery("SELECT id FROM " + TABLE_NAME + " WHERE itemname=? AND type='" + type + "' AND status=1;", new String[]{itemName});
//            if (cursor != null) {
//                if (cursor.moveToNext()) {
//                    result = true;
//                }
//            }
//        }finally {
//            if(cursor != null){
//                cursor.close();
//            }
//        }
//        return result;
//    }


    public boolean checkItem(String itemName,String type){
        boolean result = false;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT id FROM " + TABLE_NAME + " WHERE itemname=? AND type='" + type + "';", new String[]{itemName});
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    result = true;
                }
            }
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return result;
    }

//    public boolean checkItemFWhse(String fromModule,String item, String fWhse) {
//        Cursor cursor = null;
//        boolean bIsExist = false;
//        int isExist = 0;
//        SQLiteDatabase db = this.getReadableDatabase();
//        cursor = db.rawQuery("SELECT fromWhse FROM " + TABLE_NAME + " WHERE itemname=? AND type='" + fromModule + "' AND status=1;", new String[]{item});
//        if(cursor != null){
//            while (cursor.moveToNext()){
//                System.out.println(cursor.getString(0) + "/" + fWhse + "/" + item);
//                if(cursor.getString(0).toLowerCase().contains(fWhse.toLowerCase())){
//                    isExist ++;
//                }
//            }
//            if(isExist > 0){
//                bIsExist = true;
//            }
//        }
//        return bIsExist;
//    }

    public boolean checkItemFWhse(String fromModule,String item, String fWhse) {
        Cursor cursor = null;
        boolean bIsExist = false;
        int isExist = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        cursor = db.rawQuery("SELECT fromWhse FROM " + TABLE_NAME + " WHERE itemname=? AND type='" + fromModule + "';", new String[]{item});
        if(cursor != null){
            while (cursor.moveToNext()){
                System.out.println(cursor.getString(0) + "/" + fWhse + "/" + item);
                if(cursor.getString(0).toLowerCase().contains(fWhse.toLowerCase())){
                    isExist ++;
                }
            }
            if(isExist > 0){
                bIsExist = true;
            }
        }
        return bIsExist;
    }
//
//    public void deleteByType(String type){
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE type= '" + type + "';");
//        db.execSQL("VACUUM");
//    }


    public void truncateTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.execSQL("VACUUM");
    }

}
