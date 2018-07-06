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

import com.qsboy.antirecall.ui.activyty.App;
import com.qsboy.antirecall.utils.NodesInfo;

import java.util.Date;
import java.util.List;

import static com.qsboy.antirecall.ui.activyty.App.pkgQQ;
import static com.qsboy.antirecall.ui.activyty.App.pkgThis;
import static com.qsboy.antirecall.ui.activyty.App.pkgTim;
import static com.qsboy.antirecall.ui.activyty.App.pkgWX;


public class MainService extends AccessibilityService {


    private String TAG = "Main Service";
    private String packageName;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            if (event.getPackageName() == null) {
                Log.d(TAG, "onAccessibilityEvent: package name is null, return");
                return;
            }
            packageName = event.getPackageName() + "";

//        if (!(packageName.equals(pkgTim) || packageName.equals(pkgQQ) || packageName.equals(pkgWX)))
//            return;

//            NodesInfo.show(event.getSource(), TAG, "d");
//            NodesInfo.show(root, TAG, "d");

            int eventType = event.getEventType();
            if (eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                Log.v(TAG, AccessibilityEvent.eventTypeToString(eventType));
            }

            switch (eventType) {
                case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                    onNotification(event);
                    break;
                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                    autoLoginWX();
                    onContentChanged(event);
                    break;
                case AccessibilityEvent.TYPE_VIEW_CLICKED:
                    onClick(event);
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onContentChanged(AccessibilityEvent event) {
        // 只需在改变类型为文字时执行添加操作
        // 大部分change type为 CONTENT_CHANGE_TYPE_SUBTREE
//        Log.w(TAG, "onContentChanged: TYPE: " + event.getContentChangeTypes());
//        if (App.isTypeText) {
//            if (event.getContentChangeTypes() != AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT) {
//                Log.v(TAG, "onContentChanged: content change type: " + event.getContentChangeTypes());
//                return;
//            }
//        } else if (event.getContentChangeTypes() == AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT)
//            App.isTypeText = true;
        // 只有在一整条消息变动时才进入逻辑, 不然会引入很多无关事件
        if (event.getSource().getChildCount() == 0)
            return;

        switch (packageName) {
            case pkgTim:
                new TimClient(this).onContentChanged(getRootInActiveWindow());
                break;
            case pkgQQ:
                new QQClient(this).onContentChanged(getRootInActiveWindow());
                break;
        }
    }

    private void onClick(AccessibilityEvent event) {
        Log.i(TAG, "onClick " + event.getText());
        if (event.getSource() == null) {
            Log.i(TAG, "onClick: event.getSource() is null, return");
            return;
        }
        AccessibilityNodeInfo root = getRootInActiveWindow();
        switch (packageName) {
            case pkgTim:
                new TimClient(this).findRecalls(root, event);
                break;
            case pkgQQ:
                NodesInfo.show(root, TAG);
                new QQClient(this).findRecalls(root, event);
                break;
            case pkgWX:
                NodesInfo.show(root, TAG);
                break;
            case pkgThis:
                App.timeCheckAccessibilityServiceIsWorking = new Date().getTime();
                break;
        }
    }

    private void onNotification(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (texts.isEmpty()) {
            // 微信的登录通知的 text 是 null
            // 现在转为Notification Listener来判定了
            // App.autoLoginFlagEnable();
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
    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 在检测到空通知体的时候 enable flag
     * 在之后的10次 onContentChange 都去检查微信登录
     */
    private void autoLoginWX() {
        // TODO: 2018/7/5 查看微信登录时的 event.getSource()
        while (WXClient.WeChatAutoLoginTimes > 0) {
            AccessibilityNodeInfo root = getRootInActiveWindow();
            if (root == null) {
                Log.d(TAG, "autoLoginWX: root is null, return");
                return;
            }
            WXClient.WeChatAutoLoginTimes--;
            Log.v(TAG, "autoLoginWX");
            if (root.getChildCount() != 1) {
                Log.v(TAG, "autoLoginWX: 1");
                return;
            }
            AccessibilityNodeInfo node = root.getChild(0);
            if (node.getChildCount() != 5) {
                Log.v(TAG, "autoLoginWX: 2");
                return;
            }
            //不直接判断字符串是因为多语言适应
            AccessibilityNodeInfo loginBtn = node.getChild(3);
            if (!loginBtn.isClickable()) {
                Log.v(TAG, "autoLoginWX: 3");
                return;
            }
            if (!node.getChild(0).isClickable()) {
                Log.v(TAG, "autoLoginWX: 4");
                return;
            }
            if (!node.getChild(4).isClickable()) {
                Log.v(TAG, "autoLoginWX: 5");
                return;
            }
            loginBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Log.w(TAG, "autoLoginWX: Perform Click");
            WXClient.WeChatAutoLoginTimes = 0;
        }
    }
}
