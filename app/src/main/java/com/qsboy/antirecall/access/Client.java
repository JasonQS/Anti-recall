/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.access;

import android.content.Context;
import android.graphics.Rect;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.qsboy.antirecall.db.Dao;
import com.qsboy.antirecall.db.Messages;
import com.qsboy.utils.XBitmap;
import com.qsboy.utils.XToast;

import java.util.Date;
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
        dao = Dao.getInstance(context);
        this.context = context;
    }

    protected abstract boolean init(AccessibilityNodeInfo root);

    protected abstract void parser(AccessibilityNodeInfo group);

    public void findRecalls(AccessibilityNodeInfo root, AccessibilityEvent event) {
        // TODO: 通知栏收到的表情 聊天框收到的表情 乱码 根据 utf 位置判断
        // TODO: 没找到 再根据 subName 找最后一个
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

        Log.w(TAG, "findRecalls: unknownRecalls: " + unknownRecalls + " prevMsg: " + prevMessage + " nextMsg: " + nextMessage);
        if (prevMessage == null && nextMessage == null) {
            XToast.build(context, "不能全屏撤回哦").show();
            return;
        }

        // TODO: 如果前后都没找到 就输出最后一个subName的消息
        // TODO: 查找到的要和subName做比较 如果不对要继续找
        Messages messages;
        if (prevMessage != null) {
            if (nextMessage != null)
                nextPos = dao.queryByMessage(title, isWX, nextSubName, nextMessage);
            prevPos = dao.queryByMessage(title, isWX, prevSubName, prevMessage);
            for (int i = 0; i < unknownRecalls; i++) {
                while ((messages = dao.queryById(title, isWX, prevPos + 1 + i)) == null) {
                    prevPos++;
                }
                dao.addRecall(messages, prevSubName, prevMessage, nextSubName, nextMessage);
                XToast.build(context, messages.getSubName() + ": " + messages.getMessage()).show();
            }
        } else {
            nextPos = dao.queryByMessage(title, isWX, nextSubName, nextMessage);
            for (int i = unknownRecalls - 1; i >= 0; i--) {
                while ((messages = dao.queryById(title, isWX, nextPos - 1 - i)) == null) {
                    nextPos--;
                }
                dao.addRecall(messages, prevSubName, prevMessage, nextSubName, nextMessage);
                XToast.build(context, messages.getSubName() + ": " + messages.getMessage()).show();
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
            // TODO: 特别关心
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

    public void addRecall() {
//        if ("[图片]".equals(message))
//            if (XBitmap.searchImageFile(new Date().getTime(),TAG))
    }
}
