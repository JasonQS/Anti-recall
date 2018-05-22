/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;

import android.content.Context;
import android.util.Log;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qsboy.antirecall.R;
import com.qsboy.antirecall.db.Messages;
import com.qsboy.utils.ImageHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Calendar.DAY_OF_YEAR;

public class MultiMessagesAdapter extends BaseItemDraggableAdapter<Messages, BaseViewHolder> {

    String TAG = "MultiMessagesAdapter";

    Context context;
    private int theme;
    int day;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdfSec = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    SimpleDateFormat sdfDate = new SimpleDateFormat("MM - dd", Locale.getDefault());

    OnDateChangeListener onDateChangeListener;

    public MultiMessagesAdapter(List<Messages> data, Context context, int theme) {
        super(R.layout.item_message, data);
        this.context = context;
        this.theme = theme;
    }

    @Override
    protected void convert(BaseViewHolder helper, Messages item) {
        Log.v(TAG, "convert: " + item.getMessage() + " id: " + item.getId());
        helper.setText(R.id.cell_name, item.getSubName());
        helper.setText(R.id.cell_time, sdfSec.format(item.getTime()));
        formatTime(item.getTime());
        // TODO: image之后改成可以左右滑动的
        if (item.getImages() != null && item.getImages().length() != 0) {
            helper.setImageBitmap(R.id.cell_message_image, ImageHelper.getBitmap(item.getImage()));
            helper.setText(R.id.cell_message_text, "");
        } else {
            helper.setImageBitmap(R.id.cell_message_image, null);
            helper.setText(R.id.cell_message_text, item.getMessage());
        }
        switch (theme) {
            case App.THEME_BLUE:
                helper.setBackgroundColor(R.id.cell_name, context.getResources().getColor(R.color.bgNameBlue));
                helper.setBackgroundColor(R.id.item_message, context.getResources().getColor(R.color.bgContentBlue));
                break;
            case App.THEME_RED:
                helper.setBackgroundColor(R.id.cell_name, context.getResources().getColor(R.color.bgNameRed));
                helper.setBackgroundColor(R.id.item_message, context.getResources().getColor(R.color.bgContentRed));
                break;
            case App.THEME_GREEN:
                helper.setBackgroundColor(R.id.cell_name, context.getResources().getColor(R.color.bgNameGreen));
                helper.setBackgroundColor(R.id.item_message, context.getResources().getColor(R.color.bgContentGreen));
                break;
        }
    }

    public void setOnDateChangeListener(OnDateChangeListener onDateChangeListener) {
        this.onDateChangeListener = onDateChangeListener;
    }

    private void formatTime(long time) {
        Date date = new Date(time);
        calendar.setTime(date);
        if (day != (day = calendar.get(DAY_OF_YEAR)))
            onDateChangeListener.onDateChange(time);
    }

    interface OnDateChangeListener {
        void onDateChange(long date);
    }
}
