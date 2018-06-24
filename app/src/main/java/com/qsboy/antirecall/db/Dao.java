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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.qsboy.antirecall.db.DBHelper.Column_ID;
import static com.qsboy.antirecall.db.DBHelper.Column_Image;
import static com.qsboy.antirecall.db.DBHelper.Column_Message;
import static com.qsboy.antirecall.db.DBHelper.Column_Name;
import static com.qsboy.antirecall.db.DBHelper.Column_Next_Message;
import static com.qsboy.antirecall.db.DBHelper.Column_Next_SubName;
import static com.qsboy.antirecall.db.DBHelper.Column_Original_ID;
import static com.qsboy.antirecall.db.DBHelper.Column_Prev_Message;
import static com.qsboy.antirecall.db.DBHelper.Column_Prev_SubName;
import static com.qsboy.antirecall.db.DBHelper.Column_SubName;
import static com.qsboy.antirecall.db.DBHelper.Column_Time;
import static com.qsboy.antirecall.db.DBHelper.Table_Recalled_Messages;

public class Dao {

    public static String DB_NAME_QQ = "QQ.db";
    public static String DB_NAME_WE_CHAT = "WeChat.db";
    private static String TAG = "Dao";
    private static Dao instanceQQ = null;
    private static Dao instanceWeChat = null;
    private Cursor cursor;
    private SQLiteDatabase db;
    private HashMap<String, Boolean> existTables = new HashMap<>();

    private Dao(DBHelper dbHelper) {
        db = dbHelper.getWritableDatabase();
    }

    public static Dao getInstance(Context context, String dbName) {
        if (DB_NAME_QQ.equals(dbName)) {
            TAG = "QQ Dao";
            if (instanceQQ == null)
                instanceQQ = new Dao(new DBHelper(context, DB_NAME_QQ, null, 6));
            return instanceQQ;
        }
        if (DB_NAME_WE_CHAT.equals(dbName)) {
            TAG = "WeChat Dao";
            if (instanceWeChat == null)
                instanceWeChat = new Dao(new DBHelper(context, DB_NAME_WE_CHAT, null, 6));
            return instanceWeChat;
        }
        return new Dao(new DBHelper(context, DB_NAME_QQ, null, 1));
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
        if (!existTable(name))
            return 0;
        cursor = db.rawQuery("SELECT MAX(id) FROM " + getSafeName(name), null);
        if (!cursor.moveToFirst()) {
            return 0;
        }
        int maxID = cursor.getInt(0);
        Log.i(TAG, "getMaxID: " + maxID);
        return maxID;
    }

    /**
     * @param name    联系人名字
     * @param subName 群昵称
     * @param message 消息记录
     */
    public long addMessage(String name, String subName, String message) {
        return this.addMessage(name, subName, message, new Date().getTime());
    }

    public long addMessage(String name, String subName, String message, long time) {
        if (subName == null || subName.equals(""))
            subName = name;
        createTableIfNotExists(name);
        ContentValues values = new ContentValues();
        values.put(Column_SubName, subName);
        values.put(Column_Message, message);
        values.put(Column_Time, time);
        return db.insert(getSafeName(name), null, values);
    }

    public void addRecall(Messages messages) {
        this.addRecall(messages.getId(), messages.getName(), messages.getSubName(), messages.getMessage(), messages.getTime(), messages.getImages());
    }

    public void addRecall(int originalID, String name, String subName, String message, long time, String images) {
        Log.d(TAG, "addRecall() called with: originalID = [" + originalID + "], name = [" + name + "], subName = [" + subName + "], message = [" + message + "], time = [" + time + "]");

        ContentValues values = new ContentValues();
        values.put(Column_Original_ID, originalID);
        values.put(Column_Name, name);
        values.put(Column_SubName, subName);
        values.put(Column_Message, message);
        values.put(Column_Time, time);
        values.put(Column_Image, images);

        db.insert(Table_Recalled_Messages, null, values);
    }

    public void addRecall(Messages messages, String nextMessage, String prevMessage, String nextSubName, String prevSubName) {
        this.addRecall(messages.getId(), messages.getName(), messages.getSubName(), messages.getMessage(), messages.getTime(), messages.getImages(),
                prevSubName, prevMessage, nextSubName, nextMessage);
    }

    public void addRecall(int originalID, String name, String subName, String message, long time, String images,
                          String prevSubName, String prevMessage, String nextSubName, String nextMessage) {
        Log.d(TAG, "addRecall() called with: originalID = [" + originalID + "], name = [" + name + "], subName = [" + subName + "], message = [" + message + "], time = [" + time + "], prevSubName = [" + prevSubName + "], prevMessage = [" + prevMessage + "], nextSubName = [" + nextSubName + "], nextMessage = [" + nextMessage + "]");

        ContentValues values = new ContentValues();
        values.put(Column_Original_ID, originalID);
        values.put(Column_Name, name);
        values.put(Column_SubName, subName);
        values.put(Column_Message, message);
        values.put(Column_Time, time);
        values.put(Column_Image, images);
        values.put(Column_Prev_SubName, prevSubName);
        values.put(Column_Prev_Message, prevMessage);
        values.put(Column_Next_SubName, nextSubName);
        values.put(Column_Next_Message, nextMessage);

        db.insert(Table_Recalled_Messages, null, values);
    }

    public ArrayList<Integer> queryByMessage(String name, String subName, String message) {
        Log.d(TAG, "queryByMessage: " + name + " - " + subName + " : " + message);
        ArrayList<Integer> list = new ArrayList<>();
        if (message == null || subName == null || name == null)
            return list;
        if (!existTable(name))
            return list;
        cursor = db.query(getSafeName(name),
                new String[]{Column_ID},
                Column_Message + " = ? and " + Column_SubName + " = ?",
                new String[]{message, subName},
                null,
                null,
                Column_ID + " desc");
        if (!cursor.moveToFirst()) {
            Log.d(TAG, "queryByMessage: (null)");
            return list;
        }
        do {
            list.add(cursor.getInt(0));
        } while (cursor.moveToNext());
        Log.d(TAG, "queryByMessage: >>>>>> " + list);
        return list;
    }

    public Messages queryById(String name, int id) {
        // SELECT * FROM tableName WHERE Id = id
        if (!existTable(name))
            return null;
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

    public Messages queryRecallById(int position) {
        if (!existTable(Table_Recalled_Messages))
            return null;
        cursor = db.query(Table_Recalled_Messages,
                null,
                Column_ID + " = ?",
                new String[]{String.valueOf(position)},
                null,
                null,
                null);
        if (!cursor.moveToFirst()) {
            Log.d(TAG, "queryById: (null): " + position);
            return null;
        }
        int recalledID = cursor.getInt(0);
        int id = cursor.getInt(1);
        String name = cursor.getString(2);
        String subName = cursor.getString(3);
        String message = cursor.getString(4);
        long time = cursor.getLong(5);
        String image = cursor.getString(6);
        Messages messages = new Messages(id, name, subName, message, time);
        messages.setRecalledID(recalledID);
        messages.setImages(image);
        Log.d(TAG, "queryRecalls: >>>>>> " + position);
        return messages;
    }

    public List<Messages> queryAllRecalls() {
        List<Messages> list = new ArrayList<>();
        // SELECT * FROM Table_Recalled_Messages
        if (!existTable(Table_Recalled_Messages))
            return list;
        cursor = db.query(Table_Recalled_Messages,
                null,
                null,
                null,
                null,
                null,
                Column_ID + " DESC");
        if (!cursor.moveToFirst()) {
            Log.d(TAG, "queryAllRecalls: (null)");
            return null;
        }
        do {
            int recalledID = cursor.getInt(0);
            int id = cursor.getInt(1);
            String name = cursor.getString(2);
            String subName = cursor.getString(3);
            String message = cursor.getString(4);
            long time = cursor.getLong(5);
            String image = cursor.getString(6);
            Messages messages = new Messages(id, name, subName, message, time);
            messages.setRecalledID(recalledID);
            messages.setImages(image);
            list.add(messages);
        } while (cursor.moveToNext());
        Log.d(TAG, "queryAllRecalls: >>>>>> " + list);
        return list;
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

    public List<Messages> queryAllTheLastMessage(List<String> nameList) {
        List<Messages> list = new ArrayList<>();
        for (String name : nameList) {
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
            Collections.sort(list, (o1, o2) -> {
                long time1 = o1.getTime();
                long time2 = o2.getTime();
                return Long.compare(time2, time1);
            });
        }
        return list;
    }

    public boolean existTable(String name) {
        if (existTables.containsKey(name))
            if (existTables.get(name))
                return true;
        cursor = db.rawQuery("SELECT count(*) FROM sqlite_master WHERE type = 'table' AND name = " + getSafeName(name), null);
        if (!cursor.moveToFirst()) return false;
        int count = cursor.getInt(0);
        existTables.put(name, count > 0);
        return count > 0;
    }

    public boolean existMessage(String name, String message, String prevMessage, String subName, String prevSubName) {
        if (prevMessage == null || prevSubName == null)
            return false;
        if (!existTable(name))
            return false;
        cursor = db.query(getSafeName(name),
                new String[]{Column_ID},
                Column_Message + " = ? and " + Column_SubName + " = ?",
                new String[]{message, subName},
                null, null,
                Column_ID + " DESC");
        // 如果不存在上一条
        if (!cursor.moveToFirst())
            return false;
        int idMsg = cursor.getInt(0);
        cursor = db.query(getSafeName(name),
                new String[]{Column_ID},
                Column_Message + " = ? and " + Column_SubName + " = ?",
                new String[]{prevMessage, prevSubName},
                null, null,
                Column_ID + " DESC");

        if (!cursor.moveToFirst())
            return false;
        int idPreMsg = cursor.getInt(0);
        return idMsg - idPreMsg == 1;
    }

    public boolean existRecall(Messages messages) {
        if (!existTable(Table_Recalled_Messages))
            return false;
        cursor = db.query(Table_Recalled_Messages,
                new String[]{Column_ID},
                Column_Original_ID + " = ? and " +
                        Column_Name + " = ? and " +
                        Column_SubName + " = ? and " +
                        Column_Message + " = ? and " +
                        Column_Time + " = ?",
                new String[]{String.valueOf(
                        messages.getId()),
                        messages.getName(),
                        messages.getSubName(),
                        messages.getMessage(),
                        String.valueOf(messages.getTime())},
                null, null, null);
        return cursor.moveToFirst();
    }

    public void deleteMessage(String name, int id) {
        String selection = Column_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.delete(getSafeName(name), selection, selectionArgs);
    }

    public void deleteRecall(int id) {
        String selection = Column_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.delete(Table_Recalled_Messages, selection, selectionArgs);
    }

    public void deleteTable(int id, String name) {
        String selection = Column_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.delete(getSafeName(name), selection, selectionArgs);
    }

    public void deleteAll() {
        try {
            int delete1 = db.delete(Table_Recalled_Messages, null, null);
            int delete2 = db.delete("sqlite_sequence", null, null);
            int delete3 = db.delete("Jason", null, null);
            Log.i(TAG, "deleteAll: " + delete1);
            Log.i(TAG, "deleteAll: " + delete2);
            Log.i(TAG, "deleteAll: " + delete3);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
