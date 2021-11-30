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

public class DatabaseHelper3 extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "db33.db";
    public static final String TABLE_NAME = "tbl33";
    public  static  final String COL_1 = "id";
    public  static  final String COL_2 = "sap_number";
    public  static  final String COL_3 = "fromBranch";
    public  static  final String COL_4 = "item_name";
    public  static  final String COL_5 = "quantity";
    public  static  final String COL_6 = "actual_quantity";
    public  static  final String COL_7 = "isSelected";
    public  static  final String COL_8 = "isSAPIT";
    public  static  final String COL_9 = "toBranch";
    public  static  final String COL_10 = "base_id";
    public  static  final String COL_11 = "fromModule";
    public  static  final String COL_12 = "uom";
    public  static  final String COL_13 = "received_quantity";
    public  static  final String COL_14 = "item_id";
    public  static  final String COL_15 = "objtype";
    public  static  final String COL_16 = "isClosed";
    public  static  final String COL_17 = "item_code";
    public  static  final String COL_18 = "data";
    public  static  final String COL_19 = "fromWhse";
    public  static  final String COL_20 = "toWhse";
//    public  static  final String COL_21 = "status";
//    public  static  final String COL_22 = "isDraft";
//    public  static  final String COL_23 = "header";
//    public  static  final String COL_24 = "isSelect";
    public DatabaseHelper3(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT,sap_number INTEGER, fromBranch TEXT,item_name TEXT,quantity FLOAT,actual_quantity FLOAT,isSelected INTEGER,isSAPIT INTEGER,toBranch TEXT, base_id INTEGER,fromModule TEXT,uom TEXT,received_quantity INTEGER,item_id INTEGER, objtype INTEGER, isClosed INTEGER,item_code TEXT,data TEXT,fromWhse TEXT, toWhse TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String sapNumber, String fromBranch,  String itemName,Double quantity,Double actual_quantity,int isSAPIT,String toBranch,int baseID, String fromModule,int isSelected, String uom,int received_quantity, int item_id, int objtype, int isClosed,String itemCode,String data,String fromWhse, String toWhse) {
        boolean result;
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, sapNumber);
        contentValues.put(COL_3, fromBranch);
        contentValues.put(COL_4, itemName);
        contentValues.put(COL_5, quantity);
        contentValues.put(COL_6, actual_quantity);
        contentValues.put(COL_7, isSelected);
        contentValues.put(COL_8, isSAPIT);
        contentValues.put(COL_9, toBranch);
        contentValues.put(COL_10, baseID);
        contentValues.put(COL_11, fromModule);
        contentValues.put(COL_12, uom);
        contentValues.put(COL_13, received_quantity);
        contentValues.put(COL_14, item_id);
        contentValues.put(COL_15, objtype);
        contentValues.put(COL_16, isClosed);
        contentValues.put(COL_17, itemCode);
        contentValues.put(COL_18, data);
        contentValues.put(COL_19, fromWhse);
        contentValues.put(COL_20, toWhse);
        long resultQuery = db.insert(TABLE_NAME, null, contentValues);
        result = resultQuery != -1;
        return result;
    }


//    public boolean insertData(String sapNumber, String fromBranch,  String itemName,Double quantity,Double actual_quantity,int isSAPIT,String toBranch,int baseID, String fromModule,int isSelected, String uom,int received_quantity, int item_id, int objtype, int isClosed,String itemCode,String data,String fromWhse, String toWhse) {
//        boolean result = false;
//        long resultQuery = 0;
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        JSONArray ja = new JSONArray();
//        Cursor c = getAllDataIsSelectedDraft(sapNumber);
//        try{
//            while (c.moveToNext()){
//                JSONObject jo = new JSONObject();
//                jo.put("id", c.getInt(0));
//                jo.put("sap_number", c.getString(1));
//                jo.put("item_name", c.getString(3));
//                jo.put("quantity", c.getDouble(4));
//                ja.put(jo);
//                System.out.println("wala na? " + c.getString(1));
//            }
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//        try{
//            if(ja.length() >0){
//                for(int i = 0; i < ja.length(); i++){
//                    JSONObject jo = ja.getJSONObject(i);
//                    String jsapNumber = jo.has("sap_number") ? !jo.isNull("sap_number") ? jo.getString("sap_number") : "" : "";
//                    String jitemName = jo.has("item_name") ? !jo.isNull("item_name") ? jo.getString("item_name") : "" : "";
//                    int id = jo.has("id") ? !jo.isNull("id") ? jo.getInt("id") : 0 : 0;
//                    double jQuantity = jo.has("quantity") ? !jo.isNull("quantity") ? jo.getDouble("quantity") : 0.000 : 0.000;
//                    System.out.println(id + "/jsapNumber : " + jsapNumber + "/sap_number: " + sapNumber + "/jItemName: " + jitemName + "/item_name: " + itemName + "/jquantity: " + jQuantity + "/" + quantity);
//                    if(jitemName.trim().toLowerCase().equals(itemName.trim().toLowerCase())){
//                        System.out.println("weley?");
//                        resultQuery+= updateStatusById(String.valueOf(id));
//                    }
//                    else{
//                        ContentValues contentValues = new ContentValues();
//                        contentValues.put(COL_2, sapNumber);
//                        contentValues.put(COL_3, fromBranch);
//                        contentValues.put(COL_4, itemName);
//                        contentValues.put(COL_5, quantity);
//                        contentValues.put(COL_6, actual_quantity);
//                        contentValues.put(COL_7, isSelected);
//                        contentValues.put(COL_8, isSAPIT);
//                        contentValues.put(COL_9, toBranch);
//                        contentValues.put(COL_10, baseID);
//                        contentValues.put(COL_11, fromModule);
//                        contentValues.put(COL_12, uom);
//                        contentValues.put(COL_13, received_quantity);
//                        contentValues.put(COL_14, item_id);
//                        contentValues.put(COL_15, objtype);
//                        contentValues.put(COL_16, isClosed);
//                        contentValues.put(COL_17, itemCode);
//                        contentValues.put(COL_18, data);
//                        contentValues.put(COL_19, fromWhse);
//                        contentValues.put(COL_20, toWhse);
//                        contentValues.put(COL_21, 1);
//                        contentValues.put(COL_22, 0);
//                        contentValues.put(COL_23, "");
//                        contentValues.put(COL_24, 1);
//                        resultQuery += db.insert(TABLE_NAME, null, contentValues);
//                    }
//                }
//            }else{
//                ContentValues contentValues = new ContentValues();
//                contentValues.put(COL_2, sapNumber);
//                contentValues.put(COL_3, fromBranch);
//                contentValues.put(COL_4, itemName);
//                contentValues.put(COL_5, quantity);
//                contentValues.put(COL_6, actual_quantity);
//                contentValues.put(COL_7, isSelected);
//                contentValues.put(COL_8, isSAPIT);
//                contentValues.put(COL_9, toBranch);
//                contentValues.put(COL_10, baseID);
//                contentValues.put(COL_11, fromModule);
//                contentValues.put(COL_12, uom);
//                contentValues.put(COL_13, received_quantity);
//                contentValues.put(COL_14, item_id);
//                contentValues.put(COL_15, objtype);
//                contentValues.put(COL_16, isClosed);
//                contentValues.put(COL_17, itemCode);
//                contentValues.put(COL_18, data);
//                contentValues.put(COL_19, fromWhse);
//                contentValues.put(COL_20, toWhse);
//                contentValues.put(COL_21, 1);
//                contentValues.put(COL_22, 0);
//                contentValues.put(COL_23, "");
//                contentValues.put(COL_24, 1);
//                resultQuery += db.insert(TABLE_NAME, null, contentValues);
//            }
//
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//        result = resultQuery != -1;
//        return result;
//    }

    public void deleteBySAPNumber(String sapNumber){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE sap_number= '" + sapNumber + "';");
        db.execSQL("VACUUM");
    }

//    public Cursor getAllDataIsSelectedDraft(String reference){
//        Cursor cursor;
//        SQLiteDatabase db = this.getWritableDatabase();
//        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE sap_number=? AND isDraft=1 AND status=1;", new String[]{reference});
//        return cursor;
//    }
//
//    public Integer updateIsSelected(String fromModule){
//        int result = 0;
//        SQLiteDatabase db  = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_24, 0);
//        result = db.update(TABLE_NAME, contentValues, "fromModule=?", new String[]{fromModule});
//        System.out.println("mga rows affected: " + result);
//        return  result;
//    }
//
//    public Integer updateStatusById(String id){
//        int result = 0;
//        SQLiteDatabase db  = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_24, 1);
//        contentValues.put(COL_21, 1);
//        System.out.println("id " + id);
//        result = db.update(TABLE_NAME, contentValues, "id=?", new String[]{id});
//        return  result;
//    }
//
//    public boolean insertDataDraft(String header, String fromModule){
//        boolean result = false;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = getAllSelectedData(fromModule);
//        JSONArray ja = new JSONArray();
//        String sapNumber = "";
//        while (cursor.moveToNext()){
//            try{
//                JSONObject jo = new JSONObject();
//                sapNumber = cursor.getString(1);
//                jo.put("sap_number", cursor.getString(1));
//                jo.put("fromBranch", cursor.getString(2));
//                jo.put("item_name", cursor.getString(3));
//                jo.put("quantity", cursor.getDouble(4));
//                jo.put("actual_quantity", cursor.getDouble(5));
//                jo.put("isSelected", cursor.getInt(6));
//                jo.put("isSAPIT", cursor.getInt(7));
//                jo.put("toBranch", cursor.getString(8));
//                jo.put("base_id", cursor.getInt(9));
//                jo.put("fromModule", cursor.getString(10));
//                jo.put("uom", cursor.getString(11));
//                jo.put("received_quantity", cursor.getDouble(12));
//                jo.put("item_id", cursor.getInt(13));
//                jo.put("objtype", cursor.getInt(14));
//                jo.put("isClosed", cursor.getInt(15));
//                jo.put("item_code", cursor.getString(16));
//                jo.put("data", cursor.getString(17));
//                jo.put("fromWhse", cursor.getString(18));
//                jo.put("toWhse", cursor.getString(19));
//                jo.put("status", 1);
//                jo.put("isDraft", 1);
//                jo.put("header", header);
//                jo.put("isSelect", 0);
//                ja.put(jo);
//            }catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//        deleteBySAPNumber(sapNumber);
//        long resultQuery = 0;
//        try{
//            for(int i =0; i < ja.length();i++){
//                JSONObject jo = ja.getJSONObject(i);
//                ContentValues contentValues = new ContentValues();
//                contentValues.put(COL_2, jo.getString("sap_number"));
//                contentValues.put(COL_3, jo.getString("fromBranch"));
//                contentValues.put(COL_4, jo.getString("item_name"));
//                contentValues.put(COL_5, jo.getDouble("quantity"));
//                contentValues.put(COL_6, jo.getDouble("actual_quantity"));
//                contentValues.put(COL_7, jo.getInt("isSelected"));
//                contentValues.put(COL_8, jo.getInt("isSAPIT"));
//                contentValues.put(COL_9, jo.getString("toBranch"));
//                contentValues.put(COL_10, jo.getInt("base_id"));
//                contentValues.put(COL_11, jo.getString("fromModule"));
//                contentValues.put(COL_12, jo.getString("uom"));
//                contentValues.put(COL_13, jo.getDouble("received_quantity"));
//                contentValues.put(COL_14, jo.getInt("item_id"));
//                contentValues.put(COL_15, jo.getInt("objtype"));
//                contentValues.put(COL_16, jo.getInt("isClosed"));
//                contentValues.put(COL_17, jo.getString("item_code"));
//                contentValues.put(COL_18, jo.getString("data"));
//                contentValues.put(COL_19, jo.getString("fromWhse"));
//                contentValues.put(COL_20, jo.getString("toWhse"));
//                contentValues.put(COL_21, 1);
//                contentValues.put(COL_22, 1);
//                contentValues.put(COL_23, jo.getString("header"));
//                contentValues.put(COL_24, 0);
//                resultQuery += db.insert(TABLE_NAME,null,contentValues);
//
//            }
//        }catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        result = resultQuery > 0;
//        return result;
//    }

//    public Cursor getAllData(String fromModule){
//        Cursor cursor;
//        SQLiteDatabase db = this.getWritableDatabase();
//        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE fromModule='" + fromModule +"' AND isSelect=1 AND status=1;", null);
//        return  cursor;
//    }


    public Cursor getAllData(String fromModule){
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE fromModule='" + fromModule +"';", null);
        return  cursor;
    }

//    public Cursor getAllSelectedData(String fromModule){
//        Cursor cursor;
//        SQLiteDatabase db = this.getWritableDatabase();
//        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE fromModule='" + fromModule +"' AND isSelect=1 AND isSelected=1 AND status=1;", null);
//        return  cursor;
//    }

    public Cursor getAllSelectedData(String fromModule){
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE fromModule='" + fromModule +"' AND isSelected=1;", null);
        return  cursor;
    }
//
//    public boolean deleteSelected(String fromModule){
//        SQLiteDatabase db  = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_21, 0);
//        contentValues.put(COL_24, 0);
//        db.update(TABLE_NAME, contentValues, "fromModule= ? AND isDraft=0", new String[]{fromModule});
//        return true;
//    }

    public Integer countSelected(String fromModule){
        int resultPrice = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor result = db.rawQuery("SELECT COUNT(id) FROM " + TABLE_NAME + " WHERE fromModule='" + fromModule + "';", null);
        if(result.moveToFirst()){
            do{
                resultPrice = Integer.parseInt(result.getString(0));
            }
            while (result.moveToNext());
        }
        return resultPrice;
    }

    public boolean deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, "id = ?", new  String[] {id});
        boolean bResult = result <= 0 ? false : true;
        return bResult;
    }

    public boolean removeData(String id){
        SQLiteDatabase db  = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_7, 0);
        db.update(TABLE_NAME, contentValues, "id= ?", new String[]{id});
        return true;
    }

    public boolean updateSelected(String id,Integer isSelected, Double actual_quantity,String fromWhse, String toWhse){
        SQLiteDatabase db  = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_7, isSelected);
        contentValues.put(COL_6, actual_quantity);
        contentValues.put(COL_19, fromWhse);
        contentValues.put(COL_20, toWhse);
        db.update(TABLE_NAME, contentValues, "id= ?", new String[]{id});
        return true;
    }

    public boolean updateActualQuantity(String id,double actual_quantity){
        SQLiteDatabase db  = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_6, actual_quantity);
        db.update(TABLE_NAME, contentValues, "id= ?", new String[]{id});
        return true;
    }

    public boolean updatePendingItemRequest(String id,double actual_quantity, String fromWhse, String toWhse){
        SQLiteDatabase db  = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_6, actual_quantity);
        contentValues.put(COL_19, fromWhse);
        contentValues.put(COL_20, toWhse);
        db.update(TABLE_NAME, contentValues, "id= ?", new String[]{id});
        return true;
    }

    public void truncateTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.execSQL("VACUUM");
    }
    public boolean checkItem(String itemName,String fromModule){
        boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("SELECT id FROM " + TABLE_NAME + " WHERE item_name= ? AND fromModule='" + fromModule + "';",new String[]{itemName});
        if(cursor.moveToFirst()){
            do{
                result = true;
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public Integer countItems(String fromModule){
        int resultPrice = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor result = db.rawQuery("SELECT COUNT(id) FROM " + TABLE_NAME + " WHERE fromModule='" + fromModule + "';", null);
        if(result.moveToFirst()){
            do{
                resultPrice = Integer.parseInt(result.getString(0));
            }
            while (result.moveToNext());
        }
        return resultPrice;
    }

//    public boolean checkItemSelected(String itemName,String fromModule){
//        boolean result = false;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor;
//        cursor = db.rawQuery("SELECT id FROM " + TABLE_NAME + " WHERE item_name= ? AND fromModule='" + fromModule + "' AND isSelect=1 AND isSelected=1;",new String[]{itemName});
//        if(cursor.moveToFirst()){
//            do{
//                result = true;
//            }
//            while (cursor.moveToNext());
//        }
//        cursor.close();
//        return result;
//    }


//    public Integer countItemsSelected(String fromModule){
//        int resultPrice = 0;
//        SQLiteDatabase db = this.getReadableDatabase();
//        @SuppressLint("Recycle") Cursor result = db.rawQuery("SELECT COUNT(id) FROM " + TABLE_NAME + " WHERE fromModule='" + fromModule + "' AND isSelected=1 AND status=1 AND isSelect=1;", null);
//        if(result.moveToFirst()){
//            do{
//                resultPrice = Integer.parseInt(result.getString(0));
//            }
//            while (result.moveToNext());
//        }
//        return resultPrice;
//    }

    public boolean checkItemSelected(String itemName,String fromModule){
        boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("SELECT id FROM " + TABLE_NAME + " WHERE item_name= ? AND fromModule='" + fromModule + "' AND isSelected=1;",new String[]{itemName});
        if(cursor.moveToFirst()){
            do{
                result = true;
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public Integer countItemsSelected(String fromModule){
        int resultPrice = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor result = db.rawQuery("SELECT COUNT(id) FROM " + TABLE_NAME + " WHERE fromModule='" + fromModule + "' AND isSelected=1;", null);
        if(result.moveToFirst()){
            do{
                resultPrice = Integer.parseInt(result.getString(0));
            }
            while (result.moveToNext());
        }
        return resultPrice;
    }


    public Integer countSAPItems(String fromModule){
        int resultPrice = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor result = db.rawQuery("SELECT COUNT(id) FROM " + TABLE_NAME + " WHERE fromModule='" + fromModule + "' AND isSelected=1;", null);
        if(result.moveToFirst()){
            do{
                resultPrice = Integer.parseInt(result.getString(0));
            }
            while (result.moveToNext());
        }
        return resultPrice;
    }
}
