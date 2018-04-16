/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.access;

import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class QQClient extends Client {

    String TAG = "QQ";

    final String IdTitle = "com.tencent.mobileqq:id/title";
    final String IdChatGroupView = "com.tencent.mobileqq:id/listView1";
    final String IdHeadIcon = "com.tencent.mobileqq:id/chat_item_head_icon";
    final String IdChatItem = "com.tencent.mobileqq:id/chat_item_content_layout";
    final String IdNickName = "com.tencent.mobileqq:id/chat_item_nick_name";
    final String IdOtherMsg = "com.tencent.mobileqq:id/msgbox";
    final String IdGrayBar = "com.tencent.mobileqq:id/graybar";
    final String IdInput = "com.tencent.mobileqq:id/input";
    final String IdSend = "com.tencent.mobileqq:id/fun_btn";

    int headIconPos;
    int messagePos;

    public QQClient(Context context) {
        super(context);
        isWX = false;
        client = "QQ";
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
    protected boolean init(AccessibilityNodeInfo root) {
        if (root.getChildCount() < 10) {
            // 正常是13
            // 有其他消息是14
            // 非好友是10
            Log.i(TAG, "init: root.childCount: " + root.getChildCount());
            return false;
        }
        List<AccessibilityNodeInfo> titleList;
        List<AccessibilityNodeInfo> inputList;
        List<AccessibilityNodeInfo> sendList;

        titleList = root.findAccessibilityNodeInfosByViewId(IdTitle);
        inputList = root.findAccessibilityNodeInfosByViewId(IdInput);
        sendList = root.findAccessibilityNodeInfosByViewId(IdSend);
        if (titleList.size() == 0) {
            Log.d(TAG, "init: title is null, return");
            return false;
        }
        if (inputList.size() == 0) {
            Log.d(TAG, "init: input is null, return");
            return false;
        }
        if (sendList.size() == 0) {
            Log.d(TAG, "init: send button is null, return");
            return false;
        }
        titleNode = titleList.get(0);
        inputNode = inputList.get(0);
        sendBtnNode = sendList.get(0);
        if (titleNode.getText() == null) {
            Log.d(TAG, "init: name is null，return");
            return false;
        }
        title = titleNode.getText() + "";

        chatGroupViewNode = root.getChild(0);
        if (chatGroupViewNode == null) {
            Log.d(TAG, "init: chatView node is null, return");
            return false;
        }
        if (!IdChatGroupView.equals(chatGroupViewNode.getViewIdResourceName())) {
            Log.d(TAG, "init: not chat view, return");
            return false;
        }

        isOtherMsg = IdOtherMsg.equals(root.getChild(1).getViewIdResourceName());

        return true;
    }

    protected void parser(AccessibilityNodeInfo group) {
        subName = "";
        message = "";
        isRecalledMsg = false;
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
                case IdHeadIcon:
                    //头像图标
                    headIconPos = j;
                    break;
                case IdChatItem:
                    switch (child.getClassName() + "") {
                        case "android.widget.RelativeLayout":
                            if (child.getChildCount() != 0) {
                                if (child.getContentDescription() != null) {
                                    redPegNode = child;
                                    message = "红包";
                                    //红包或者是分享
                                    Log.d(TAG, "content_layout: 红包");
                                }
                            } else {
                                message = "[图片]";
                                Log.d(TAG, "content_layout: 图片");
                            }
                            break;
                        case "android.widget.LinearLayout": {
                            if (child.getChildCount() == 2) {
                                AccessibilityNodeInfo child1 = child.getChild(0);
                                AccessibilityNodeInfo child2 = child.getChild(1);
                                if (child1 != null && "android.widget.RelativeLayout".equals(child1.getClassName())) {
                                    if (child2 != null && "android.widget.TextView".equals(child2.getClassName())) {
//                                        message = "回复 " + child1.getText() + ": \n" + child2.getText();
                                        message = child2.getText() + "";
                                    }
                                }
                            }
                            Log.d(TAG, "content_layout: 回复消息");
                        }
                        break;
                        case "android.widget.TextView": {
                            message = child.getText() + "";
                            Log.v(TAG, "content_layout: 普通文本");
                        }
                        break;
                    }
                    messagePos = j;
                    break;
                case IdNickName:
                    subName = child.getText() + "";
                    break;
                case IdGrayBar:
                    if (!child.isClickable() && !child.isFocusable()) {
                        message = child.getText() + "";
                        int indexOfRecall = message.indexOf(RECALL);
                        if (indexOfRecall >= 0) {
                            isRecalledMsg = true;
                            subName = message.substring(0, indexOfRecall);
                            message = message.substring(indexOfRecall);
                            if ("对方".equals(subName))
                                subName = title;
                            else if ("你".equals(subName))
                                subName = "我";
                        }
                    }
            }
        }
        if (messagePos < headIconPos)
            // 消息在头像左边
            subName = "我";
        else if ("".equals(subName))
            // 两人聊天时 没有subName
            subName = title;
        Log.d(TAG, "parser: " + title + " - " + subName + " : " + message);
    }


}
