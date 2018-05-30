/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;


import android.graphics.Canvas;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

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
public class QQFragment extends Fragment {

    String TAG = "QQFragment";
    ImageView adjuster;
    ConstraintLayout constraintLayout;
    RecyclerView recyclerViewRecalled;
    RecyclerView recyclerViewAll;
    MessageAdapter adapterRecalled;
    MessageAdapter adapterAll;
    Dao dao;
    int max;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_messages, container, false);

        dao = Dao.getInstance(getContext(), Dao.DB_NAME_QQ);
        max = dao.getMaxID(Table_Recalled_Messages);

        adjuster = view.findViewById(R.id.adjuster);
        recyclerViewRecalled = view.findViewById(R.id.main_recycler_view_recall);
        recyclerViewAll = view.findViewById(R.id.main_recycler_view_all);
        adapterRecalled = new MessageAdapter(dao, null, getActivity(), App.THEME_BLUE);
        adapterAll = new MessageAdapter(dao, null, getActivity(), App.THEME_RED);

        constraintLayout = view.findViewById(R.id.constraint_layout_lists);

        initList(recyclerViewRecalled, adapterRecalled, prepareRecalledData());
        initList(recyclerViewAll, adapterAll, prepareAllData());

        constraintLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                constraintLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int layoutHeight = constraintLayout.getHeight();
                int deviceHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
                Log.e(TAG, "onCreateView: layoutHeight: " + layoutHeight);
                Log.e(TAG, "onCreateView: deviceHeight: " + deviceHeight);
                adjuster.setPadding(100, 100, 0, 100);

                adjuster.setOnTouchListener((v, event) -> {
//                    TransitionManager.beginDelayedTransition(constraintLayout);
                    ConstraintSet constraintSet = new ConstraintSet();
//            constraintSet.clone(getContext(), R.id.constraint_layout_lists);
                    float bias = (event.getRawY() - deviceHeight + layoutHeight) / layoutHeight;
                    Log.d(TAG, "onCreateView: " + event.getRawY());
                    Log.i(TAG, "onCreateView: " + bias);
                    constraintSet.clone(constraintLayout);
//                    constraintSet.setTransformPivotY(R.id.adjuster, 0.5f);
//                    Log.w(TAG, "onGlobalLayout: Y: " + constraintLayout.getChildAt(2).getY());
//                    constraintSet.setTranslationY(R.id.adjuster, event.getY());

                    constraintSet.setVerticalWeight(R.id.main_recycler_view_recall, event.getRawY() - deviceHeight + layoutHeight);
                    constraintSet.setVerticalWeight(R.id.main_recycler_view_all, deviceHeight - event.getRawY());
                    constraintSet.applyTo(constraintLayout);
                    return true;
                });
            }
        });

        return view;
    }

    private void initList(RecyclerView recyclerView, MessageAdapter adapter, List<Messages> messages) {
        if (messages != null && messages.size() != 0)
            adapter.addData(messages);

//        adapter.setPreLoadNumber(4);
//        adapter.setEnableLoadMore(true);
//        adapter.setOnLoadMoreListener(() -> {
//            int cursor = max + 1 - adapter.getData().size();
//            Log.i(TAG, "initList: cursor: " + cursor);
//            Log.v(TAG, "convert: OnLoadMore");
//            Messages data;
//            while (true) {
//                cursor--;
//                if (cursor <= 0) {
//                    adapter.loadMoreEnd();
//                    return;
//                }
//                if ((data = dao.queryRecallById(cursor)) != null)
//                    break;
//            }
//            adapter.addData(data);
//            adapter.loadMoreComplete();
//        }, recyclerView);

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
                dao.deleteRecall(msg.getRecalledID());
                Log.w(TAG, "onItemSwiped: " + pos + " - " + msg.getName());
            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

            }
        });

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

    }

    //    // TODO: 24/04/2018 refresh
//    public void refresh() {
//        if (adapter == null)
//            return;
//        if (recyclerView == null)
//            return;
//        List<Messages> messages = prepareRecalledData();
//        if (messages != null && messages.size() != 0)
//            if (adapter.getData().size() != messages.size()) {
//                adapter.getData().clear();
//                adapter.addData(messages);
//            }
//        adapter.notifyDataSetChanged();
//        recyclerView.setAdapter(adapter);
//    }
//
    public List<Messages> prepareRecalledData() {
//        List<Messages> list = new ArrayList<>();
//        Messages messages;
//        int[] cursor = new int[]{2, 1};
//        cursor[0] = max + 1 - adapter.getData().size();
//        for (int i = 0; i < 2; i++) {
//            while (true) {
//                cursor[0]--;
//                if (cursor[0] <= 0)
//                    return list;
//                if ((messages = dao.queryRecallById(cursor[0])) != null)
//                    break;
//            }
//            adapter.addData(messages);
//        }
//        return list;
        return dao.queryAllRecalls();
    }

    public List<Messages> prepareAllData() {
        List<Messages> list = dao.queryAllTheLastMessage(dao.queryAllTables());
        Log.i(TAG, "prepareAllData: list: " + list);
        return list;
    }

}
