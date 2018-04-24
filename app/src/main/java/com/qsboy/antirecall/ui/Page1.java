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

/**
 * A simple {@link Fragment} subclass.
 */
public class Page1 extends Fragment {

    String TAG = "Page1";
    MyRecyclerView recyclerView;
    Page1Adapter adapter;

    public Page1() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page1, container, false);

        adapter = new Page1Adapter(null, getActivity());
        recyclerView = view.findViewById(R.id.main_recycler_view);

        List<Messages> messages = adapter.prepareData();
        if (messages != null && messages.size() != 0)
            adapter.addData(messages);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        adapter.enableSwipeItem();
        adapter.setOnItemSwipeListener(onItemSwipeListener);
        return view;
    }

    // TODO: 24/04/2018 refresh
    public void refresh() {
        if (adapter == null)
            return;
        if (recyclerView == null)
            return;
        List<Messages> messages = adapter.prepareData();
        if (messages != null && messages.size() != 0)
            if (adapter.getData().size() != messages.size()) {
                adapter.getData().clear();
                adapter.addData(messages);
            }
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
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
            Dao dao = Dao.getInstance(getActivity());
            dao.deleteRecall(adapter.getData().get(pos).getRecalledID());
            Log.i(TAG, "clearView: " + adapter.getData());
        }

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

        }
    };
}
