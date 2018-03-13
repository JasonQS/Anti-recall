package com.qsboy.antirecall.access;

import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.qsboy.antirecall.db.Dao;

import java.util.Date;
import java.util.List;


/**
 * Created by JasonQS
 */

public class TimClient {

    AccessibilityNodeInfo nameNode;
    AccessibilityNodeInfo chatGroupViewNode;
    AccessibilityNodeInfo groupNode;
    AccessibilityNodeInfo redPegNode;
    AccessibilityNodeInfo inputNode;
    AccessibilityNodeInfo sendBtnNode;
    List<AccessibilityNodeInfo> inputList;
    List<AccessibilityNodeInfo> sendList;

    final String TAG = "Tim";

    final String IdName = "com.tencent.tim:id/title";
    final String IdChatGroupView = "com.tencent.tim:id/listView1";
    final String IdTimeStamp = "com.tencent.tim:id/chat_item_time_stamp";
    final String IdHeadIcon = "com.tencent.tim:id/chat_item_head_icon";
    final String IdChatItem = "com.tencent.tim:id/chat_item_content_layout";
    final String IdNickName = "com.tencent.tim:id/chat_item_nick_name";
    final String IdGrayBar = "com.tencent.tim:id/graybar";
    final String IdInput = "com.tencent.tim:id/input";
    final String IdSend = "com.tencent.tim:id/fun_btn";

    int headIconPos;
    int messagePos;
    String message;
    String name;
    boolean isGroupMessage;

    Dao dao;

    TimClient(Context context){
        dao = new Dao(context);
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
    public boolean init(AccessibilityNodeInfo root) {
        if (root.getChildCount() != 14 && root.getChildCount() != 15) {
            Log.d(TAG, "init: root.childCount: " + root.getChildCount());
            return false;
        }

        nameNode = root.getChild(1);
        if (nameNode == null) {
            Log.d(TAG, "init: name node is null, return");
            return false;
        }
        if (!nameNode.getViewIdResourceName().equals(IdName)) {
            Log.d(TAG, "init: 名字ID不对，return");
            return false;
        }
        if (nameNode.getText() == null) {
            Log.i(TAG, "init: name is null");
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
        if (inputNode == null) {
            Log.d(TAG, "init: input node is null, return");
            return false;
        }
        if (sendBtnNode == null) {
            Log.d(TAG, "init: sendButton node is null, return");
            return false;
        }

        chatGroupViewNode = root.getChild(4);
        if (chatGroupViewNode.getViewIdResourceName().equals(IdChatGroupView))
            isGroupMessage = false;
        else
            chatGroupViewNode = root.getChild(5);
        if (chatGroupViewNode == null) {
            Log.d(TAG, "init: chatView node is null, return");
            return false;
        } else isGroupMessage = true;
        if (!chatGroupViewNode.getViewIdResourceName().equals(IdChatGroupView)) {
            Log.d(TAG, "init: not chat view, return");
            return false;
        }
        return true;
    }

    public void addMessage(AccessibilityNodeInfo root) {
        Date in1 = new Date();
        if (!init(root))
            return;
        Date out1 = new Date();
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
                                        // TODO: 红包或者是分享
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
                        name = child.getText().toString();
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
                    name = "我";
                else
                    name = nameNode.getText().toString();

            Log.i(TAG, name + " : " + message);
        }

        Date out = new Date();
        Log.i(TAG, "init: time: " + (out1.getTime() - in1.getTime()));
        Log.i(TAG, "add : time: " + (out.getTime() - in.getTime()));
    }
}
