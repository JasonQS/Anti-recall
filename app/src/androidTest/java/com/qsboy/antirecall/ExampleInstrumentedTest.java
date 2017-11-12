package com.qsboy.antirecall;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.qsboy.antirecall.db.DBHelper;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.qsboy.antirecall.db.DBHelper.TABLE_NAME;
import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.qsboy.antirecall", appContext.getPackageName());

        SQLiteDatabase db;
        SQLiteOpenHelper helper = new DBHelper(appContext, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        db = helper.getWritableDatabase();
        db.beginTransaction();
        String sql = "create table if not exists " + TABLE_NAME + " (Id integer primary key, Withdrawals text, Name text, Time text)";
        db.execSQL(sql);

    }
}
