package com.qsboy.antirecall.access;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.qsboy.antirecall.utils.GetNodes;

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

        if (!packageName.equals(pknTim) &&
                !packageName.equals(pknQQ) &&
                !packageName.equals(pknWX))
            return;

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
                    case pknWX:
                        new WXClient().init(root);
                        break;
                }

        }
    }

    @Override
    public void onInterrupt() {

    }
}
