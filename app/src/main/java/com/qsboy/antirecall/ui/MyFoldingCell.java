/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.ramotion.foldingcell.FoldingCell;

public class MyFoldingCell extends FoldingCell {
    public MyFoldingCell(Context context) {
        super(context);
    }

    public MyFoldingCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFoldingCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(isUnfolded());
            case MotionEvent.ACTION_UP:
        }
        return super.dispatchTouchEvent(ev);
    }

}
