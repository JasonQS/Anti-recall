package com.qsboy.antirecall.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qsboy.antirecall.R;
import com.qsboy.antirecall.db.Dao;
import com.qsboy.antirecall.db.Messages;
import com.ramotion.foldingcell.FoldingCell;

import java.util.List;

/**
 * Created by JasonQS
 */

public class FoldingCellAdapter extends BaseItemDraggableAdapter<Messages, BaseViewHolder> {

    Context context;

    String TAG = "FoldingCellAdapter";

    public FoldingCellAdapter(@Nullable List<Messages> data, Context context) {
        super(R.layout.cell, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, Messages item) {
        // TODO: 时间显示优化
        FoldingCell fc = helper.getView(R.id.folding_cell);
        RecyclerView recyclerView = helper.getView(R.id.cell_recycler_view);
        MultiMessagesAdapter adapter = new MultiMessagesAdapter(null, context);

        fc.setOnClickListener(v -> fc.toggle(false));
        helper.setText(R.id.cell_title, item.getName());
        helper.setText(R.id.cell_name, item.getSubName());
        helper.setText(R.id.cell_time, item.getTime());
        helper.setText(R.id.cell_message_text, item.getMessage());

        List<Messages> messages = adapter.prepareData(item.getName(), item.isWX(), item.getId());
        final int[] top = {item.getId() - 3};
        final int[] bot = {item.getId() + 2};
        int max = new Dao(context).getMaxID(item.getName(), item.isWX());
        if (messages.size() != 0)
            adapter.addData(messages);
        adapter.setPreLoadNumber(3);
        adapter.setStartUpFetchPosition(3);
        adapter.setUpFetchEnable(true);
        adapter.setEnableLoadMore(false);
        adapter.disableLoadMoreIfNotFullPage(recyclerView);
        adapter.setUpFetchListener(() -> recyclerView.post(() -> {
            if (top[0] <= 1)
                adapter.setUpFetchEnable(false);
            Messages data = adapter.fetchData(item.getName(), item.isWX(), --top[0]);
            if (data != null)
                adapter.addData(0, data);
            Log.i(TAG, "convert: UpFetch");
        }));
        adapter.setOnLoadMoreListener(() -> {
            recyclerView.post(() -> {
                Messages data = adapter.fetchData(item.getName(), item.isWX(), ++bot[0]);
                if (bot[0] >= max)
                    adapter.loadMoreEnd();
                if (data != null) {
                    adapter.addData(data);
                    adapter.loadMoreComplete();
                }
                Log.i(TAG, "convert: OnLoadMore");
            });
        }, recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        // TODO: 滑动删除
//        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);

        // 开启拖拽
//        adapter.enableDragItem(itemTouchHelper, R.id.textView, true);
//        adapter.setOnItemDragListener(onItemDragListener);

        // 开启滑动删除
//        adapter.enableSwipeItem();
//        adapter.setOnItemSwipeListener(onItemSwipeListener);

    }

    public List<Messages> prepareData() {
        Dao dao = new Dao(context);
        return dao.queryAllRecalls();
    }

}