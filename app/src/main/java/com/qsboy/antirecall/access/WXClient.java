package com.qsboy.antirecall.access;

import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Date;


public class WXClient extends Client{

    String TAG = "Wx";

    public WXClient(Context context) {
        super(context);
    }

    @Override
    protected boolean init(AccessibilityNodeInfo root) {
        return false;
    }

    @Override
    protected void parser(AccessibilityNodeInfo group) {

    }
}
