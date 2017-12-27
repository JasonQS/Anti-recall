package com.qsboy.antirecall.ui;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qsboy.antirecall.R;

import java.util.List;

/**
 * Created by JasonQS
 */

public class MessagesAdapter extends BaseQuickAdapter<MessageItem, BaseViewHolder> {
    public MessagesAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageItem item) {
        helper.setText(R.id.text_view_name, item.getName());
        helper.setText(R.id.text_view_time, item.getTime());
        helper.setText(R.id.text_view_message, item.getMessage());
//        helper.setImageResource(R.id.icon, item.getImageResource());

    }
}
