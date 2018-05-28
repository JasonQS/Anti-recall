/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class MySwitch extends RelativeLayout {

    private String text;
    private boolean isChecked;

    public MySwitch(Context context) {
        super(context);
    }

    public MySwitch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MySwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
