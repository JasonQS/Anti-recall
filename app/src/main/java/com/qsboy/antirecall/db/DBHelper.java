package com.qsboy.antirecall.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JasonQS
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
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
                Column_Time + " TimeStamp NOT NULL, " +
                Column_IsWX + " TEXT NOT NULL, " +
                Column_Image + " TEXT)";

        String sqlCreateJason = "CREATE TABLE IF NOT EXISTS " + "Jason" + " (" +
                Column_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Column_SubName + " TEXT, " +
                Column_Message + " TEXT NOT NULL, " +
                Column_Time + " TimeStamp NOT NULL DEFAULT(datetime('now','localtime')))";

        db.execSQL(sqlCreateRecallsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

}
