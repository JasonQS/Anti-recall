/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui.fragment;


import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.qsboy.antirecall.R;
import com.qsboy.antirecall.db.Dao;
import com.qsboy.antirecall.db.Messages;
import com.qsboy.antirecall.ui.activyty.App;
import com.qsboy.antirecall.ui.adapter.MessageAdapter;

import java.util.List;

import static com.qsboy.antirecall.db.DBHelper.Table_Recalled_Messages;


public class QQFragment extends Fragment {

    String TAG = "QQFragment";
    ImageView adjuster;
    RelativeLayout relativeLayout;
    RecyclerView recyclerViewRecalled;
    RecyclerView recyclerViewAll;
    MessageAdapter adapterRecalled;
    MessageAdapter adapterAll;
    Handler handler;
    Dao dao;
    int max;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_messages, container, false);

        setHasOptionsMenu(true);

        dao = Dao.getInstance(getContext(), Dao.DB_NAME_QQ);
        max = dao.getMaxID(Table_Recalled_Messages);
        handler = new Handler();

        // TODO: 11/06/2018 recyclerView data 加载, 数据库查询优化
        recyclerViewRecalled = view.findViewById(R.id.main_recycler_view_recall);
        recyclerViewAll = view.findViewById(R.id.main_recycler_view_all);
        adapterRecalled = new MessageAdapter(dao, null, getActivity(), App.THEME_BLUE);
        adapterAll = new MessageAdapter(dao, null, getActivity(), App.THEME_RED);
        adjuster = view.findViewById(R.id.adjuster);

        View emptyViewRecalled = inflater.inflate(R.layout.empty_view_recalled, container, false);
        View emptyViewAll = inflater.inflate(R.layout.empty_view_all, container, false);
        initList(recyclerViewRecalled, emptyViewRecalled, adapterRecalled, prepareRecalledData());
        if (App.isShowAllQQMessages) {
            initList(recyclerViewAll, emptyViewAll, adapterAll, prepareAllData());
            initAdjuster(view);
        } else {
            setRecyclerViewRecalledHeight(App.layoutHeight);
            initAdjuster(view);
            adjuster.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar, menu);
    }

    private void initAdjuster(View view) {
        adjuster.setVisibility(View.VISIBLE);
        relativeLayout = view.findViewById(R.id.relative_layout_lists);
        relativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                relativeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                onMeasured();
            }
        });


        setRecyclerViewAllHeight(App.recyclerViewAllHeight);
        setRecyclerViewRecalledHeight(App.recyclerViewRecalledHeight);
        adjuster.setY(App.adjusterY - App.adjusterOriginalY);

        adjuster.setOnTouchListener(new View.OnTouchListener() {
            float difAdjuster = 0;
            float downY = 0;
            int heightAll;
            int heightRecalled;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        difAdjuster = adjuster.getY() - event.getRawY();
                        downY = event.getRawY();
                        heightAll = recyclerViewAll.getLayoutParams().height;
                        heightRecalled = recyclerViewRecalled.getLayoutParams().height;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dif = event.getRawY() - downY;
                        int v1 = (int) (heightAll + dif);
                        int v2 = (int) (heightRecalled - dif);

                        if (v1 < 0 || v1 > App.layoutHeight || v2 < 0 | v2 > App.layoutHeight)
                            break;

                        setRecyclerViewAllHeight(v1);
                        setRecyclerViewRecalledHeight(v2);
                        adjuster.setY(App.adjusterY = event.getRawY() + difAdjuster);

                        break;
                }

                return true;
            }
        });
    }

    private void onMeasured() {
        if (App.layoutHeight != -1)
            return;
        App.layoutHeight = relativeLayout.getHeight();
        if (App.isShowAllQQMessages) {
            setRecyclerViewAllHeight(App.layoutHeight / 2);
            setRecyclerViewRecalledHeight(App.layoutHeight / 2);
        } else
            setRecyclerViewAllHeight(App.layoutHeight);
        App.adjusterY = App.adjusterOriginalY = adjuster.getY();
    }

    private void setRecyclerViewAllHeight(int height) {
        ViewGroup.LayoutParams params = recyclerViewAll.getLayoutParams();
        params.height = height;
        App.recyclerViewAllHeight = height;
        recyclerViewAll.setLayoutParams(params);
    }

    private void setRecyclerViewRecalledHeight(int height) {
        ViewGroup.LayoutParams params = recyclerViewRecalled.getLayoutParams();
        params.height = height;
        App.recyclerViewRecalledHeight = height;
        recyclerViewRecalled.setLayoutParams(params);
    }

    private void initList(RecyclerView recyclerView, View emptyView, MessageAdapter adapter, List<Messages> messages) {
        if (messages != null && messages.size() != 0)
            adapter.addData(messages);
        if (App.isSwipeRemoveOn)
            adapter.enableSwipeItem();
        else
            adapter.disableSwipeItem();

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
                Log.w(TAG, "onItemSwiped: " + pos + " - " + msg.getName());
                dao.deleteTable(msg.getName());

            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

            }
        });

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        adapter.setEmptyView(emptyView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

    }

    public List<Messages> prepareRecalledData() {
        return dao.queryAllRecalls();
    }

    public List<Messages> prepareAllData() {
        List<Messages> list = dao.queryAllTheLastMessage(dao.queryAllTables());
        Log.d(TAG, "prepareAllData: list: " + list);
        return list;
    }

}
