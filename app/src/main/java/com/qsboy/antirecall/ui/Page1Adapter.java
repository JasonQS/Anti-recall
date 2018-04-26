/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.qsboy.antirecall.R;
import com.qsboy.antirecall.db.QQDBHelper;
import com.qsboy.antirecall.db.QQDao;
import com.qsboy.antirecall.db.Messages;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Calendar.*;

public class Page1Adapter extends BaseItemDraggableAdapter<Messages, BaseViewHolder> {

    String TAG = "Page1Adapter";

    Context context;
    QQDao dao;
    Messages data;
    Calendar now = Calendar.getInstance();
    Calendar calendar = Calendar.getInstance();

    SimpleDateFormat sdfDate = new SimpleDateFormat("MM - dd", Locale.getDefault());
    SimpleDateFormat sdfL = new SimpleDateFormat("MM-dd\nHH:mm", Locale.getDefault());
    SimpleDateFormat sdfS = new SimpleDateFormat("HH:mm", Locale.getDefault());
    static DividerItemDecoration decor;
    long down = 0;

    public Page1Adapter(@Nullable List<Messages> data, Context context) {
        super(R.layout.cell, data);
        this.context = context;
        dao = QQDao.getInstance(context);
        decor = new DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL);
    }

    @Override
    protected void convert(BaseViewHolder helper, Messages item) {
        Log.v(TAG, "convert: " + item.getMessage() + " id: " + item.getId());
        MyFoldingCell fc = helper.getView(R.id.folding_cell);
        RecyclerView recyclerView = helper.getView(R.id.cell_recycler_view);
        MultiMessagesAdapter adapter = new MultiMessagesAdapter(null, context);

        helper.setText(R.id.cell_title_fold, item.getName());
        helper.setText(R.id.cell_title_unfold, item.getName());
        helper.setText(R.id.cell_name, item.getSubName());
        helper.setText(R.id.cell_time, formatTime(item.getTime()));
        helper.setText(R.id.cell_message_text, item.getMessage());

        final int[] top = {item.getId()};
        final int[] bot = {item.getId(), 1};
        int max = dao.getMaxID(item.getName());
        adapter.addData(item);
        adapter.setStartUpFetchPosition(3);
        adapter.setUpFetchEnable(true);
        adapter.setPreLoadNumber(4);
        adapter.setEnableLoadMore(true);
        adapter.setOnDateChangeListener(date -> helper.setText(R.id.cell_title_date, formatDate(date)));
        adapter.setUpFetchListener(() -> recyclerView.post(() -> {
            Log.v(TAG, "convert: UpFetch");
            if (top[0] <= 1) {
                adapter.setUpFetchEnable(false);
                return;
            }
            while ((data = dao.queryById(item.getName(), --top[0])) == null) ;
            adapter.addData(0, data);
            adapter.setUpFetching(false);
        }));
        //刚载入时会同时出发多次 load more 第二位是锁
        adapter.setOnLoadMoreListener(() -> recyclerView.post(() -> {
            Log.v(TAG, "convert: OnLoadMore");
            if (bot[1] == 0)
                try {
                    Log.i(TAG, "convert: load more wait");
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            if (bot[0] >= max) {
                adapter.loadMoreEnd();
                return;
            }
            bot[1] = 0;
            while ((data = dao.queryById(item.getName(), ++bot[0])) == null) ;
            adapter.addData(data);
            adapter.loadMoreComplete();
            bot[1] = 1;
        }), recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(decor);
        recyclerView.setAdapter(adapter);

        // 滑动删除
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        adapter.enableSwipeItem();
        adapter.setOnItemSwipeListener(new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
            }

            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
            }

            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.i(TAG, "onItemSwiped: pos: " + pos);
                List<Messages> data = adapter.getData();
                if (data.size() <= pos) {
                    Log.i(TAG, "onItemSwiped: size is too small");
                    return;
                }
                Messages msg = data.get(pos);
                dao.deleteMessage(msg.getName(), msg.getId());
            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
            }
        });

        fc.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    down = new Date().getTime();
                    fc.getParent().requestDisallowInterceptTouchEvent(true);
                    return true;
                case MotionEvent.ACTION_UP:
                    long downTime = new Date().getTime() - down;
                    if (downTime < 200)
                        fc.toggle(false);
                    break;
            }
            return fc.isUnfolded();
        });
    }

    public List<Messages> prepareData() {
        return dao.queryAllRecalls();
    }

    private String formatTime(long time) {
        String string;
        Date date = new Date(time);
        calendar.setTime(date);
        switch (now.get(DAY_OF_YEAR) - calendar.get(DAY_OF_YEAR)) {
            case 0:
                string = sdfS.format(date);
                break;
            case 1:
                string = "昨天\n" + sdfS.format(date);
                break;
            case 2:
                string = "前天\n" + sdfS.format(date);
                break;
            default:
                string = sdfL.format(date);
                break;
        }
        return string;
    }

    private String formatDate(long time) {
        String string;
        Date date = new Date(time);
        calendar.setTime(date);
        switch (now.get(DAY_OF_YEAR) - calendar.get(DAY_OF_YEAR)) {
            case 0:
                string = "今天";
                break;
            case 1:
                string = "昨天";
                break;
            case 2:
                string = "前天";
                break;
            default:
                string = sdfDate.format(date);
                break;
        }
        return string;
    }

}