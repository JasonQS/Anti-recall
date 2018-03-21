/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.access;

import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.qsboy.antirecall.db.Dao;

import java.util.Date;
import java.util.List;

public class QQClient extends Client{

    String TAG = "QQ";

    final String IdName = "com.tencent.mobileqq:id/title";
    final String IdChatGroupView = "com.tencent.mobileqq:id/listView1";
    final String IdTimeStamp = "com.tencent.mobileqq:id/chat_item_time_stamp";
    final String IdHeadIcon = "com.tencent.mobileqq:id/chat_item_head_icon";
    final String IdChatItem = "com.tencent.mobileqq:id/chat_item_content_layout";
    final String IdNickName = "com.tencent.mobileqq:id/chat_item_nick_name";
    final String IdGrayBar = "com.tencent.mobileqq:id/graybar";
    final String IdInput = "com.tencent.mobileqq:id/input";
    final String IdSend = "com.tencent.mobileqq:id/fun_btn";

    int headIconPos;
    int messagePos;

    public QQClient(Context context) {
        super(context);
        isWX = false;
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
        if (root.getChildCount() != 13) {
            // TODO: 非好友是10
            Log.i(TAG, "init: root.childCount: " + root.getChildCount());
            return false;
        }

        AccessibilityNodeInfo titleGroupViewNode = root.getChild(root.getChildCount() - 1);
        if (titleGroupViewNode == null) {
            Log.d(TAG, "init: subName node is null, return");
            return false;
        }
        int titleNodeChildCount = titleGroupViewNode.getChildCount();
        if (titleNodeChildCount != 5 && titleNodeChildCount != 6) {
            Log.d(TAG, "init: titleGroupViewNode child count is not 5 or 6, return");
            return false;
        }
        isGroupMessage = titleNodeChildCount == 6;

        nameNode = titleGroupViewNode.getChild(2);
        if (nameNode == null) {
            Log.d(TAG, "init: subName node is null, return");
            return false;
        }
        if (nameNode.getChildCount() > 0)
            nameNode = nameNode.getChild(0);
        if (!nameNode.getViewIdResourceName().equals(IdName)) {
            Log.d(TAG, "init: 名字ID不对，return");
            return false;
        }
        if (nameNode.getText() == null) {
            Log.i(TAG, "init: subName is null");
            return false;
        }

        inputList = root.findAccessibilityNodeInfosByViewId(IdInput);
        sendList = root.findAccessibilityNodeInfosByViewId(IdSend);
        if (inputList.size() == 0) {
            Log.d(TAG, "init: input is null, return");
            return false;
        }
        if (sendList.size() == 0) {
            Log.d(TAG, "init: send button is null, return");
            return false;
        }
        inputNode = inputList.get(0);
        sendBtnNode = sendList.get(0);

//        inputBoxNode = root.getChild(2);
//        if (inputBoxNode == null || inputBoxNode.getChildCount() != 2)
//            inputBoxNode = root.getChild(3);
//        if (inputBoxNode == null || inputBoxNode.getChildCount() != 2) {
//            Log.d(TAG, "init: inputBox is null, return");
//            return false;
//        }
//
//        inputNode = inputBoxNode.getChild(0);
//        sendBtnNode = inputBoxNode.getChild(1);
//        if (inputNode == null) {
//            Log.d(TAG, "init: input node is null, return");
//            return false;
//        }
//        if (sendBtnNode == null) {
//            Log.d(TAG, "init: sendButton node is null, return");
//            return false;
//        }

        chatGroupViewNode = root.getChild(0);
        if (chatGroupViewNode == null) {
            Log.d(TAG, "init: chatView node is null, return");
            return false;
        }
        if (!chatGroupViewNode.getViewIdResourceName().equals(IdChatGroupView)) {
            Log.d(TAG, "init: not chat view, return");
            return false;
        }
        return true;
    }

    protected void parser(AccessibilityNodeInfo group) {
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
                    //时间戳
                    break;
                case IdHeadIcon:
                    //头像图标
                    headIconPos = j;
                    break;
                case IdChatItem:
                    switch (child.getClassName().toString()) {
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
                                if (child1.getClassName() == "android.widget.TextView") {
                                    if (child2.getClassName() == "android.widget.TextView") {
                                        message = "回复 " + child1.getText() + ": \n" + child2.getText();
                                    }
                                }
                            }
                            groupNode = child;
                            Log.d(TAG, "content_layout: 组合消息");
                        }
                        break;
                        case "android.widget.TextView": {
                            message = child.getText().toString();
                            Log.d(TAG, "content_layout: 普通文本");
                        }
                        break;
                    }
                    messagePos = j;
                    break;
                case IdNickName:
                    subName = child.getText().toString();
                    break;
                case IdGrayBar:
//                        if (!child.isClickable() && !child.isFocusable())
                    // 撤回消息或者是有人加入
                    // 撤回消息的是不可点击的
            }
        }
        //2人聊天 头像在消息右边
        if (!isGroupMessage)
            if (messagePos < headIconPos)
                subName = "我";
            else
                subName = nameNode.getText().toString();
        Log.i(TAG, subName + " : " + message);
    }
}
