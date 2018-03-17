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

public class TimClient extends Client {

    String TAG = "Tim";

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

    public TimClient(Context context) {
        super(context);
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
    protected boolean init(AccessibilityNodeInfo root) {
        //16 是其他界面
//        if (root.getChildCount() != 14 && root.getChildCount() != 15) {
        Log.d(TAG, "init: root.childCount: " + root.getChildCount());
        if (root.getChildCount() < 14) {
            return false;
        }

        nameNode = root.getChild(1);
        // 通过群的即时聊天
        if (nameNode.getChildCount() == 2)
            nameNode = nameNode.getChild(0);
        if (!nameNode.getViewIdResourceName().equals(IdName)) {
            Log.d(TAG, "init: 名字ID不对，return");
            return false;
        }
        if (nameNode.getText() == null) {
            Log.d(TAG, "init: name is null，return");
            return false;
        }
        title = nameNode.getText().toString();

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

        // 群的 node 位置在4 好友聊天在5
        if ((chatGroupViewNode = root.getChild(4)).getViewIdResourceName().equals(IdChatGroupView))
            isGroupMessage = true;
        else if ((chatGroupViewNode = root.getChild(5)).getViewIdResourceName().equals(IdChatGroupView))
            isGroupMessage = false;
        else {
            Log.d(TAG, "init: not chat view, return");
            return false;
        }
        return true;
    }

    protected void parser(AccessibilityNodeInfo group) {
        isRecalledMessage = false;
        int childCount = group.getChildCount();

        for (int j = 0; j < childCount; j++) {
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
                    //群聊头像上面的群昵称最后有一个冒号
                    subName = child.getText().toString();
                    subName = subName.substring(0, subName.length() - 1);
                    break;
                case IdGrayBar:
                    // 撤回消息或者是有人加入
                    // 撤回消息的是不可点击的
                    // 接收文件和撤回消息一样
                    if (!child.isClickable() && !child.isFocusable()) {
                        message = child.getText().toString();
                        isRecalledMessage = true;
                    }

            }
        }
        //2人聊天 头像在消息右边
        Log.v(TAG, "parser: 群消息: " + isGroupMessage);
        if (!isGroupMessage)
            if (messagePos < headIconPos)
                subName = "我";
            else {
                subName = nameNode.getText().toString();
            }

        Log.i(TAG, subName + " : " + message);
    }
}
