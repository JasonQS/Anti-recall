/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui.widget;


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
                // 如果是展开的 所有手势都交给自己处理
                // 如果是收起的 在滑动一段后 (为了判断是真的想滑动) 就交给父 View 处理
                // 否则自己处理 (点击之类)
                // 实现父子View同方向滚动
                if (!isUnfolded()) {
                    int historySize = ev.getHistorySize();
                    Log.v("MyFoldingCell", "dispatchTouchEvent: historySize: " + historySize);
                    if (historySize > 1)
                        getParent().requestDisallowInterceptTouchEvent(false);
                }

                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}
