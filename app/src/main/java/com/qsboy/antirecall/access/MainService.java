/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.access;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class MainService extends AccessibilityService {

    private String TAG = "Main Service";
    private AccessibilityNodeInfo root;
    private String packageName;
    final String pknTim = "com.tencent.tim";
    final String pknQQ = "com.tencent.mobileqq";
    final String pknWX = "com.tencent.mm";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getPackageName() == null) {
            Log.d(TAG, "onAccessibilityEvent: package subName is null, return");
            return;
        }
        packageName = event.getPackageName().toString();

//        if (!packageName.equals(pknTim) &&
//                !packageName.equals(pknQQ) &&
//                !packageName.equals(pknWX))
//            return;

        root = getRootInActiveWindow();
        if (root == null) {
//            Log.d(TAG, "onAccessibilityEvent: root is null, return");
            return;
        }

        int eventType = event.getEventType();
        if (eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            Log.v(TAG, AccessibilityEvent.eventTypeToString(eventType));
        }

        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                onNotification(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                onContentChanged(event);
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                onClick(event);
                break;

        }

    }

    private void onNotification(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (texts.isEmpty() || texts.size() == 0) {
            GetNodes.show(root, TAG);
            //微信的登录通知的 text 是 null
            autoLoginWX();
            return;
        }
        Log.i(TAG, "onNotification: " + texts);
        switch (packageName) {
            case pknTim:
//                new TimClient(this).onContentChanged(root);
                new TimClient(this).onNotificationChanged(event);
                break;
            case pknQQ:
//                new QQClient(this).onContentChanged(root);
                new QQClient(this).onNotificationChanged(event);
                break;
        }
    }

    private void onContentChanged(AccessibilityEvent event) {
        if (root == null)
            return;
        // 只需在改变类型为文字时执行添加操作
        // 大部分change type为 CONTENT_CHANGE_TYPE_SUBTREE
        if (event.getContentChangeTypes() != AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT)
            return;
        CharSequence cs = event.getSource().getText();

        switch (packageName) {
            case pknTim:
                Log.i(TAG, "\nonContentChanged: " + cs);
                new TimClient(this).onContentChanged(root);
                break;
            case pknQQ:
                Log.i(TAG, "\nonContentChanged: " + cs);
                new QQClient(this).onContentChanged(root);
                break;
        }
    }

    private void onClick(AccessibilityEvent event) {
        Log.i(TAG, "onClick: ");
//        GetNodes.show(root, "d");
        switch (packageName) {
            case pknTim:
                new TimClient(this).findRecalls(root, event);
                break;
            case pknQQ:
                new QQClient(this).findRecalls(root, event);
                break;
            case pknWX:
                GetNodes.show(root, TAG);

        }
    }

    private void autoLoginWX() {
        Log.i(TAG, "autoLoginWX: ");
        if (root.getChildCount() != 1)
            return;
        AccessibilityNodeInfo node = root.getChild(0);
        if (node.getChildCount() != 5)
            return;
        //不直接判断字符串是因为多语言适应
        AccessibilityNodeInfo loginBtn = node.getChild(3);
        if (!loginBtn.isClickable())
            return;
        if (!node.getChild(0).isClickable())
            return;
        if (!node.getChild(4).isClickable())
            return;
        loginBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        Log.i(TAG, "autoLoginWX: click");
    }

    @Override
    public void onInterrupt() {

    }
}
