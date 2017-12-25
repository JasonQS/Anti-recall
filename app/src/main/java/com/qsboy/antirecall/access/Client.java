package com.qsboy.antirecall.access;

import android.view.accessibility.AccessibilityNodeInfo;


/**
 * Created by JasonQS
 */

public class Client {

    AccessibilityNodeInfo nameNode;
    AccessibilityNodeInfo chatGroupViewNode;
    AccessibilityNodeInfo timestampNode;
    AccessibilityNodeInfo headIconNode;
    AccessibilityNodeInfo messageNode;
    AccessibilityNodeInfo groupNode;
    AccessibilityNodeInfo picNode;
    AccessibilityNodeInfo redPegNode;
    AccessibilityNodeInfo recallNode;
    AccessibilityNodeInfo inputNode;
    AccessibilityNodeInfo sendBtnNode;
    AccessibilityNodeInfo nickNameNode;

    String packageName;
    String nameId;
    String picId;
    String chatGroupViewId;

    boolean isGroupMessage;
    String name;

    public boolean init(AccessibilityNodeInfo root) {
        return false;
    }

    public void addMessage(AccessibilityNodeInfo root) {

    }
}

