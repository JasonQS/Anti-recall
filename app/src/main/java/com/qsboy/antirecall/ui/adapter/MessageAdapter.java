/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui.adapter;

import android.content.Context;
import android.graphics.Canvas;
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
import com.qsboy.antirecall.ui.activity.App;
import com.qsboy.antirecall.ui.widget.MyFoldingCell;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Calendar.DAY_OF_YEAR;

public class MessageAdapter extends BaseItemDraggableAdapter<Messages, BaseViewHolder> {

    private String TAG = "MessageAdapter";
    private Context context;
    private Dao dao;
    private int theme;
    private Messages data;
    private Calendar now = Calendar.getInstance();
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat sdfDate = new SimpleDateFormat("MM - dd", Locale.getDefault());
    private SimpleDateFormat sdfL = new SimpleDateFormat("MM-dd\nHH:mm", Locale.getDefault());
    private SimpleDateFormat sdfS = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private long down = 0;

    public MessageAdapter(Dao dao, @Nullable List<Messages> data, Context context, int theme) {
        super(R.layout.cell, data);
        this.context = context;
        this.dao = dao;
        this.theme = theme;
    }

    @Override
    protected void convert(BaseViewHolder helper, Messages item) {
        Log.v(TAG, "convert: " + item.getText() + " id: " + item.getId());
        MyFoldingCell fc = helper.getView(R.id.folding_cell);
        RecyclerView recyclerView = helper.getView(R.id.cell_recycler_view);
        MultiMessagesAdapter adapter = new MultiMessagesAdapter(null, context, theme);

        init(helper, item);

        int[] top = {item.getId()};
        int[] bot = {item.getId()};
        int max = dao.getMaxID(item.getName());
        adapter.addData(item);

        adapter.setUpFetchEnable(true);
        adapter.setEnableLoadMore(true);
//        adapter.setStartUpFetchPosition(3);
//        adapter.setPreLoadNumber(3);
        adapter.setOnDateChangeListener(date -> helper.setText(R.id.cell_title_date, formatDate(date)));

        adapter.setUpFetchListener(() -> recyclerView.post(() -> {
            while (true) {
                top[0]--;
                if (top[0] <= 0) {
                    adapter.setUpFetchEnable(false);
                    return;
                }
                if ((data = dao.queryById(item.getName(), top[0])) != null)
                    break;
            }
            adapter.addData(0, data);
            adapter.setUpFetching(false);
        }));

        adapter.setOnLoadMoreListener(() -> recyclerView.post(() -> {
            while (true) {
                bot[0]++;
                if (bot[0] > max) {
                    adapter.loadMoreEnd();
                    return;
                }
                if ((data = dao.queryById(item.getName(), bot[0])) != null)
                    break;
            }
            adapter.addData(data);
            adapter.loadMoreComplete();
        }), recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(context) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (Exception ignored) {

                }
            }
        });
        recyclerView.setAdapter(adapter);

        initSwipe(recyclerView, adapter);

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

    private void init(BaseViewHolder helper, Messages item) {
        helper.setText(R.id.cell_title_fold, item.getName());
        helper.setText(R.id.cell_title_unfold, item.getName());
        helper.setText(R.id.cell_name, item.getSubName());
        helper.setText(R.id.cell_time, formatTime(item.getTime()));
        helper.setText(R.id.cell_message_text, item.getText());

        switch (theme) {
            case App.THEME_BLUE:
                helper.setBackgroundColor(R.id.cell_name, context.getResources().getColor(R.color.bgNameBlue));
                helper.setBackgroundColor(R.id.item_message, context.getResources().getColor(R.color.bgContentBlue));
                helper.setBackgroundColor(R.id.cell_title_fold_bg, context.getResources().getColor(R.color.bgTitleBlue));
                helper.setBackgroundColor(R.id.cell_title_unfold_bg, context.getResources().getColor(R.color.bgTitleBlue));
                break;
            case App.THEME_RED:
                helper.setBackgroundColor(R.id.cell_name, context.getResources().getColor(R.color.bgNameRed));
                helper.setBackgroundColor(R.id.item_message, context.getResources().getColor(R.color.bgContentRed));
                helper.setBackgroundColor(R.id.cell_title_fold_bg, context.getResources().getColor(R.color.bgTitleRed));
                helper.setBackgroundColor(R.id.cell_title_unfold_bg, context.getResources().getColor(R.color.bgTitleRed));
                break;
            case App.THEME_GREEN:
                helper.setBackgroundColor(R.id.cell_name, context.getResources().getColor(R.color.bgNameGreen));
                helper.setBackgroundColor(R.id.item_message, context.getResources().getColor(R.color.bgContentGreen));
                helper.setBackgroundColor(R.id.cell_title_fold_bg, context.getResources().getColor(R.color.bgTitleGreen));
                helper.setBackgroundColor(R.id.cell_title_unfold_bg, context.getResources().getColor(R.color.bgTitleGreen));
                break;

        }
    }

    private void initSwipe(RecyclerView recyclerView, MultiMessagesAdapter adapter) {
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
                List<Messages> data = adapter.getData();
                if (data.size() <= pos) {
                    Log.i(TAG, "onItemSwiped: size is too small: " + pos);
                    return;
                }
                Messages msg = data.get(pos);
                Log.i(TAG, "onItemSwiped: " + pos + " - " + msg.getName() + ": " + msg.getText());
                dao.deleteMessage(msg.getName(), msg.getId());
            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
            }
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