/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall;

import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.qsboy.antirecall.db.Dao;
import com.qsboy.antirecall.db.Messages;
import com.qsboy.antirecall.ui.FoldingCellAdapter;
import com.qsboy.utils.CheckAuthority;
import com.qsboy.utils.XToast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final String TAG = "Main Activity";

    RecyclerView recyclerView;
    FoldingCellAdapter foldingCellAdapter;

//    private TextView mTextMessage;
//
//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
//                    return true;
//                case R.id.navigation_monitor:
//                    mTextMessage.setText(R.string.title_monitor);
//                    return true;
//                case R.id.navigation_setting:
//                    mTextMessage.setText(R.string.title_setting);
//                    // 跳转到辅助功能的设置
//                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//                    startActivity(intent);
//                    return true;
//                default:
//                    return false;
//            }
//        }
//    };

    //todo 查找的时候加个program bar提示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Date in = new Date();
        setContentView(R.layout.activity_main);

//        mTextMessage = findViewById(R.id.message);
//        BottomNavigationView navigation = findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (!new CheckAuthority(this).checkAlertWindowPermission()) {
            Log.i(TAG, "authorized: show warning");
            Toast.makeText(this, "为显示撤回的消息\n请授予悬浮窗权限", Toast.LENGTH_LONG).show();
        }

//        prepareDataForTest();
        new Handler().postDelayed(() -> XToast.makeText(this, "hello").show(), 500);
        new Handler().postDelayed(() -> Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show(), 500);
        new Handler().postDelayed(() -> XToast.makeText(this, "hello").show(), 4000);
        new Handler().postDelayed(() -> Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show(), 4000);
        new Handler().postDelayed(() -> XToast.makeText(this, "hello").show(), 8000);
        new Handler().postDelayed(() -> Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show(), 8000);
        new Handler().postDelayed(() -> XToast.makeText(this, "hello").show(), 12000);
        new Handler().postDelayed(() -> Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show(), 12000);
        new Handler().postDelayed(() -> XToast.makeText(this, "hello").show(), 16000);
        new Handler().postDelayed(() -> Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show(), 16000);
        new Handler().postDelayed(() -> XToast.makeText(this, "hello").show(), 20000);
        new Handler().postDelayed(() -> Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show(), 20000);

        foldingCellAdapter = new FoldingCellAdapter(null, this);
        List<Messages> messages = foldingCellAdapter.prepareData();
        if (messages.size() != 0)
            foldingCellAdapter.addData(messages);
        recyclerView = findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(foldingCellAdapter);
//        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(foldingCellAdapter);
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);

        // 开启滑动删除
//        foldingCellAdapter.enableSwipeItem();
//        foldingCellAdapter.setOnItemSwipeListener(onItemSwipeListener);

        Date out = new Date();
        Log.i(TAG, "onCreate: time: " + (out.getTime() - in.getTime()));
    }

    OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
        int pos = 0;

        @Override
        public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
            this.pos = pos;
            Log.i(TAG, "onItemSwipeStart: pos:" + pos);
        }

        @Override
        public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
            Log.i(TAG, "clearView: pos: " + pos);
            Dao dao = new Dao(getApplicationContext());
            dao.deleteRecall(foldingCellAdapter.getData().get(this.pos).getRecalledID());
            System.out.println(foldingCellAdapter.getData());
        }

        @Override
        public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
            Log.i(TAG, "onItemSwiped: pos: " + pos);
        }

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

        }
    };

    // TODO: for test
    public void prepareDataForTest() {
        Date in = new Date();
        Dao dao = new Dao(this);
        dao.deleteAll();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -9);
        for (int i = 1; i < 200; ) {
            for (int j = 1; j < 21; j++, i++) {
                dao.addMessage("Jason", "qs", false, String.valueOf(i), calendar.getTime().getTime());
                calendar.add(Calendar.MINUTE, 3);
                calendar.add(Calendar.SECOND, 3);
            }
            dao.addRecall(i, "Jason", "qs", false, String.valueOf(i - 1), calendar.getTime().getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        Date out = new Date();
        Log.i(TAG, "prepareDataForTest: time: " + (out.getTime() - in.getTime()));
    }

}
