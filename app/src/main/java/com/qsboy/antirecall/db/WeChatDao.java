/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import static com.qsboy.antirecall.db.WeChatDBHelper.DB_VERSION;

public class WeChatDao {

    private String TAG = "WeChatDao";
    private WeChatDBHelper weChatDBHelper;
    private static WeChatDao instance = null;

    SQLiteDatabase db = null;
    Cursor cursor = null;

    private WeChatDao(Context context) {
        weChatDBHelper = new WeChatDBHelper(context, null, DB_VERSION);
        db = weChatDBHelper.getWritableDatabase();
    }

    public static WeChatDao getInstance(Context context) {
        if (instance == null)
            instance = new WeChatDao(context);
        return instance;
    }

    public int getMaxID(String name) {
        return 0;
    }

    public Messages queryById(String name, int i) {
        return null;
    }

    public List<Messages> queryAllMessages() {
        return null;
    }
}
