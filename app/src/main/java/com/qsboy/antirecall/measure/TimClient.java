package com.qsboy.antirecall.measure;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.qsboy.antirecall.utils.GetNodes;

import java.util.List;

/**
 * Created by JasonQS
 */

public class TimClient extends Client {

    static String TAG = "Tim";

    public TimClient() {
        nameId = "com.tencent.tim:id/title";
        chatViewId = "com.tencent.tim:id/listView1";
        picId = "com.tencent.tim:id/pic";
    }


    /**
     * 好友：
     * 姓名       1    android.widget.TextView
     * 消息       5-[]-last android.widget.TextView   focusable
     * 文本框     7-0  android.widget.EditText
     * 发送按钮   7-1  android.widget.Button
     * <p>
     * 群：
     * 群名       1
     * 消息       4/5-
     * <p>
     * 群里的临时会话：
     * 姓名       1-0
     * 消息       6-[]-last
     * 文本框     8-0
     * 发送按钮   8-1
     */
    public void init(AccessibilityNodeInfo root) {
        packageName = "com.tencent.tim";
        if (root.getChildCount() < 10)
            return;
        nameNode = root.getChild(1);
        if (!nameNode.getViewIdResourceName().equals(nameId)) {
            Log.d(TAG, "init: 名字ID不对，return");
            return;
        }

        inputNode = root.findAccessibilityNodeInfosByViewId("com.tencent.tim:id/input").get(0);
        sendBtnNode = root.findAccessibilityNodeInfosByViewId("com.tencent.tim:id/fun_btn").get(0);

        chatViewNode = root.getChild(4);
        if (!chatViewNode.getViewIdResourceName().equals(chatViewId))
            chatViewNode = root.getChild(5);
        if (!chatViewNode.getViewIdResourceName().equals(chatViewId)) {
            Log.d(TAG, "init: not chat view, return");
            return;
        }

        if (nameNode == null) {
            Log.d(TAG, "init: name node is null, return");
            return;
        }
        if (inputNode == null) {
            Log.d(TAG, "init: input node is null, return");
            return;
        }
        if (sendBtnNode == null) {
            Log.d(TAG, "init: sendButton node is null, return");
            return;
        }
        if (chatViewNode == null) {
            Log.d(TAG, "init: chatView node is null, return");
            return;
        }

        for (int i = 0; i < chatViewNode.getChildCount(); i++) {
            AccessibilityNodeInfo group = chatViewNode.getChild(i);
            GetNodes.show(group, 0);
            for (int j = 0; j < group.getChildCount(); j++) {
                AccessibilityNodeInfo child = group.getChild(j);
                switch (child.getViewIdResourceName()) {
                    case "com.tencent.tim:id/chat_item_time_stamp":
                        timestampNode = child;
                        break;
                    case "com.tencent.tim:id/chat_item_head_icon":
                        headIconNode = child;
                        break;
                    case "com.tencent.tim:id/chat_item_content_layout":
                        switch (child.getClassName().toString()) {
                            case "android.widget.RelativeLayout":
                                if (child.getChildCount() != 0) {
                                    if (child.getContentDescription() != null) {
                                        redPegNode = child;
                                        Log.d(TAG, "content_layout: 红包");
                                    }
                                } else {
                                    picNode = child;
                                    Log.d(TAG, "content_layout: 图片");
                                }
                                break;
                            case "android.widget.LinearLayout": {
                                groupNode = child;
                                Log.d(TAG, "content_layout: 组合消息");
                            }
                            break;
                            case "android.widget.TextView": {
                                messageNode = child;
                                Log.d(TAG, "content_layout: 普通文本");
                            }
                            break;
                        }
//                        if (child.getChildCount() != 0)
                        // TODO: 2017/10/23 多类型消息
                        break;
                    case "com.tencent.tim:id/chat_item_nick_name":
                        nickNameNode = child;
                        break;
                    case "com.tencent.tim:id/graybar":
                        if (!child.isClickable() && !child.isFocusable())
                            recallNode = child;

                }
            }
        }

    }
}
