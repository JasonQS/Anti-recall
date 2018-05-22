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

import java.util.List;

import static com.qsboy.antirecall.db.DBHelper.Table_Recalled_Messages;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimFragment extends Fragment {

    String TAG = "TimFragment";
    RecyclerView recyclerViewRecall;
    RecyclerView recyclerViewAll;
    MessageAdapter adapter;
    Dao dao;
    int max;
    int[] cursor = new int[]{0, 1};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_messages, container, false);

        dao = Dao.getInstance(getContext(), Dao.DB_NAME_WE_CHAT);
        adapter = new MessageAdapter(dao, null, getActivity(), App.THEME_BLUE);
        recyclerViewRecall = view.findViewById(R.id.main_recycler_view_recall);
        recyclerViewAll = view.findViewById(R.id.main_recycler_view_all);
        max = dao.getMaxID(Table_Recalled_Messages);

        List<Messages> messages = prepareData();
        if (messages != null && messages.size() != 0)
            adapter.addData(messages);

        adapter.setPreLoadNumber(4);
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(() -> {
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
                if (cursor[0] == 0) {
                    adapter.loadMoreEnd();
                    return;
                }
                if ((data = dao.queryRecallById(cursor[0])) != null)
                    break;
            }
            adapter.addData(data);
            adapter.loadMoreComplete();
            cursor[1] = 1;
        }, recyclerViewRecall);
        recyclerViewRecall.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewRecall.setAdapter(adapter);

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewRecall);

        adapter.enableSwipeItem();
        adapter.setOnItemSwipeListener(onItemSwipeListener);
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
            Dao dao = Dao.getInstance(getActivity(), Dao.DB_NAME_QQ);
            dao.deleteRecall(adapter.getData().get(pos).getRecalledID());
            Log.d(TAG, "clearView: " + adapter.getData());
        }

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

        }
    };

    // TODO: 24/04/2018 refresh
    public void refresh() {
        if (adapter == null)
            return;
        if (recyclerViewRecall == null)
            return;
        List<Messages> messages = prepareData();
        if (messages != null && messages.size() != 0)
            if (adapter.getData().size() != messages.size()) {
                adapter.getData().clear();
                adapter.addData(messages);
            }
        adapter.notifyDataSetChanged();
        recyclerViewRecall.setAdapter(adapter);
    }

    public List<Messages> prepareData() {
        return dao.queryAllLastMessage(dao.queryAllTables());
    }
}
