/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qsboy.antirecall.R;
import com.qsboy.antirecall.db.Dao;
import com.qsboy.antirecall.db.Messages;
import com.qsboy.utils.ImageHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Calendar.*;

public class MultiMessagesAdapter extends BaseItemDraggableAdapter<Messages, BaseViewHolder> {

    String TAG = "MultiMessagesAdapter";

    Context context;
    Dao dao;
    int day;
    Calendar calendar = Calendar.getInstance();

    SimpleDateFormat sdfL = new SimpleDateFormat("MM - dd\nHH:mm:ss", Locale.getDefault());
    SimpleDateFormat sdfS = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public MultiMessagesAdapter(List<Messages> data, Context context) {
        super(R.layout.item_message, data);
        this.context = context;
        dao = Dao.getInstance(context);
    }

    @Override
    protected void convert(BaseViewHolder helper, Messages item) {
        Log.v(TAG, "convert: " + item.getMessage() + " id: " + item.getId());
        helper.setText(R.id.cell_name, item.getSubName());
        helper.setText(R.id.cell_time, formatTime(item.getTime()));
        // TODO: image之后改成可以左右滑动的
        if (item.getImages() != null && item.getImages().length() != 0) {
            helper.setImageBitmap(R.id.cell_message_image, ImageHelper.getBitmap(item.getImages()));
            helper.setText(R.id.cell_message_text, "");
        } else
            helper.setText(R.id.cell_message_text, item.getMessage());
    }

    private String formatTime(long time) {
        String string;
        Date date = new Date(time);
        calendar.setTime(date);
        if (day == (day = calendar.get(DAY_OF_YEAR)))
            string = sdfS.format(date);
        else
            string = sdfL.format(date);
        return string;
    }
}
