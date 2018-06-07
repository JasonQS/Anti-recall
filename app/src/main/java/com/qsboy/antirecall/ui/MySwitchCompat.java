/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;

import java.lang.reflect.Field;


public class MySwitchCompat extends SwitchCompat {

    public MySwitchCompat(Context context) {
        super(context);
    }

    public MySwitchCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySwitchCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // 利用反射把属性保存在application里
    public void setAttr(Class app, String booleanFieldName) {
        try {
            Field field = app.getDeclaredField(booleanFieldName);
            setChecked(field.getBoolean(app));
            setOnClickListener(v -> {
                try {
                    field.set(app, isChecked());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
