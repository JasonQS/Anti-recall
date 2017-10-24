package com.qsboy.antirecall.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by JasonQS
 */

public class Dao {

    private Context context;
    private DBHelper helper;
    private OnQueryListener onQuery;

    SQLiteDatabase db = null;
    Cursor cursor = null;

    public Dao(Context context) {
        this.context = context;
        helper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
    }

    public void close() {
        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
    }

    public void addMessage(String tableName, String message, int time) {

        String sqlCreateTable = "CREATE TABLE IF NOT EXISTS " + tableName
                + " (Id integer primary key autoincrement, Message text, Time integer)";

        String sqlInsert = "INSERT INTO" + tableName
                + " (Message, Time) VALUES ('" + message + "'," + time + ")";

        db = helper.getWritableDatabase();
        db.beginTransaction();

        db.execSQL(sqlCreateTable);
        db.execSQL(sqlInsert);

        db.setTransactionSuccessful();
    }

    public void setOnQueryListener(OnQueryListener onQuery) {
        this.onQuery = onQuery;
    }

    public interface OnQueryListener {
        void onNoResult();

        boolean onNext();
    }

    public void queryMessage(String tableName, String message) {

        // SELECT * FROM tableName WHERE Message = message
        cursor = db.query(tableName, null, "Message = ?", new String[]{message}, null, null, null);

        if (cursor.getCount() == 0) {
            onQuery.onNoResult();
            return;
        }

        while (cursor.moveToNext()) {
            if (!onQuery.onNext())
                break;
        }
    }

    public String queryId(String tableName, int id) {
        // SELECT * FROM tableName WHERE Id = id
        cursor = db.query(tableName, null, "Id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.getCount() == 0)
            return null;
        return cursor.getString(2);

    }

}
