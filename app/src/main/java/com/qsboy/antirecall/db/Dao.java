package com.qsboy.antirecall.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.qsboy.antirecall.db.DBHelper.*;

/**
 * Created by JasonQS
 */

//@SuppressWarnings({"unused"})
public class Dao {

    private String TAG = "Dao";
    private DBHelper dbHelper;

    SQLiteDatabase db = null;
    Cursor cursor = null;

    public Dao(Context context) {
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        // TODO: 获取db对象方式待优化 可以多次读完再一起close
    }

    public void close() {
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
        String tableName = getTableName(name, isWX);
        String sqlCreateTable = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                Column_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Column_SubName + " TEXT, " +
                Column_Message + " TEXT NOT NULL, " +
                Column_Time + " TimeStamp NOT NULL DEFAULT(datetime('now','localtime')))";

        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL(sqlCreateTable);
            ContentValues values = new ContentValues();
            values.put(Column_SubName, subName);
            values.put(Column_Message, message);
            db.insert(tableName, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            close();
        }
    }

    // TODO: 查找方式待重写
    // TODO: 如果只要id的话只查询id
    public List<Messages> queryByMessage(String name, Boolean isWX, String message) {
        String tableName = getTableName(name, isWX);
        List<Messages> list = new ArrayList<>();
        // SELECT * FROM tableName WHERE Message = message
        db = dbHelper.getWritableDatabase();
        cursor = db.query(
                tableName,
                null,
                Column_Message + " = ?",
                new String[]{message},
                null,
                null,
                Column_ID + " desc");
        if (!cursor.moveToFirst())
            return null;
//     Log.i(TAG, "queryByMessage: cursor position: " + cursor.getPosition());
        while (cursor.moveToNext()) {
            Log.d(TAG, "queryByMessage: cursor position: " + cursor.getPosition());
            int id = cursor.getInt(0);
            String subName = cursor.getString(1);
            String time = cursor.getString(3);
            list.add(new Messages(id, isWX, name, subName, message, time));
        }
        close();
        return list;
    }

    public Messages queryById(String name, Boolean isWX, int id) {
        String tableName = getTableName(name, isWX);
        // SELECT * FROM tableName WHERE Id = id
        db = dbHelper.getWritableDatabase();
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
        String time = cursor.getString(3);
        close();
        return new Messages(id, isWX, tableName, subName, message, time);
    }

    public void addRecall(int originalID, String name, String subName, Boolean isWX, String message, String time) {
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Column_Original_ID, originalID);
            values.put(Column_Name, name);
            values.put(Column_SubName, subName);
            values.put(Column_Message, message);
            values.put(Column_Time, time);
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
        db = dbHelper.getWritableDatabase();
        cursor = db.query(Table_Recalled_Messages,
                null,
                null,
                null,
                null,
                null,
                null);
        if (!cursor.moveToFirst())
            return null;
        while (cursor.moveToNext()) {
            int recalledID = cursor.getInt(0);
            int id = cursor.getInt(1);
            String name = cursor.getString(2);
            String subName = cursor.getString(3);
            String message = cursor.getString(4);
            String time = cursor.getString(5);
            boolean isWX = cursor.getInt(6) == 1;
            String image = cursor.getString(7);
            Messages messages = new Messages(id, isWX, name, subName, message, time);
            messages.setRecalledID(recalledID);
            if (image != null) {
                String[] images = image.split(" ");
                messages.setImages(images);
            }
            list.add(messages);
        }
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
        db = dbHelper.getWritableDatabase();
        db.delete(tableName, selection, selectionArgs);
    }

    public void deleteRecall(int id) {
        String selection = Column_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db = dbHelper.getWritableDatabase();
        db.delete(Table_Recalled_Messages, selection, selectionArgs);
    }

    public int getMaxID(String name, Boolean isWX) {
        String tableName = getTableName(name, isWX);
        // SELECT * FROM tableName WHERE Id = id
        db = dbHelper.getWritableDatabase();
        cursor = db.rawQuery("SELECT MAX(id) FROM " + tableName, null);
        if (!cursor.moveToFirst())
            return 0;
        int id = cursor.getInt(0);
        close();
        return id;
    }

    public static String getTableName(String name, Boolean isWX) {
        String tableName;
        if (isWX)
            tableName = Table_Prefix_WX + name;
        else tableName = Table_Prefix_QQ_And_Tim + name;
        return tableName;
    }
}
