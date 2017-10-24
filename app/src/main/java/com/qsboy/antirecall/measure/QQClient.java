package com.qsboy.antirecall.measure;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.qsboy.antirecall.utils.GetNodes;

import java.util.List;

/**
 * Created by JasonQS
 */

public class QQClient extends Client {

    static String TAG = "QQ";

    public QQClient() {
        nameId = "com.tencent.mobileqq:id/title";
        groupId = "com.tencent.mobileqq:id/name";
        nickNameId = "com.tencent.mobileqq:id/chat_item_nick_name";
        headIconId = "com.tencent.mobileqq:id/chat_item_head_icon";
        messageId = "com.tencent.mobileqq:id/chat_item_content_layout";
        picId = "com.tencent.mobileqq:id/pic";
        timestampId = "com.tencent.mobileqq:id/chat_item_mobileqqe_stamp";
        recallId = "com.tencent.mobileqq:id/graybar";
        inputId = "com.tencent.mobileqq:id/input";
        sendBtnId = "com.tencent.mobileqq:id/fun_btn";
    }

    public void init(AccessibilityNodeInfo root){

        List<AccessibilityNodeInfo> groups = root.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/name");
        Log.i(TAG, "init: group.size: " + groups.size());
        for (AccessibilityNodeInfo group : groups) {
            GetNodes.show(group, 0);
            Log.i(TAG, "init: ");
        }

    }
}
