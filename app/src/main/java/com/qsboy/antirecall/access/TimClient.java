package com.qsboy.antirecall.access;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.qsboy.antirecall.utils.GetNodes;

import java.util.Date;
import java.util.List;


/**
 * Created by JasonQS
 */

public class TimClient extends Client {

    static String TAG = "Tim";

    List<AccessibilityNodeInfo> inputList;
    List<AccessibilityNodeInfo> sendList;
    final String IdTimeStamp = "com.tencent.tim:id/chat_item_time_stamp";
    final String IdHeadIcon = "com.tencent.tim:id/chat_item_head_icon";
    final String IdChatItem = "com.tencent.tim:id/chat_item_content_layout";
    final String IdNickName = "com.tencent.tim:id/chat_item_nick_name";
    final String IdGrayBar = "com.tencent.tim:id/graybar";
    final String IdInput = "com.tencent.tim:id/input";
    final String IdSend = "com.tencent.tim:id/fun_btn";

    public TimClient() {
        packageName = "com.tencent.tim";
        nameId = "com.tencent.tim:id/title";
        chatGroupViewId = "com.tencent.tim:id/listView1";
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

        if (root.getChildCount() < 10)
            return;

        nameNode = root.getChild(1);
        if (nameNode == null) {
            Log.d(TAG, "init: name node is null, return");
            return;
        }
        if (!nameNode.getViewIdResourceName().equals(nameId)) {
            Log.d(TAG, "init: 名字ID不对，return");
            return;
        }

        inputList = root.findAccessibilityNodeInfosByViewId(IdInput);
        sendList = root.findAccessibilityNodeInfosByViewId(IdSend);
        if (inputList.size() == 0) {
            Log.d(TAG, "init: input is null, return");
            return;
        }
        if (sendList.size() == 0) {
            Log.d(TAG, "init: send button is null, return");
            return;
        }
        inputNode = inputList.get(0);
        sendBtnNode = sendList.get(0);
        if (inputNode == null) {
            Log.d(TAG, "init: input node is null, return");
            return;
        }
        if (sendBtnNode == null) {
            Log.d(TAG, "init: sendButton node is null, return");
            return;
        }

        chatGroupViewNode = root.getChild(4);
        if (chatGroupViewNode.getViewIdResourceName().equals(chatGroupViewId))
            isGroupMessage = false;
        else
            chatGroupViewNode = root.getChild(5);
        if (chatGroupViewNode == null) {
            Log.d(TAG, "init: chatView node is null, return");
            return;
        }
        else isGroupMessage = true;
        if (!chatGroupViewNode.getViewIdResourceName().equals(chatGroupViewId)) {
            Log.d(TAG, "init: not chat view, return");
            return;
        }

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
