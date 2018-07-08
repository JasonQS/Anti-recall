///*
// * Copyright © 2016 - 2018 by GitHub.com/JasonQS
// * anti-recall.qsboy.com
// * All Rights Reserved
// */
//
//package com.qsboy.antirecall.temp;
//
//import android.util.Log;
//import android.view.accessibility.AccessibilityNodeInfo;
//
//import com.qsboy.antirecall.utils.NodesInfo;
//
//import java.util.List;
//
//public class QQ {
//
//    final String TAG = "QQ";
//    final String IdTitle = "com.tencent.mobileqq:id/title";
//    final String IdChatGroupView = "com.tencent.mobileqq:id/listView1";
//    final String IdHeadIcon = "com.tencent.mobileqq:id/chat_item_head_icon";
//    final String IdChatItem = "com.tencent.mobileqq:id/chat_item_content_layout";
//    final String IdNickName = "com.tencent.mobileqq:id/chat_item_nick_name";
//    final String IdOtherMsg = "com.tencent.mobileqq:id/msgbox";
//    final String IdGrayBar = "com.tencent.mobileqq:id/graybar";
//    final String IdInput = "com.tencent.mobileqq:id/input";
//    final String IdSend = "com.tencent.mobileqq:id/fun_btn";
//
//    protected AccessibilityNodeInfo inputNode;
//    protected AccessibilityNodeInfo sendBtnNode;
//
//    protected boolean init(AccessibilityNodeInfo root) {
//        if (root.getChildCount() < 10) {
//            // 正常是13
//            // 有其他消息是14
//            // 非好友是10
//            Log.v(TAG, "init: root.childCount: " + root.getChildCount());
//            return false;
//        }
//
//        List<AccessibilityNodeInfo> inputList;
//        List<AccessibilityNodeInfo> sendList;
//        inputList = root.findAccessibilityNodeInfosByViewId(IdInput);
//        sendList = root.findAccessibilityNodeInfosByViewId(IdSend);
//        if (inputList.size() == 0) {
//            Log.d(TAG, "init: input is null, return");
//            return false;
//        }
//        if (sendList.size() == 0) {
//            Log.d(TAG, "init: send button is null, return");
//            return false;
//        }
//        inputNode = inputList.get(0);
//        sendBtnNode = sendList.get(0);
//
//        List<AccessibilityNodeInfo> titleList;
//        titleList = root.findAccessibilityNodeInfosByViewId(IdTitle);
//        if (titleList.size() == 0) {
//            Log.d(TAG, "init: title is null, return");
//            return false;
//        }
//        titleNode = titleList.get(0);
//        if (titleNode.getText() == null) {
//            Log.d(TAG, "init: name is null，return");
//            return false;
//        }
//        title = titleNode.getText() + "";
//
//        chatGroupViewNode = root.getChild(0);
//        if (chatGroupViewNode == null) {
//            Log.d(TAG, "init: chatView node is null, return");
//            return false;
//        }
//        if (!IdChatGroupView.equals(chatGroupViewNode.getViewIdResourceName())) {
//            Log.d(TAG, "init: not chat view, return");
//            return false;
//        }
//
//        isOtherMsg = IdOtherMsg.equals(root.getChild(1).getViewIdResourceName());
//
//        return true;
//    }
//    public MsgInfo parser() {
//        MsgInfo msg = new MsgInfo();
//
//
//        return msg;
//    }
//}
