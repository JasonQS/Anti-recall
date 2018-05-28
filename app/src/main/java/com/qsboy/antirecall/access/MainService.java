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

import com.qsboy.antirecall.ui.App;
import com.qsboy.utils.NodesInfo;

import java.util.Date;
import java.util.List;

public class MainService extends AccessibilityService {

    final String pkgTim = "com.tencent.tim";
    final String pkgQQ = "com.tencent.mobileqq";
    final String pkgWX = "com.tencent.mm";
    final String pkgThis = "com.qsboy.antirecall";
    WXAutoLogin autoLogin;
    private String TAG = "Main Service";
    private AccessibilityNodeInfo root;
    private String packageName;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            // TODO: 加一段 服务运行时间段 记录第一次成功启动服务到最后一次收到 event
            if (event.getPackageName() == null) {
                Log.d(TAG, "onAccessibilityEvent: package name is null, return");
                return;
            }
            packageName = event.getPackageName() + "";

//        if (!(packageName.equals(pkgTim) || packageName.equals(pkgQQ) || packageName.equals(pkgWX)))
//            return;

            root = getRootInActiveWindow();
            if (root == null) {
                Log.d(TAG, "onAccessibilityEvent: root is null, return");
                return;
            }

            int eventType = event.getEventType();
            Log.v(TAG, AccessibilityEvent.eventTypeToString(eventType));
            // TODO: 判断各种手机型号 小米华为/oppo vivo
            if (eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            }

            switch (eventType) {
                case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                    onNotification(event);
                    break;
                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                    autoLogin.autoLoginWX();
                    onContentChanged(event);
                    break;
                case AccessibilityEvent.TYPE_VIEW_CLICKED:
                    onClick(event);
                    break;

            }
        } catch (Exception ignored) {
        }
    }

    private void onContentChanged(AccessibilityEvent event) {
        if (root == null) {
            Log.d(TAG, "onContentChanged: root is null, return");
            return;
        }
        // 只需在改变类型为文字时执行添加操作
        // 大部分change type为 CONTENT_CHANGE_TYPE_SUBTREE
        // TODO: 有些机型需要所有types
        if (event.getContentChangeTypes() != AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT) {
            Log.v(TAG, "onContentChanged: content change type: " + event.getContentChangeTypes());
            return;
        }

        switch (packageName) {
            case pkgTim:
                new TimClient(this).onContentChanged(root);
                break;
            case pkgQQ:
                new QQClient(this).onContentChanged(root);
                break;
        }
    }

    private void onClick(AccessibilityEvent event) {
        Log.i(TAG, "onClick " + event.getText());
//        NodesInfo.show(root, "d");
        switch (packageName) {
            case pkgTim:
                new TimClient(this).findRecalls(root, event);
                break;
            case pkgQQ:
                new QQClient(this).findRecalls(root, event);
                break;
            case pkgWX:
                NodesInfo.show(root, TAG);
                break;
            case pkgThis:
                if (event.getSource() == null) {
                    Log.d(TAG, "onAccessibilityEvent: event.getSource() is null, return");
                    return;
                }
                App.timeClickedCheckPermissionButton = new Date().getTime();
                break;
        }
    }

    private void onNotification(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (texts.isEmpty()) {
            NodesInfo.show(root, TAG);
            //微信的登录通知的 text 是 null
            autoLogin.flagEnable();
            return;
        }
        Log.i(TAG, "onNotification: " + packageName + " | " + texts);
        switch (packageName) {
            case pkgQQ:
//                new QQClient(this).onContentChanged(root);
                new QQClient(this).onNotificationChanged(event);
                break;
            case pkgTim:
//                new TimClient(this).onContentChanged(root);
                new TimClient(this).onNotificationChanged(event);
                break;
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.e(TAG, "onServiceConnected");
        autoLogin = new WXAutoLogin();
    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 在检测到空通知体的时候 enable flag
     * 在之后的10次 onContentChange 都去检查微信登录
     */
    private class WXAutoLogin {
        private int time = 0;

        public void flagEnable() {
            time = 10;
        }

        private void autoLoginWX() {
            while (time > 0) {
                time--;
                Log.v(TAG, "autoLoginWX");
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
                Log.w(TAG, "autoLoginWX: Perform Click");
                time = 0;
            }
        }
    }
}
