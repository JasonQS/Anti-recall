/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.qsboy.antirecall.R;
import com.qsboy.antirecall.db.Dao;
import com.qsboy.utils.CheckAuthority;
import com.qsboy.utils.LogcatHelper;
import com.qsboy.utils.UpdateHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import devlight.io.library.ntb.NavigationTabBar;

public class MainActivity extends AppCompatActivity {

    final String TAG = "Main Activity";

    Page1 page1;

    // TODO: 09/04/2018 加一个看聊天记录的
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogcatHelper.getInstance().start();
        Date in = new Date();
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initPage1();

        checkUpdate();

        Date out = new Date();
        Log.d(TAG, "onCreate: tvTime: " + (out.getTime() - in.getTime()));
    }

    private Page1 initPage1() {
        if (page1 != null)
            return page1;
        page1 = new Page1();

        return page1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_service) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        if (!new CheckAuthority(this).checkAlertWindowPermission()) {
            Log.i(TAG, "authorized: show warning");
            Toast.makeText(this, "请授予悬浮窗权限\n为了能正常显示撤回的消息 谢谢", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void checkUpdate() {
        //wifi环境下检查更新
        UpdateHelper helper = new UpdateHelper(this);
//        if (helper.isWifi())
        helper.checkUpdate();
    }

    // for test
    public void prepareDataForTest() {
        Date in = new Date();
        Dao dao = Dao.getInstance(this);
//        dao.deleteAll();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -9);
        for (int i = 1; i < 200; ) {
            for (int j = 1; j < 21; j++, i++) {
                dao.addMessage("Jason", "qs", false, String.valueOf(i), calendar.getTime().getTime());
                calendar.add(Calendar.MINUTE, 3);
                calendar.add(Calendar.SECOND, 3);
            }
            dao.addRecall(i, "Jason", "qs", String.valueOf(i - 1), false, calendar.getTime().getTime(), null, null, null, null, null);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        Date out = new Date();
        Log.i(TAG, "prepareDataForTest: tvTime: " + (out.getTime() - in.getTime()));
    }

}
