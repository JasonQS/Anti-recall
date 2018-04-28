/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;


import android.content.Intent;
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
import com.qsboy.antirecall.db.Messages;
import com.qsboy.antirecall.db.WeChatDao;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeChatFragment extends Fragment {

    String TAG = "WeChatFragment";
    MyRecyclerView recyclerView;
    Page2Adapter adapter;
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
            WeChatDao dao = WeChatDao.getInstance(getActivity());
            Messages messages = adapter.getData().get(pos);
            dao.deleteMessage(messages.getName(), messages.getRecalledID());
            Log.i(TAG, "clearView: " + adapter.getData());
        }

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

        }
    };

    public WeChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qq, container, false);

        adapter = new Page2Adapter(null, getActivity());
        recyclerView = view.findViewById(R.id.main_recycler_view);

        List<Messages> messages = adapter.prepareData();
        Log.i(TAG, "onCreateView: data: " + messages);
        if (messages != null && messages.size() != 0)
            adapter.addData(messages);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        adapter.enableSwipeItem();
        adapter.setOnItemSwipeListener(onItemSwipeListener);

        String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
        Intent intent = new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
}
