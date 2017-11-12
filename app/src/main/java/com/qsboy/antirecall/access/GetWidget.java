package com.qsboy.antirecall.access;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by JasonQS
 */

public class GetWidget {

    private String TAG = "Get Widget";

    public void init(AccessibilityNodeInfo root){
//        root.
    }

    public void get(AccessibilityNodeInfo nodeInfo){
        Log.i(TAG, "get: ");
        System.out.println(nodeInfo);
//        nodeInfo.setContentDescription();
    }

}
