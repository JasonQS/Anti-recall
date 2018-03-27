/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.qsboy.antirecall.db.Dao;
import com.qsboy.antirecall.db.Messages;
import com.ramotion.foldingcell.FoldingCell;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.crypto.AEADBadTagException;

import static java.util.Calendar.*;

public class FoldingCellAdapter extends BaseItemDraggableAdapter<Messages, BaseViewHolder> {

    String TAG = "FoldingCellAdapter";

    Context context;
    Dao dao;
    Calendar now = Calendar.getInstance();
    Calendar calendar = Calendar.getInstance();

    SimpleDateFormat sdfL = new SimpleDateFormat("MM-dd\nHH:mm", Locale.getDefault());
    SimpleDateFormat sdfS = new SimpleDateFormat("HH:mm", Locale.getDefault());
    long down = 0;

    public FoldingCellAdapter(@Nullable List<Messages> data, Context context) {
        super(R.layout.cell, data);
        this.context = context;
        dao = new Dao(context);
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

        List<Messages> messages = adapter.prepareData(item.getName(), item.isWX(), item.getId());
        final int[] top = {item.getId() - 3};
        final int[] bot = {item.getId() + 3};
        int max = dao.getMaxID(item.getName(), item.isWX());
        if (messages.size() != 0)
            adapter.addData(messages);
        adapter.setStartUpFetchPosition(3);
        adapter.setUpFetchEnable(true);
        adapter.setPreLoadNumber(4);
        adapter.setEnableLoadMore(true);
        adapter.setUpFetchListener(() -> recyclerView.post(() -> {
            if (top[0] <= 1)
                adapter.setUpFetchEnable(false);
            Messages data = adapter.fetchData(item.getName(), item.isWX(), --top[0]);
            if (data != null)
                adapter.addData(0, data);
            Log.v(TAG, "convert: UpFetch");
        }));
        adapter.setOnLoadMoreListener(() -> recyclerView.post(() -> {
            Messages data = adapter.fetchData(item.getName(), item.isWX(), ++bot[0]);
            if (bot[0] >= max)
                adapter.loadMoreEnd();
            if (data != null) {
                adapter.addData(data);
                adapter.loadMoreComplete();
            }
            Log.v(TAG, "convert: OnLoadMore");
        }), recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        // TODO: 滑动删除
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // 开启拖拽
//        adapter.enableDragItem(itemTouchHelper, R.id.textView, true);
//        adapter.setOnItemDragListener(onItemDragListener);

        // 开启滑动删除
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
                Dao dao = new Dao(context);
                dao.deleteRecall(adapter.getData().get(pos).getRecalledID());
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

}