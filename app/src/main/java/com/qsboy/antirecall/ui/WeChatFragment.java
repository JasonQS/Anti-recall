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
public class WeChatFragment extends Fragment {

    String TAG = "WeChatFragment";
    //    RecyclerView recyclerView;
    RecyclerView recyclerViewAll;
    MessageAdapter adapterAll;
    Dao dao;
    int max;
    int[] cursor = new int[]{0, 1};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_messages, container, false);

        dao = Dao.getInstance(getContext(), Dao.DB_NAME_WE_CHAT);
        adapterAll = new MessageAdapter(dao, null, getActivity(), App.THEME_GREEN);
        recyclerViewAll = view.findViewById(R.id.main_recycler_view_all);
        max = dao.getMaxID(Table_Recalled_Messages);

        List<Messages> messages = prepareAllData();
        if (messages != null && messages.size() != 0)
            adapterAll.addData(messages);

        recyclerViewAll.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewAll.setAdapter(adapterAll);

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapterAll);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewAll);

        adapterAll.enableSwipeItem();
        adapterAll.setOnItemSwipeListener(onItemSwipeListener);

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
            List<Messages> data = adapterAll.getData();
            if (data.size() <= pos) {
                Log.i(TAG, "onItemSwiped: size is too small: " + pos);
                return;
            }
            Messages msg = data.get(pos);
            dao.deleteRecall(msg.getRecalledID());
            Log.w(TAG, "onItemSwiped: " + pos + " - " + msg.getName());
        }

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

        }
    };

    public List<Messages> prepareAllData() {
        Log.w(TAG, "prepareAllData: " + dao.toString());
        List<Messages> list = dao.queryAllTheLastMessage(dao.queryAllTables());
        Log.i(TAG, "prepareAllData: list: " + list);
        return list;
//        List<Messages> list = new ArrayList<>();
//        Messages messages;
//        cursor[0] = max + 1 - adapter.getData().size();
//        for (int i = 0; i < 10; i++) {
//            while (true) {
//                cursor[0]--;
//                if (cursor[0] == 0)
//                    return list;
//                if ((messages = dao.queryRecallById(cursor[0])) != null)
//                    break;
//            }
//            adapter.addData(messages);
//        }
//        return list;
    }

    // TODO: 24/04/2018 refresh
    public void refresh() {
        if (adapterAll == null)
            return;
        if (recyclerViewAll == null)
            return;
        List<Messages> messages = adapterAll.prepareData();
        if (messages != null && messages.size() != 0)
            if (adapterAll.getData().size() != messages.size()) {
                adapterAll.getData().clear();
                adapterAll.addData(messages);
            }
        adapterAll.notifyDataSetChanged();
        recyclerViewAll.setAdapter(adapterAll);
    }
}
