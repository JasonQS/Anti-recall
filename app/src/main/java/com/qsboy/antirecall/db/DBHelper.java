package com.qsboy.antirecall.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JasonQS
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "messages.db";
    public static final String Table_Name_Prefix_WX = "wx_";
    public static final String Table_Name_Prefix_QQ_And_Tim = "qq_";
    public static final String Table_Name_Prefix_Group = "group_";
    public static final String Table_Name_Recalled_Message = "recalled";

    public static final String Column_Name_ID = "id";
    public static final String Column_Name_Message = "message";
    public static final String Column_Name_Time = "time";
    public static final String Column_Name_SubName = "sub_name";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " +
                "recalled" + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "withdrawals TEXT, " +
                "name TEXT, " +
                "sub_name TEXT, " +
                "id INTEGER, " +
                "time INTEGER)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

}
