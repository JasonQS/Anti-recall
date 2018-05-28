/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;


import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.qsboy.antirecall.R;
import com.qsboy.antirecall.db.Dao;
import com.qsboy.antirecall.db.Messages;

import java.util.ArrayList;
import java.util.List;

import static com.qsboy.antirecall.db.DBHelper.Table_Recalled_Messages;

/**
 * A simple {@link Fragment} subclass.
 */
public class QQFragment extends Fragment {

    String TAG = "QQFragment";
    RecyclerView recyclerViewRecall;
    RecyclerView recyclerViewAll;
    MessageAdapter adapterRecall;
    MessageAdapter adapterAll;
    Dao dao;
    int max;
    int[] cursor = new int[]{0, 1};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_messages, container, false);

        dao = Dao.getInstance(getContext(), Dao.DB_NAME_QQ);
        adapterRecall = new MessageAdapter(dao, null, getActivity(), App.THEME_BLUE);
        adapterAll = new MessageAdapter(Dao.getInstance(getContext(), Dao.DB_NAME_QQ), null, getActivity(), App.THEME_RED);
        recyclerViewRecall = view.findViewById(R.id.main_recycler_view_recall);
        recyclerViewAll = view.findViewById(R.id.main_recycler_view_all);
        max = dao.getMaxID(Table_Recalled_Messages);

        List<Messages> messages = prepareData();
        if (messages != null && messages.size() != 0)
            adapterRecall.addData(messages);

        adapterRecall.setPreLoadNumber(4);
        adapterRecall.setEnableLoadMore(true);
        adapterRecall.setOnLoadMoreListener(() -> {
            Log.v(TAG, "convert: OnLoadMore");
            if (cursor[1] == 0)
                try {
                    Log.i(TAG, "convert: load more wait");
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            cursor[1] = 0;
            Messages data;
            while (true) {
                cursor[0]--;
                if (cursor[0] <= 0) {
                    adapterRecall.loadMoreEnd();
                    return;
                }
                if ((data = dao.queryRecallById(cursor[0])) != null)
                    break;
            }
            adapterRecall.addData(data);
            adapterRecall.loadMoreComplete();
            cursor[1] = 1;
        }, recyclerViewRecall);
        adapterRecall.enableSwipeItem();
        adapterRecall.setOnItemSwipeListener(onItemSwipeListener);
        recyclerViewRecall.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewRecall.setAdapter(adapterRecall);


        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapterRecall);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewRecall);


        return view;
    }

    private OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {

        @Override
        public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
        }

        @Override
        public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
        }

        @Override
        public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
            Log.i(TAG, "onItemSwiped: pos: " + pos);
            dao.deleteRecall(adapterRecall.getData().get(pos).getRecalledID());
            Log.d(TAG, "clearView: " + adapterRecall.getData());
        }

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

        }
    };

    // TODO: 24/04/2018 refresh
    public void refresh() {
        if (adapterRecall == null)
            return;
        if (recyclerViewRecall == null)
            return;
        List<Messages> messages = prepareData();
        if (messages != null && messages.size() != 0)
            if (adapterRecall.getData().size() != messages.size()) {
                adapterRecall.getData().clear();
                adapterRecall.addData(messages);
            }
        adapterRecall.notifyDataSetChanged();
        recyclerViewRecall.setAdapter(adapterRecall);
    }

    public List<Messages> prepareData() {
        List<Messages> list = new ArrayList<>();
        Messages messages;
        cursor[0] = max + 1 - adapterRecall.getData().size();
        for (int i = 0; i < 10; i++) {
            while (true) {
                cursor[0]--;
                if (cursor[0] <= 0)
                    return list;
                if ((messages = dao.queryRecallById(cursor[0])) != null)
                    break;
            }
            adapterRecall.addData(messages);
        }
        return list;
    }

}
