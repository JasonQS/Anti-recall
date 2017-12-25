package com.qsboy.antirecall.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.qsboy.antirecall.db.DBHelper.*;

/**
 * Created by JasonQS
 */

public class Dao {

    private String TAG = "Dao";
    private Context context;
    private DBHelper dbHelper;

    SQLiteDatabase db = null;
    Cursor cursor = null;

    public Dao(Context context) {
        this.context = context;
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

    public void addMessage(String tableName, String subName, Boolean isWX, String message) {
        if (isWX)
            tableName = Table_Name_Prefix_WX + tableName;
        else tableName = Table_Name_Prefix_QQ_And_Tim + tableName;

        this.addMessage(tableName, subName, isWX, message, new Date().getTime());
    }

    /**
     * @param tableName 联系人名字
     * @param subName   群昵称
     * @param isWX      是微信
     * @param message   消息记录
     * @param time      时间
     */
    private void addMessage(String tableName, String subName, Boolean isWX, String message, long time) {
        if (isWX)
            tableName = Table_Name_Prefix_WX + tableName;
        else tableName = Table_Name_Prefix_QQ_And_Tim + tableName;

        try {
            String sqlCreateTable;
            if (subName == null)
                sqlCreateTable = "CREATE TABLE IF NOT EXISTS " +
                        tableName + " (" +
                        Column_Name_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Column_Name_Message + " TEXT, " +
                        Column_Name_Time + " INTEGER)";
            else
                sqlCreateTable = "CREATE TABLE IF NOT EXISTS " +
                        Table_Name_Prefix_Group + tableName + " (" +
                        Column_Name_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Column_Name_SubName + " TEXT, " +
                        Column_Name_Message + " TEXT, " +
                        Column_Name_Time + " INTEGER)";

            String sqlInsert = "INSERT INTO" + tableName + " (" +
                    Column_Name_Message + "," +
                    Column_Name_Time + ") " +
                    "VALUES ('" + message + "'," + time + ")";

            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            db.execSQL(sqlCreateTable);
            db.execSQL(sqlInsert);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            // TODO: 17/12/2017 db.close
        }
    }

    public boolean insertData(String tableName, String subName, String message, long time) {

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            ContentValues contentValues = new ContentValues();
            contentValues.put(Column_Name_Message, message);
            contentValues.put(Column_Name_SubName, subName);
            contentValues.put(Column_Name_Time, time);
            db.insertOrThrow(tableName, null, contentValues);

            db.setTransactionSuccessful();
            return true;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }


    public List<Integer> queryMessage(String tableName, String message) {
        List<Integer> ids = new ArrayList<>();

        // SELECT * FROM tableName WHERE Message = message
        cursor = db.query(tableName, null, Column_Name_Message + " = ?", new String[]{message}, null, null, Column_Name_ID + " desc");
        Log.i(TAG, "queryMessage: cursor position: " + cursor.getPosition());
        while (cursor.moveToNext()) {
            Log.d(TAG, "queryMessage: cursor position: " + cursor.getPosition());
            ids.add(cursor.getInt(0));
        }
        return ids;
    }

    public String queryId(String tableName, int id) {
        // SELECT * FROM tableName WHERE Id = id
        cursor = db.query(tableName, null, "Id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.getCount() == 0)
            return null;
        return cursor.getString(2);

    }

}
