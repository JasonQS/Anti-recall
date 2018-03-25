/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.qsboy.antirecall.db.DBHelper.*;

//@SuppressWarnings({"unused"})
public class Dao {

    private String TAG = "Dao";
    private DBHelper dbHelper;

    SQLiteDatabase db = null;
    Cursor cursor = null;

    public Dao(Context context) {
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        db = dbHelper.getWritableDatabase();
        // TODO: 获取db对象方式待优化 可以多次读完再一起close
    }

    private void open() {
        if (db != null)
            db = dbHelper.getWritableDatabase();
    }

    private void close() {
        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
    }

    /**
     * @param name    联系人名字
     * @param subName 群昵称
     * @param isWX    是微信
     * @param message 消息记录
     */
    public void addMessage(String name, String subName, Boolean isWX, String message) {
        this.addMessage(name, subName, isWX, message, new Date().getTime());
    }

    public void addMessage(String name, String subName, Boolean isWX, String message, long time) {
        String tableName = getTableName(name, isWX);
        String sqlCreateTable = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                Column_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Column_SubName + " TEXT, " +
                Column_Message + " TEXT NOT NULL, " +
                Column_Time + " REAL NOT NULL)";

        open();
        if (subName == null || subName.equals(""))
            subName = name;
        db.beginTransaction();
        try {
            db.execSQL(sqlCreateTable);
            ContentValues values = new ContentValues();
            values.put(Column_SubName, subName);
            values.put(Column_Message, message);
            values.put(Column_Time, time);
            db.insert(tableName, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            close();
        }
    }

    // TODO: 查找方式待重写
    // TODO: 如果只要id的话只查询id
    public int queryByMessage(String name, Boolean isWX, String subName, String message) {
        Log.d(TAG, "queryByMessage: name = " + name + " subName = " + subName + " message = " + message);
        String tableName = getTableName(name, isWX);
        // SELECT * FROM tableName WHERE Message = message
        open();
        cursor = db.query(
                tableName,
                new String[]{Column_ID},
                Column_Message + " = ? and " + Column_SubName + " = ?",
                new String[]{message, subName},
                null,
                null,
                Column_ID + " desc");
        if (!cursor.moveToFirst())
            return 0;
        int id = cursor.getInt(0);
        close();
        return id;
    }

    public Messages queryById(String name, Boolean isWX, int id) {
        Log.d(TAG, "queryById: name = " + name + " id = " + id);
        String tableName = getTableName(name, isWX);
        // SELECT * FROM tableName WHERE Id = id
        open();
        cursor = db.query(tableName,
                null,
                Column_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
        if (!cursor.moveToFirst())
            return null;
//        Log.i(TAG, "queryById: id: " + id);
        String subName = cursor.getString(1);
        String message = cursor.getString(2);
        long time = cursor.getLong(3);
        close();
        return new Messages(id, isWX, name, subName, message, time);
    }

    public void addRecall(Messages messages, String nextMessage, String prevMessage, String nextSubName, String prevSubName) {
        this.addRecall(messages.getId(), messages.getName(), messages.getSubName(), messages.isWX(), messages.getMessage(), messages.getTime(),
                prevSubName, prevMessage, nextSubName, nextMessage);

    }

    public void addRecall(int originalID, String name, String subName, Boolean isWX, String message, long time,
                          String prevSubName, String prevMessage, String nextSubName, String nextMessage) {
        Log.d(TAG, "addRecall() called with: originalID = [" + originalID + "], name = [" + name + "], subName = [" + subName + "], isWX = [" + isWX + "], message = [" + message + "], time = [" + time + "], prevSubName = [" + prevSubName + "], prevMessage = [" + prevMessage + "], nextSubName = [" + nextSubName + "], nextMessage = [" + nextMessage + "]");
        open();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Column_Original_ID, originalID);
            values.put(Column_Name, name);
            values.put(Column_SubName, subName);
            values.put(Column_Message, message);
            values.put(Column_Time, time);
            values.put(Column_Prev_SubName, prevSubName);
            values.put(Column_Prev_Message, prevMessage);
            values.put(Column_Next_SubName, nextSubName);
            values.put(Column_Next_Message, nextMessage);
            if (isWX)
                values.put(Column_IsWX, 1);
            else
                values.put(Column_IsWX, 0);
            // TODO: 把查找到的图片也加进去 list用空格分割
            db.insert(Table_Recalled_Messages, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            close();
        }
    }

    public List<Messages> queryAllRecalls() {
        List<Messages> list = new ArrayList<>();
        // SELECT * FROM Table_Recalled_Messages
        open();
        cursor = db.query(Table_Recalled_Messages,
                null,
                null,
                null,
                null,
                null,
                Column_ID + " DESC");
        if (!cursor.moveToFirst())
            return null;
        do {
            int recalledID = cursor.getInt(0);
            int id = cursor.getInt(1);
            String name = cursor.getString(2);
            String subName = cursor.getString(3);
            String message = cursor.getString(4);
            long time = cursor.getLong(5);
            boolean isWX = cursor.getInt(6) == 1;
            String image = cursor.getString(7);
            Messages messages = new Messages(id, isWX, name, subName, message, time);
            messages.setRecalledID(recalledID);
            if (image != null) {
                String[] images = image.split(" ");
                messages.setImages(images);
            }
            list.add(messages);
        } while (cursor.moveToNext());
        close();
        return list;
    }

//    public Messages queryRecallsMyId(String name, Boolean isWX, int id) {
//
//    }

    public void deleteMessage(String name, Boolean isWX, int id) {
        String tableName = getTableName(name, isWX);
        String selection = Column_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        open();
        db.delete(tableName, selection, selectionArgs);
        close();
    }

    public void deleteRecall(int id) {
        String selection = Column_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        open();
        db.delete(Table_Recalled_Messages, selection, selectionArgs);
        close();
    }

    public void deleteAll() {
        String tableName = getTableName("Jason", false);
        open();
        try {
            int delete1 = db.delete(Table_Recalled_Messages, null, null);
            int delete2 = db.delete("sqlite_sequence", null, null);
            int delete3 = db.delete(tableName, null, null);
            Log.i(TAG, "deleteAll: " + delete1);
            Log.i(TAG, "deleteAll: " + delete2);
            Log.i(TAG, "deleteAll: " + delete3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        close();
    }

    public int getMaxID(String name, Boolean isWX) {
        String tableName = getTableName(name, isWX);
        // SELECT * FROM tableName WHERE Id = id
        open();
        cursor = db.rawQuery("SELECT MAX(id) FROM " + tableName, null);
        if (!cursor.moveToFirst())
            return 0;
        int id = cursor.getInt(0);
        close();
        return id;
    }

//    public List<String> getTables() {
//        List<String> list = new ArrayList<>();
//        cursor = db.rawQuery("SELECT 'name' FROM 'sqlite_master' WHERE type = 'table'", null);
//        if (!cursor.moveToFirst())
//            return list;
//        do {
//            String table = cursor.getString(0);
//            list.add(table);
//        } while (cursor.moveToNext());
//        close();
//        return list;
//    }

    public static String getTableName(String name, Boolean isWX) {
        String tableName;
        if (isWX)
            tableName = Table_Prefix_WX + name;
        else tableName = Table_Prefix_QQ_And_Tim + name;
        return "\"" + tableName + "\"";
    }

    public void temp() {
        String sql;
        sql = "DROP TABLE " + Table_Recalled_Messages;
        db.execSQL(sql);
//        sql = "ALTER TABLE " + Table_Recalled_Messages + " RENAME TO " + Table_Recalled_Messages + "_";
//        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS " + Table_Recalled_Messages + " (" +
                Column_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Column_Original_ID + " INTEGER NOT NULL, " +
                Column_Name + " TEXT NOT NULL, " +
                Column_SubName + " TEXT, " +
                Column_Message + " TEXT NOT NULL, " +
                Column_Time + " REAL NOT NULL, " +
                Column_IsWX + " TEXT NOT NULL, " +
                Column_Image + " TEXT, " +
                Column_Prev_SubName + " TEXT, " +
                Column_Prev_Message + " TEXT, " +
                Column_Next_SubName + " TEXT, " +
                Column_Next_Message + " TEXT)";
        db.execSQL(sql);
//        sql = "INSERT INTO " + Table_Recalled_Messages + " SELECT * FROM " + Table_Recalled_Messages + "_";
//        db.execSQL(sql);

    }
}
