package com.qsboy.antirecall.access;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Date;


/**
 * Created by JasonQS
 */

public class QQClient extends Client {

    static String TAG = "QQ";

    AccessibilityNodeInfo inputBoxNode;
    AccessibilityNodeInfo titleGroupViewNode;

//    List<AccessibilityNodeInfo> inputList;
//    List<AccessibilityNodeInfo> sendList;
//    final String IdInput = "com.tencent.tim:id/input";
//    final String IdSend = "com.tencent.tim:id/fun_btn";

    final String IdTimeStamp = "com.tencent.mobileqq:id/chat_item_time_stamp";
    final String IdHeadIcon = "com.tencent.mobileqq:id/chat_item_head_icon";
    final String IdChatItem = "com.tencent.mobileqq:id/chat_item_content_layout";
    final String IdNickName = "com.tencent.mobileqq:id/chat_item_nick_name";
    final String IdGrayBar = "com.tencent.mobileqq:id/graybar";

    public QQClient() {
        packageName = "com.tencent.qq";
        nameId = "com.tencent.mobileqq:id/name";
        picId = "com.tencent.mobileqq:id/pic";
        chatGroupViewId = "com.tencent.mobileqq:id/listView1";
    }

    /**
     * 好友：
     * 姓名       last-3    android.widget.TextView
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
    public boolean init(AccessibilityNodeInfo root) {
        if (root.getChildCount() != 13) {
            Log.i(TAG, "init: root.childCount: " + root.getChildCount());
            return false;
        }

        titleGroupViewNode = root.getChild(root.getChildCount() - 1);
        int titleNodeChildCount = titleGroupViewNode.getChildCount();
        if (titleGroupViewNode == null) {
            Log.d(TAG, "init: name node is null, return");
            return false;
        }
        if (titleNodeChildCount != 4 && titleNodeChildCount != 5) {
            Log.d(TAG, "init: titleGroupViewNode child count is not 4 or 5, return");
            return false;
        }
        isGroupMessage = titleNodeChildCount != 4;

        nameNode = titleGroupViewNode.getChild(2);
        if (nameNode == null) {
            Log.d(TAG, "init: name node is null, return");
            return false;
        }
        if (!nameNode.getViewIdResourceName().equals(nameId)) {
            Log.d(TAG, "init: 名字ID不对，return");
            return false;
        }

        inputBoxNode = root.getChild(2);
        if (inputBoxNode == null || inputBoxNode.getChildCount() != 2) {
            Log.d(TAG, "init: inputBox is null, return");
            return false;
        }
        inputBoxNode = inputBoxNode.getChild(0);
        sendBtnNode = inputBoxNode.getChild(1);
        if (inputNode == null) {
            Log.d(TAG, "init: input node is null, return");
            return false;
        }
        if (sendBtnNode == null) {
            Log.d(TAG, "init: sendButton node is null, return");
            return false;
        }

        chatGroupViewNode = root.getChild(0);
        if (chatGroupViewNode == null) {
            Log.d(TAG, "init: chatView node is null, return");
            return false;
        }
        if (!chatGroupViewNode.getViewIdResourceName().equals(chatGroupViewId)) {
            Log.d(TAG, "init: not chat view, return");
            return false;
        }
        return true;
    }

    private void addMessage() {
        Date in = new Date();
        for (int i = 0; i < chatGroupViewNode.getChildCount(); i++) {
            AccessibilityNodeInfo group = chatGroupViewNode.getChild(i);
            GetNodes.show(group);
            for (int j = 0; j < group.getChildCount(); j++) {
                AccessibilityNodeInfo child = group.getChild(j);
                if (child == null) {
                    Log.d(TAG, "init: child is null, continue");
                    continue;
                }
                String nodeId = child.getViewIdResourceName();
                if (nodeId == null) {
                    Log.d(TAG, "init: node ID is null, continue");
                    continue;
                }
                switch (nodeId) {
                    case IdTimeStamp:
                        timestampNode = child;
                        break;
                    case IdHeadIcon:
                        headIconNode = child;
                        break;
                    case IdChatItem:
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
                    case IdNickName:
                        nickNameNode = child;
                        break;
                    case IdGrayBar:
                        if (!child.isClickable() && !child.isFocusable())
                            recallNode = child;

                }
            }
        }
        Date out = new Date();
        Log.i(TAG, "init: time: " + (out.getTime() - in.getTime()));
    }
}
