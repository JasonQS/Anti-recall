package com.qsboy.antirecall.ui;

import android.content.Context;
import android.util.Log;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qsboy.antirecall.R;
import com.qsboy.antirecall.db.Dao;
import com.qsboy.antirecall.db.Messages;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JasonQS
 */

public class MultiMessagesAdapter extends BaseItemDraggableAdapter<Messages, BaseViewHolder> {

    private final Context context;

    String TAG = "MultiMessagesAdapter";

    public MultiMessagesAdapter(List<Messages> data, Context context) {
        super(R.layout.item_message, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, Messages item) {
        // TODO: 时间显示优化
//        helper.setText(R.id.cell_title, item.getName());
        helper.setText(R.id.cell_name, item.getSubName());
        helper.setText(R.id.cell_time, item.getTime());
        helper.setText(R.id.cell_message_text, item.getId() + "\t" + item.getMessage());
    }

    public List<Messages> prepareData(String name, boolean isWX, int id) {
        List<Messages> list = new ArrayList<>();
        for (int i = id - 3; i < id + 3; i++) {
            Messages messages = fetchData(name, isWX, i);
            if (messages != null)
                list.add(messages);
        }
        return list;
    }

    public Messages fetchData(String name, boolean isWX, int id) {
        Log.i(TAG, "fetchData: " + id);
        Dao dao = new Dao(context);
        return dao.queryById(name, isWX, id);
    }

}
