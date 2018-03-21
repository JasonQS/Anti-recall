/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.access;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityEventSource;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by JasonQS
 */

public class MainAccessibilityService extends AccessibilityService {

    private String TAG = "Accessibility Service";
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
            Log.d(TAG, AccessibilityEvent.eventTypeToString(eventType));
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


    /**
     * 把通知栏里截获的消息处理并写入本地
     */
    void getNotification(AccessibilityEvent event) {
        Log.i(TAG, "Notification Changed");
        List<CharSequence> texts = event.getText();
        if (texts.isEmpty() || texts.size() == 0)
            return;
        for (CharSequence text : texts) {
            if (text == null)
                return;
            String string = text.toString();
            Log.w(TAG, "Notification Text:" + string);
            if (string.equals("你的帐号在电脑登录"))
                return;

            String content;
            String name;

            int i = string.indexOf(':');
            if (i < 1) {
                Log.d(TAG, "Notification does not contains ':'");
                return;
            }
            name = string.substring(0, i);
            content = string.substring(i + 2);
            //是QQ群消息
//            if (!is_wx)
//                if (subName.charAt(i - 1) == ')' && subName.contains("(")) {
//                    content = string.substring(i + 1);
//                    subName = subName.substring(subName.indexOf('(') + 1, subName.indexOf(')'));
//                }
//            long date = new Date().getTime();
//            Log.w(TAG, "subName : " + subName + "    content : " + content + "    time : " + date);
//            String line = content + date;
//            tempMessage = content;
//            xFile.writeFile(line, subName);
        }

    }

    private void onNotification(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (texts.isEmpty() || texts.size() == 0)
            return;
        Log.i(TAG, "onNotification: " + texts);
    }

    private void onContentChanged(AccessibilityEvent event) {
        if (root == null)
            return;
        // 只需在改变类型为文字时执行添加操作
        // 大部分change type为 CONTENT_CHANGE_TYPE_SUBTREE
        if (event.getContentChangeTypes() != AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT)
            return;
        CharSequence cs = event.getSource().getText();

        // TODO: 只有当 changed content 为 "消息.*" 时才添加新消息
        switch (packageName) {
            case pknTim:
                Log.w(TAG, "\nonContentChanged: " + cs);
                new TimClient(this).addMessage(root);
                break;
            case pknQQ:
                Log.w(TAG, "\nonContentChanged: " + cs);
                new QQClient(this).addMessage(root);
                break;
        }
    }

    private void onClick(AccessibilityEvent event) {
//        GetNodes.show(root, "d");
        switch (packageName) {
            case pknTim:
                new TimClient(this).getScreen(root);
                break;
            case pknQQ:
                new QQClient(this).getScreen(root);
                break;
        }
    }


    @Override
    public void onInterrupt() {

    }
}
