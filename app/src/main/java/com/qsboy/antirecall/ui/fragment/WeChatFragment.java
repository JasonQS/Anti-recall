/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui.fragment;


import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.qsboy.antirecall.R;
import com.qsboy.antirecall.db.Dao;
import com.qsboy.antirecall.db.Messages;
import com.qsboy.antirecall.ui.activyty.App;
import com.qsboy.antirecall.ui.adapter.MessageAdapter;

import java.util.List;

import static com.qsboy.antirecall.db.DBHelper.Table_Recalled_Messages;


public class WeChatFragment extends Fragment {

    String TAG = "WeChatFragment";
    RecyclerView recyclerView;
    MessageAdapter adapter;

    ImageView adjuster;
    Dao dao;
    int max;
    private OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {

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
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_messages, container, false);

        dao = Dao.getInstance(getContext(), Dao.DB_NAME_WE_CHAT);
        max = dao.getMaxID(Table_Recalled_Messages);

        recyclerView = view.findViewById(R.id.main_recycler_view_all);
        adapter = new MessageAdapter(dao, null, getActivity(), App.THEME_GREEN);
        adjuster = view.findViewById(R.id.adjuster);
        adjuster.setVisibility(View.GONE);

        List<Messages> messages = prepareAllData();
        if (messages != null && messages.size() != 0)
            adapter.addData(messages);

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        if (App.isSwipeRemoveOn)
            adapter.enableSwipeItem();
        else
            adapter.disableSwipeItem();

        adapter.enableSwipeItem();
        adapter.setOnItemSwipeListener(onItemSwipeListener);

        adapter.setEmptyView(R.layout.empty_view_all, container);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = -1;
        recyclerView.setLayoutParams(params);

        return view;
    }

    public List<Messages> prepareAllData() {
        List<Messages> list = dao.queryAllTheLastMessage(dao.queryAllTables());
        Log.d(TAG, "prepareAllData: list: " + list);
        return list;
    }

}
