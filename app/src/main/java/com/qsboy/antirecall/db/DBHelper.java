/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 5;
    public static final String DB_NAME = "anti-recall.db";
    public static final String Table_Prefix_WX = "wx_";
    public static final String Table_Prefix_QQ_And_Tim = "qq_";
    public static final String Table_Prefix_Group = "group_";
    public static final String Table_Recalled_Messages = "recalls";

    public static final String Column_ID = "id";
    public static final String Column_Message = "message";
    public static final String Column_Time = "time";
    public static final String Column_SubName = "sub_name";

    public static final String Column_Original_ID = "original_name";
    public static final String Column_Name = "name";
    public static final String Column_IsWX = "is_wx";
    public static final String Column_Image = "image";
    public static final String Column_Prev_Message = "prev_message";
    public static final String Column_Prev_SubName = "prev_sub_name";
    public static final String Column_Next_Message = "next_message";
    public static final String Column_Next_SubName = "next_sub_name";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreateRecallsTable = "CREATE TABLE IF NOT EXISTS " + Table_Recalled_Messages + " (" +
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

        db.execSQL(sqlCreateRecallsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("DB Helper", "onUpgrade: ");
    }
}
