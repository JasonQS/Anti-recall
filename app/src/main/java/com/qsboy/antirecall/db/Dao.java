package com.qsboy.antirecall.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.qsboy.antirecall.db.DBHelper.Column_Name_ID;
import static com.qsboy.antirecall.db.DBHelper.Column_Name_Message;
import static com.qsboy.antirecall.db.DBHelper.Column_Name_SubName;
import static com.qsboy.antirecall.db.DBHelper.Column_Name_Time;
import static com.qsboy.antirecall.db.DBHelper.Table_Name_Prefix_QQ_And_Tim;
import static com.qsboy.antirecall.db.DBHelper.Table_Name_Prefix_WX;

/**
 * Created by JasonQS
 */

@SuppressWarnings({"unused"})
public class Dao {

    private String TAG = "Dao";
    private DBHelper dbHelper;

    SQLiteDatabase db = null;
    Cursor cursor = null;

    public Dao(Context context) {
        dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
    }

    public void close() {
        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
    }

    public void addMessage(String name, String subName, Boolean isWX, String message) {
        String tableName = getTableName(name, isWX);
        this.addMessage(tableName, subName, isWX, message, new Date().getTime());
    }

    /**
     * @param name    联系人名字
     * @param subName 群昵称
     * @param isWX    是微信
     * @param message 消息记录
     * @param time    时间
     */
    public void addMessage(String name, String subName, Boolean isWX, String message, long time) {
        String tableName = getTableName(name, isWX);
        String sqlCreateTable;

        sqlCreateTable = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                Column_Name_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Column_Name_SubName + " TEXT, " +
                Column_Name_Message + " TEXT NOT NULL, " +
                Column_Name_Time + " INTEGER NOT NULL)";
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL(sqlCreateTable);
            ContentValues values = new ContentValues();
            values.put(Column_Name_Message, message);
            values.put(Column_Name_Time, time);
            if (subName != null)
                values.put(Column_Name_SubName, subName);
            long newRowId = db.insert(tableName, null, values);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Integer> queryByMessage(String name, Boolean isWX, String message) {
        String tableName = getTableName(name, isWX);
        List<Integer> list = new ArrayList<>();
        // SELECT * FROM tableName WHERE Message = message
        cursor = db.query(
                tableName,
                null,
                Column_Name_Message + " = ?",
                new String[]{message},
                null,
                null,
                Column_Name_ID + " desc");
        Log.i(TAG, "queryByMessage: cursor position: " + cursor.getPosition());
        while (cursor.moveToNext()) {
            Log.d(TAG, "queryByMessage: cursor position: " + cursor.getPosition());
            list.add(cursor.getInt(0));
        }
        return list;
    }

    public Messages queryById(String name, Boolean isWX, int id) {
        String tableName = getTableName(name, isWX);
        // SELECT * FROM tableName WHERE Id = id
        cursor = db.query(tableName,
                null,
                Column_Name_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
        if (cursor.getCount() == 0)
            return null;
        //如果没有subName
        String message = cursor.getString(1);
        String time = cursor.getString(2);
        String subName = cursor.getString(0);

        if (cursor.getColumnIndex(Column_Name_SubName) == -1) {
            return new Messages(id, isWX, tableName, subName, message, time);
        } else return new Messages(id, isWX, tableName, null, message, time);

    }

    public void deleteMessage(String name, Boolean isWX, int id) {
        String tableName = getTableName(name, isWX);
        String selection = Column_Name_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.delete(tableName, selection, selectionArgs);
    }

    public static String getTableName(String name, Boolean isWX) {
        String tableName;
        if (isWX)
            tableName = Table_Name_Prefix_WX + name;
        else tableName = Table_Name_Prefix_QQ_And_Tim + name;
        return tableName;
    }
}
