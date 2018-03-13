package com.qsboy.antirecall;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.qsboy.antirecall.db.Dao;
import com.qsboy.antirecall.db.Messages;
import com.qsboy.antirecall.ui.FoldingCellAdapter;
import com.qsboy.antirecall.ui.MultiMessagesAdapter;
import com.qsboy.antirecall.utils.CheckAuthority;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;
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
        setContentView(R.layout.activity_main);

//        mTextMessage = findViewById(R.id.message);
//        BottomNavigationView navigation = findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (!new CheckAuthority(this).checkAlertWindowPermission()) {
            Log.i(TAG, "authorized: show warning");
            Toast.makeText(this, "为显示撤回的消息\n请授予悬浮窗权限", Toast.LENGTH_LONG).show();
        }

//        prepareDataForTest();

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
        Dao dao = new Dao(this);
        dao.addMessage("Jason", "qs", false, "a");
        dao.addMessage("Jason", "qs", false, "b");
        dao.addMessage("Jason", "qs", false, "c");
        dao.addMessage("Jason", "qs", false, "d");
        dao.addMessage("Jason", "qs", false, "e");
        dao.addMessage("Jason", "qs", false, "f");
        dao.addMessage("Jason", "qs", false, "g");
        dao.addMessage("Jason", "qs", false, "h");
        dao.addMessage("Jason", "qs", false, "i");
        dao.addMessage("Jason", "qs", false, "j");
        dao.addMessage("Jason", "qs", false, "hello");
        dao.addMessage("Jason", "qs", false, "world");
        dao.addMessage("Jason", "qs", false, "0");
        dao.addMessage("Jason", "qs", false, "1");
        dao.addMessage("Jason", "qs", false, "2");
        dao.addMessage("Jason", "qs", false, "3");
        dao.addMessage("Jason", "qs", false, "4");
        dao.addMessage("Jason", "qs", false, "5");
        dao.addMessage("Jason", "qs", false, "6");
        dao.addMessage("Jason", "qs", false, "7");
        dao.addMessage("Jason", "qs", false, "8");
        dao.addMessage("Jason", "qs", false, "9");
        List<Messages> ids = dao.queryByMessage("Jason", false, "hello");
        Log.i(TAG, "prepareDataForTest: ids: " + ids);
        for (Messages messages : ids)
            dao.addRecall(
                    messages.getId(),
                    messages.getName(),
                    messages.getSubName(),
                    messages.isWX(),
                    messages.getMessage(),
                    messages.getTime());
    }

}
