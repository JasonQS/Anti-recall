//package com.qsboy.antirecall.access;
//
//import android.util.Log;
//import android.view.accessibility.AccessibilityNodeInfo;
//
//import java.util.Date;
//
//
///**
// * Created by JasonQS
// */
//
//public class WXClient {
//
//    static String TAG = "Wx";
//
//    AccessibilityNodeInfo inputBoxNode;
//    AccessibilityNodeInfo titleGroupViewNode;
//
////    List<AccessibilityNodeInfo> inputList;
////    List<AccessibilityNodeInfo> sendList;
////    final String IdInput = "com.tencent.tim:id/input";
////    final String IdSend = "com.tencent.tim:id/fun_btn";
//
////    final String IdTimeStamp = "com.tencent.mobileqq:id/chat_item_time_stamp";
////    final String IdHeadIcon = "com.tencent.mobileqq:id/chat_item_head_icon";
////    final String IdChatItem = "com.tencent.mobileqq:id/chat_item_content_layout";
////    final String IdNickName = "com.tencent.mobileqq:id/chat_item_nick_name";
////    final String IdGrayBar = "com.tencent.mobileqq:id/graybar";
//
//    public WXClient() {
//        packageName = "com.tencent.wx";
//    }
//
//    public boolean init(AccessibilityNodeInfo root) {
//        if (root.getChildCount() != 1) {
//            Log.i(TAG, "init: root.childCount: " + root.getChildCount());
//            return false;
//        }
//        AccessibilityNodeInfo child0 = root.getChild(0);
//        if (child0.getChildCount() != 5) {
//            Log.i(TAG, "init: root.child(0).childCount: " + child0.getChildCount());
//            return false;
//        }
//
//        nameNode = child0.getChild(1);
//        if (nameNode == null) {
//            Log.d(TAG, "init: name node is null, return");
//            return false;
//        }
//        if (nameNode.getText() != null) {
//            name = nameNode.getText().toString();
//            Log.i(TAG, "init: name: " + name);
//        }
//
//        inputBoxNode = child0.getChild(4).getChild(0).getChild(1);
//        sendBtnNode = child0.getChild(4).getChild(0).getChild(3);
//        if (inputNode == null) {
//            Log.d(TAG, "init: input node is null, return");
//            return false;
//        }
//        chatGroupViewNode = child0.getChild(3);
//        if (chatGroupViewNode == null) {
//            Log.d(TAG, "init: chatView node is null, return");
//            return false;
//        }
//
//        Date in = new Date();
//        for (int i = 0; i < chatGroupViewNode.getChildCount(); i++) {
//            AccessibilityNodeInfo group = chatGroupViewNode.getChild(i);
//            GetNodes.show(group);
//            for (int j = 0; j < group.getChildCount(); j++) {
//                AccessibilityNodeInfo child = group.getChild(j);
//                if (child == null) {
//                    Log.d(TAG, "init: child is null, continue");
//                    continue;
//                }
//                String nodeId = child.getViewIdResourceName();
//                if (nodeId == null) {
//                    Log.d(TAG, "init: node ID is null, continue");
//                    continue;
//                }
//            }
//        }
//        Date out = new Date();
//        Log.i(TAG, "init: time: " + (out.getTime() - in.getTime()));
//        return true;
//    }
//}
