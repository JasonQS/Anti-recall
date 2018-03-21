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
import com.qsboy.antirecall.db.Messages;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by JasonQS
 */

public abstract class Client {

    AccessibilityNodeInfo nameNode;
    AccessibilityNodeInfo chatGroupViewNode;
    AccessibilityNodeInfo groupNode;
    AccessibilityNodeInfo redPegNode;
    AccessibilityNodeInfo inputNode;
    AccessibilityNodeInfo sendBtnNode;
    List<AccessibilityNodeInfo> inputList;
    List<AccessibilityNodeInfo> sendList;

    static String added = "";
    String TAG = "Client";
    String title = "";
    String subName = "";
    String message = "";
    String RECALL = "撤回了一条消息";
    boolean isGroupMessage;
    boolean isRecalledMessage;
    boolean isWX;
    int unknonRecalls;

    Dao dao;

    public Client(Context context) {
        dao = new Dao(context);
    }

    protected abstract boolean init(AccessibilityNodeInfo root);

    protected abstract void parser(AccessibilityNodeInfo group);

    public void addMessage(AccessibilityNodeInfo root) {
        Date in1 = new Date();

        if (!init(root))
            return;

        Date out1 = new Date();
        Date in = new Date();

        AccessibilityNodeInfo group = chatGroupViewNode.getChild(chatGroupViewNode.getChildCount() - 1);
        GetNodes.show(group, "d");
        parser(group);

        //如果和上条内容一样 则不添加
        if (added.equals(added = title + " " + subName + " " + message))
            return;

        Log.e(TAG, "addMessage: " + added);
        dao.addMessage(title, subName, false, message);

        Date out = new Date();
        Log.v(TAG, "init: time: " + (out1.getTime() - in1.getTime()));
        Log.v(TAG, "add : time: " + (out.getTime() - in.getTime()));
    }

    public void findMessage(AccessibilityNodeInfo root) {
        // TODO:找到撤回位置的上下文
        List<String> screenList = getScreen(root);
        for (int i = 1; i < screenList.size(); i++) {
            String s = screenList.get(i);
            if (s.contains(RECALL)) {
                String[] content = screenList.get(i - 1).split(" ");
                List<Messages> messagesList = dao.queryByMessage(title, isWX, content[0], content[1]);
            }
        }
        // TODO:
        // TODO:找到撤回的前一条 找到撤回
        // TODO:没有前一条 找到撤回的后一条 找到撤回
    }

    protected List<String> getScreen(AccessibilityNodeInfo root) {

        GetNodes.show(root, "v");
        List<String> list = new ArrayList<>();
        if (!init(root))
            return list;
        for (int i = 0; i < chatGroupViewNode.getChildCount(); i++) {
            AccessibilityNodeInfo group = chatGroupViewNode.getChild(i);
            GetNodes.show(group, "d");
            parser(group);
            list.add(subName + " " + message);
        }
        return list;
    }

}
