/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.access;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.qsboy.antirecall.db.Dao;
import com.qsboy.antirecall.db.Messages;
import com.qsboy.utils.XToast;

import java.util.List;

public abstract class Client {

    AccessibilityNodeInfo titleNode;
    AccessibilityNodeInfo chatGroupViewNode;
    AccessibilityNodeInfo groupNode;
    AccessibilityNodeInfo redPegNode;
    AccessibilityNodeInfo otherMsgNode;
    AccessibilityNodeInfo inputNode;
    AccessibilityNodeInfo sendBtnNode;

    static String added = "";
    String TAG = "Client";
    String title = "";
    String subName = "";
    String message = "";
    String RECALL = "撤回了一条消息";
    boolean isRecalledMsg;
    boolean isOtherMsg;
    boolean isWX;
    int unknownRecalls;

    //    public static List<String> tables;
    Dao dao;
    private Context context;

    String nextMessage;
    String prevMessage;
    String nextSubName;
    String prevSubName;
    int prevPos;
    int nextPos;

    public Client(Context context) {
        dao = new Dao(context);
        this.context = context;
    }

    protected abstract boolean init(AccessibilityNodeInfo root);

    protected abstract void parser(AccessibilityNodeInfo group);

    public void findRecalls(AccessibilityNodeInfo root, AccessibilityEvent event) {
        CharSequence cs = event.getSource().getText();
        if (cs == null)
            return;
        String string = cs.toString();
        if (!string.contains(RECALL))
            return;

        if (!init(root))
            return;
        GetNodes.show(root, TAG);

        initContext(event);

        Log.i(TAG, "findRecalls: unknownRecalls: " + unknownRecalls + " prevMsg: " + prevMessage + " nextMsg: " + nextMessage);
        if (prevMessage == null && nextMessage == null) {
            XToast.makeText(context, "不能全屏撤回哦").show();
            // TODO: toast: 不能全屏的撤回
            return;
        }

        if (prevMessage != null) {
            if (nextMessage != null)
                nextPos = dao.queryByMessage(title, isWX, nextSubName, nextMessage);
            prevPos = dao.queryByMessage(title, isWX, prevSubName, prevMessage);
            for (int i = 0; i < unknownRecalls; i++) {
                Messages messages = dao.queryById(title, isWX, prevPos + 1 + i);
                dao.addRecall(messages, prevSubName, prevMessage, nextSubName, nextMessage);
                XToast.makeText(context, messages.getSubName() + ": " + messages.getMessage()).setPos(i).show();
            }
        } else {
            nextPos = dao.queryByMessage(title, isWX, nextSubName, nextMessage);
            for (int i = unknownRecalls - 1; i >= 0; i--) {
                Messages messages = dao.queryById(title, isWX, nextPos - 1 - i);
                dao.addRecall(messages, prevSubName, prevMessage, nextSubName, nextMessage);
                XToast.makeText(context, messages.getSubName() + ": " + messages.getMessage()).setPos(i).show();
            }
        }

    }

    private void initContext(AccessibilityEvent event) {
        prevMessage = null;
        nextMessage = null;
        prevSubName = null;
        nextSubName = null;
        int topPos = 0;
        int botPos = chatGroupViewNode.getChildCount();

        Rect clickRect = new Rect();
        Rect nodeRect = new Rect();
        int pos = 0;

        event.getSource().getBoundsInScreen(clickRect);

        for (int i = 0; i < chatGroupViewNode.getChildCount(); i++) {
            AccessibilityNodeInfo group = chatGroupViewNode.getChild(i);
            parser(group);
            chatGroupViewNode.getChild(i).getBoundsInScreen(nodeRect);
            //当前点击的地方
            if (nodeRect.contains(clickRect))
                pos = i;
            //获取上下文
            if (pos == 0) {
                if (!message.contains(RECALL)) {
                    prevMessage = message;
                    prevSubName = subName;
                    topPos = i;
                }
            } else {
                if (!message.contains(RECALL)) {
                    nextMessage = message;
                    nextSubName = subName;
                    botPos = i;
                    break;
                }
            }
        }
        unknownRecalls = botPos - topPos - 1;
    }

    public void onContentChanged(AccessibilityNodeInfo root) {
        if (!init(root))
            return;
        GetNodes.show(root, TAG);
        if (isOtherMsg) {
            onOtherMsg();
            return;
        }

        AccessibilityNodeInfo group = chatGroupViewNode.getChild(chatGroupViewNode.getChildCount() - 1);
        if (group == null)
            return;
        parser(group);

        addMsg(false);

    }

    public void onNotificationChanged(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (texts.isEmpty() || texts.size() == 0)
            return;
        for (CharSequence text : texts) {
            if (text == null)
                return;
            String string = text.toString();
            Log.w(TAG, "Notification text: " + string);
            if (string.equals("你的帐号在电脑登录"))
                return;

            int i = string.indexOf(':');
            if (i < 1) {
                Log.d(TAG, "Notification does not contains ':'");
                return;
            }
            title = string.substring(0, i);
            message = string.substring(i + 2);
            subName = title;
            //是群消息
            // TODO: 当前是 QQ 群 微信群待测试
            int j = title.indexOf('(');
            if (j > 0 && title.charAt(i - 1) == ')') {
                message = string.substring(i + 1);
                subName = title.substring(0, j);
                title = title.substring(j + 1, i - 1);
            }

            addMsg(true);
        }
    }

    /**
     * 判断是否是在其他人的聊天界面收到了消息
     * 为了在 QQ-不是当前联系人-发来消息 时检查是否出现过这个人
     * QQ比较重,会在当前屏幕生成一个内部的弹窗
     * 这种消息我截下来和普通消息一样,只是内容是这样的形式:
     * "Name" + ' : ' + "Message"
     * 我根据这里是否存在冒号
     * 然后判断Name是否在NameList中来区分 QQ-普通消息和别人发的消息
     * 但微信不一样,只要是不在当前聊天窗口发来的消息都会给Notification
     */
    private void onOtherMsg() {
        String string = otherMsgNode.getText().toString();
        int i = string.indexOf(":");
        int j = string.lastIndexOf("-");
        //如果在联系人列表里出现过的,那么就是在其他人的聊天界面
        message = string.substring(i + 1);
        //包含"-" 可能是群
        if (j > 0) {
            title = string.substring(0, j);
            subName = string.substring(j + 1, i);
            addMsg(false);
            //也可能不是群
//            if (tables.contains(DBHelper.Table_Prefix_QQ_And_Tim + string.substring(0, i))) {
//                addMsg(false);
//                return;
//            }
        } else {
            title = string.substring(0, i);
            //如果人名在tables. 出现过
            addMsg(false);
        }
    }


    public void addMsg(boolean force) {
        String temp = title + "-" + subName + ": " + message;
        if (!force)
            if (added.equals(temp))
                return;
        added = temp;
        Log.e(TAG, "Add message: " + temp);
        dao.addMessage(title, subName, isWX, message);
    }
}
