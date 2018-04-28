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

import static com.qsboy.antirecall.db.WeChatDBHelper.Column_ID;
import static com.qsboy.antirecall.db.WeChatDBHelper.Column_Message;
import static com.qsboy.antirecall.db.WeChatDBHelper.Column_SubName;
import static com.qsboy.antirecall.db.WeChatDBHelper.Column_Time;
import static com.qsboy.antirecall.db.WeChatDBHelper.DB_VERSION;

public class WeChatDao {

    private static WeChatDao instance = null;
    SQLiteDatabase db = null;
    Cursor cursor = null;
    private String TAG = "WeChatDao";
    private WeChatDBHelper weChatDBHelper;

    private WeChatDao(Context context) {
        weChatDBHelper = new WeChatDBHelper(context, null, DB_VERSION);
        db = weChatDBHelper.getWritableDatabase();
    }

    public static WeChatDao getInstance(Context context) {
        if (instance == null)
            instance = new WeChatDao(context);
        return instance;
    }

    private void createTableIfNotExists(String name) {
        String sqlCreateTable = "CREATE TABLE IF NOT EXISTS " + getSafeName(name) + " (" +
                Column_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Column_SubName + " TEXT, " +
                Column_Message + " TEXT NOT NULL, " +
                Column_Time + " REAL NOT NULL)";

        db.execSQL(sqlCreateTable);
    }

    private String getSafeName(String name) {
        while (name.startsWith("'") || name.startsWith("\""))
            name = name.substring(1);
        while (name.endsWith("'") || name.endsWith("\""))
            name = name.substring(0, name.length() - 1);
        return "'" + name + "'";
    }

    public int getMaxID(String name) {
        cursor = db.rawQuery("SELECT MAX(id) FROM " + getSafeName(name), null);
        if (!cursor.moveToFirst()) {
            return 0;
        }
        return cursor.getInt(0);
    }

    public void addMessage(String name, String subName, String message) {
        this.addMessage(name, subName, message, new Date().getTime());
    }

    public void addMessage(String name, String subName, String message, long time) {
        if (subName == null || subName.equals(""))
            subName = name;
        createTableIfNotExists(name);
        ContentValues values = new ContentValues();
        values.put(Column_SubName, subName);
        values.put(Column_Message, message);
        values.put(Column_Time, time);
        db.insert(getSafeName(name), null, values);
    }

    public Messages queryById(String name, int id) {
        // SELECT * FROM tableName WHERE Id = id
        cursor = db.query(getSafeName(name),
                null,
                Column_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
        if (!cursor.moveToFirst()) {
            Log.d(TAG, "queryById: (null): " + id + " - " + name);
            return null;
        }
        String subName = cursor.getString(1);
        String message = cursor.getString(2);
        long time = cursor.getLong(3);
        Log.d(TAG, "queryById: >>>>>> " + id + " : " + subName + " - " + message);
        return new Messages(id, name, subName, message, time);
    }

    public List<String> queryAllTables() {
        List<String> list = new ArrayList<>();
        cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table'", null);
        if (!cursor.moveToFirst())
            return list;
        do {
            String name = cursor.getString(0);
            if (name != null)
                list.add(name);
        } while (cursor.moveToNext());
        return list;
    }

    public List<Messages> queryAllLastMessage(List<String> nameList) {
        List<Messages> list = new ArrayList<>();
        for (String name : nameList) {
            Log.i(TAG, "queryAllLastMessage: name: " + name);
            if ("android_metadata".equals(name) || "sqlite_sequence".equals(name))
                continue;
            cursor = db.query(getSafeName(name),
                    null,
                    Column_ID + " = ?",
                    new String[]{String.valueOf(getMaxID(name))},
                    null, null, null);
            if (!cursor.moveToFirst()) {
                Log.d(TAG, "queryByMessage: (null)");
                continue;
            }
            int id = cursor.getInt(0);
            String subName = cursor.getString(1);
            String message = cursor.getString(2);
            long time = cursor.getLong(3);

            list.add(new Messages(id, name, subName, message, time));
        }
        return list;
    }

    public void deleteMessage(String name, int id) {
        String selection = Column_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.delete(getSafeName(name), selection, selectionArgs);
    }

    public void deleteTable(int id, String name) {
        String selection = Column_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.delete(getSafeName(name), selection, selectionArgs);
    }

}
