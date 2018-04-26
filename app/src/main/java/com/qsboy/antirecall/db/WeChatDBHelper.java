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

public class WeChatDBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "WeChat.db";
    public static final String Table_Recalled_Messages = "recalls";

    public static final String Column_ID = "id";
    public static final String Column_Message = "message";
    public static final String Column_Time = "time";
    public static final String Column_SubName = "sub_name";

    public static final String Column_Original_ID = "original_name";
    public static final String Column_Name = "name";
    public static final String Column_Image = "image";
    public static final String Column_Prev_Message = "prev_message";
    public static final String Column_Prev_SubName = "prev_sub_name";
    public static final String Column_Next_Message = "next_message";
    public static final String Column_Next_SubName = "next_sub_name";

    public WeChatDBHelper(Context context, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreateRecallsTable = "CREATE TABLE IF NOT EXISTS " + Table_Recalled_Messages + " (" +
                Column_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Column_Name + " TEXT NOT NULL, " +
                Column_SubName + " TEXT, " +
                Column_Message + " TEXT NOT NULL, " +
                Column_Time + " REAL NOT NULL, " +
                Column_Image + " TEXT)";

        db.execSQL(sqlCreateRecallsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("DB Helper", "onUpgrade: ");
    }
}
