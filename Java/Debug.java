/*
 * Copyright (c) 2017.
 * qsboy.com 版权所有
 */

package com.qiansheng.messagecapture;


import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

class Debug {

    final String TAG = "x-check";

    static boolean ServerOnConnected;

    static boolean DebugEnabled = false;

    private Context mContext;
    private View view;
    private XFile xFile;

    Debug(Context context, View view) {

        mContext = context;
        this.view = view;

        xFile = new XFile(mContext);

        DebugEnabled = xFile.isDebug();

    }

    void onClick() {
        DebugEnabled = !DebugEnabled;

        Log.i(TAG, "debug? " + DebugEnabled);
        View text = view.findViewById(R.id.textView_debug);

        if (DebugEnabled) {

            XLogcat.getInstance().start();

            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBtnOn));
            text.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBtnOn));

            XToast.makeText(mContext, "调试模式打开\n收录的消息将会出现在顶部").show();
        } else {

            XLogcat.getInstance().stop();

            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBtnOff));
            text.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBtnOff));

            XToast.makeText(mContext, "调试模式已关闭").show();

        }

        xFile.setDebug(DebugEnabled);


    }

    private boolean enabled(String name) {
        AccessibilityManager am = (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> serviceInfos = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        List<AccessibilityServiceInfo> installedAccessibilityServiceList = am.getInstalledAccessibilityServiceList();
        Log.d(TAG, "enabled: " + serviceInfos);
        for (AccessibilityServiceInfo info : installedAccessibilityServiceList) {
            Log.d("MainActivity", "all -->" + info.getId());
            if (name.equals(info.getId())) {
                new XNotification(mContext).show("true");
                return true;
            }
        }
        new XNotification(mContext).show("false");
        return false;
    }

}
