package com.qsboy.antirecall.ui;

import android.content.Context;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qsboy.antirecall.R;
import com.qsboy.antirecall.db.Dao;
import com.qsboy.antirecall.db.Messages;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JasonQS
 */

public class MultiMessagesAdapter extends BaseQuickAdapter<Messages, BaseViewHolder> {

    private final Context context;

    public MultiMessagesAdapter(List<Messages> data, Context context) {
        super(R.layout.item_message, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, Messages item) {
        helper.setText(R.id.cell_title, item.getName());
        helper.setText(R.id.cell_name, item.getSubName());
        helper.setText(R.id.cell_time, item.getTime());
        helper.setText(R.id.cell_message, item.getMessage());
//        helper.setImageResource(R.id.icon, item.getImage());
    }

    public List<Messages> prepareData(String name, boolean isWX, int id) {
        List<Messages> list = new ArrayList<>();
        for (int i = id - 3; i < id + 3; i++) {
            Messages messages = fetchData(name,isWX,i);
            if (messages != null)
                list.add(messages);
        }
        return list;
    }

    public Messages fetchData(String name, boolean isWX, int id){
        Dao dao = new Dao(context);
        return dao.queryById(name, isWX, id);
    }

    class a extends BaseViewHolder {
        public a(View view) {
            super(view);
        }
    }
}
