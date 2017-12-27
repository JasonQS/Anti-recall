package com.qsboy.antirecall.access;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
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
            Log.d(TAG, "onAccessibilityEvent: package name is null, return");
            return;
        }
        packageName = event.getPackageName().toString();

//        if (!packageName.equals(pknTim) &&
//                !packageName.equals(pknQQ) &&
//                !packageName.equals(pknWX))
//            return;

        root = getRootInActiveWindow();
        if (root == null) {
            Log.d(TAG, "onAccessibilityEvent: root is null, return");
            return;
        }

        int eventType = event.getEventType();
        if (eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            Log.d(TAG, AccessibilityEvent.eventTypeToString(eventType));
        }

        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = event.getText();
                if (texts.isEmpty() || texts.size() == 0)
                    return;
                Log.i(TAG, "Notification: " + texts);
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                GetNodes.show(root);
                switch (packageName) {
                    case pknTim:
                        new TimClient().init(root);
                        break;
                    case pknQQ:
                        new QQClient().init(root);
                        break;
                }
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if (root == null)
                    return;
                switch (packageName) {
                    case pknTim:
                        new TimClient().addMessage(root);
                        break;
                    case pknQQ:
                        new QQClient().addMessage(root);
                        break;
                }

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
//                if (name.charAt(i - 1) == ')' && name.contains("(")) {
//                    content = string.substring(i + 1);
//                    name = name.substring(name.indexOf('(') + 1, name.indexOf(')'));
//                }
//            long date = new Date().getTime();
//            Log.w(TAG, "name : " + name + "    content : " + content + "    time : " + date);
//            String line = content + date;
//            tempMessage = content;
//            xFile.writeFile(line, name);
        }

    }

    @Override
    public void onInterrupt() {

    }
}
