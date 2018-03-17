package com.qsboy.antirecall.access;

import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.qsboy.antirecall.db.Dao;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    boolean isGroupMessage;
    boolean isRecalledMessage;

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
        // TODO:
        // TODO:
        // TODO:
        // TODO:
        // TODO:
    }

    protected void getScreen(AccessibilityNodeInfo root) {
        GetNodes.show(root, "v");
        if (!init(root))
            return;
        for (int i = 0; i < chatGroupViewNode.getChildCount(); i++) {
            AccessibilityNodeInfo group = chatGroupViewNode.getChild(i);
            GetNodes.show(group, "d");
            parser(group);
        }
    }

}
