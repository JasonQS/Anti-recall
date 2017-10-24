package com.qsboy.antirecall;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.qsboy.antirecall.measure.QQClient;
import com.qsboy.antirecall.measure.TimClient;
import com.qsboy.antirecall.utils.GetNodes;

import java.util.List;

/**
 * Created by JasonQS
 */

public class MainAccessibilityService extends AccessibilityService {

    private String TAG = "Accessibility Service";
//    GetWidget getWidget = new GetWidget();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null)
            return;

        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            Log.i(TAG, AccessibilityEvent.eventTypeToString(event.getEventType()));
        }

        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = event.getText();
                if (texts.isEmpty() || texts.size() == 0)
                    return;
                Log.i(TAG, "Notification: " + texts);
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                GetNodes.show(root, 0);
                switch (event.getPackageName().toString()) {
                    case "com.tencent.tim":
                        new TimClient().init(root);
                        break;
                    case "com.tencent.mobileqq":
                        new QQClient().init(root);
                        break;
                }

        }
    }

    @Override
    public void onInterrupt() {

    }
}
