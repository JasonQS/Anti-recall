/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.access;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.qsboy.antirecall.db.Dao;
import com.qsboy.antirecall.db.Messages;
import com.qsboy.utils.NodesInfo;
import com.qsboy.utils.XToast;

import java.util.ArrayList;
import java.util.List;

import static com.qsboy.utils.ImageHelper.searchImageFile;

public abstract class Client {

    AccessibilityNodeInfo titleNode;
    AccessibilityNodeInfo chatGroupViewNode;
    AccessibilityNodeInfo redPegNode;
    AccessibilityNodeInfo otherMsgNode;
    AccessibilityNodeInfo inputNode;
    AccessibilityNodeInfo sendBtnNode;

    static String added = "";
    String TAG = "Client";
    String title = "";
    String subName = "";
    String message = "";
    String pSubName = "";
    String pMessage = "";
    String RECALL = "撤回了一条消息";
    String client;
    boolean isRecalledMsg;
    boolean isOtherMsg;
    boolean isWX;

    private Dao dao;
    private Context context;

    public Client(Context context) {
        dao = Dao.getInstance(context);
        this.context = context;
    }

    protected abstract boolean init(AccessibilityNodeInfo root);

    protected abstract void parser(AccessibilityNodeInfo group);

    void findRecalls(AccessibilityNodeInfo root, AccessibilityEvent event) {
        new Recalls().findRecalls(root, event);
    }

    private class Recalls {

        private List<Messages> contextList = new ArrayList<>();
        private String nextMessage;
        private String prevMessage;
        private String nextSubName;
        private String prevSubName;
        private int unknownRecalls;
        private ArrayList<String> subNameArray = new ArrayList<>();

        void findRecalls(AccessibilityNodeInfo root, AccessibilityEvent event) {
            // TODO: 通知栏收到的表情 聊天框收到的表情 乱码 根据 utf 位置判断
            CharSequence cs = event.getSource().getText();
            if (cs == null)
                return;
            String string = cs + "";
            // 点击的是撤回消息
            if (!string.contains(RECALL))
                return;

            if (!init(root))
                return;

            NodesInfo.show(root, TAG);

            initContext(event);

            Log.w(TAG, "findRecalls: \nunknown Recalls: " + unknownRecalls +
                    " \nprev Msg: " + prevSubName + " - " + prevMessage +
                    " \nnext Msg: " + nextSubName + " - " + nextMessage);

            if (prevMessage == null && nextMessage == null) {
                XToast.build(context, "不能全屏撤回哦").show();
                return;
            }

            // TODO: 如果上一条是图片 name 根据上上条找
            ArrayList<Integer> prevList = dao.queryByMessage(title, isWX, prevSubName, prevMessage);
            ArrayList<Integer> nextList = dao.queryByMessage(title, isWX, nextSubName, nextMessage);
            Log.i(TAG, "findRecalls: prevList: " + prevList);
            Log.i(TAG, "findRecalls: nextList: " + nextList);

            if (nextList.size() == 0) {
                // 没有下文
                if (prevList.size() == 0) {
                    // 没有上下文
                    Messages message = findPrev(dao.getMaxID(title, isWX), subName);
                    if (message != null) {
                        addRecall(message);
                        XToast.build(context, "该条消息不保证正确").show();
                    } else
                        notFound();
                } else
                    // 只有上文
                    findRecallByPrev(prevList.get(0));
            } else
                // 有下文
                if (prevList.size() == 0)
                    // 只有下文
                    findRecallByNext(nextList.get(0));
                else
                    // 上下文都有
                    findRecallByContext(prevList, nextList);
        }

        private void findRecallByContext(ArrayList<Integer> prevList, ArrayList<Integer> nextList) {
            int prevPos = -1;
            int nextPos = -1;
            int distance = unknownRecalls + 5;
            for (Integer p : prevList) {
                for (Integer n : nextList) {
                    if (n - p == unknownRecalls + 1) {
                        // 如果上下文的距离刚好就是撤回的数量 那就直接拿来找
                        prevPos = p;
                        nextPos = n;
                        break;
                    } else
                        // 如果上下文之间漏加了或者多加了 那就取最近的一对
                        if (n - p < distance) {
                            // 找到距离最近的
                            distance = n - p;
                            prevPos = p;
                            nextPos = n;
                        }
                }
            }
            Log.i(TAG, "findRecallByContext: [ " + prevPos + " - " + nextPos + " ]");

            if (prevPos == -1)
                // 上文或者下文没截到 导致distance过大
                // 但是极大概率是上文沒找到 所以按照只有下文的情况找
                findRecallByNext(nextList.get(0));
            else {
                SparseArray<Messages> map = new SparseArray<>();
                for (int i = 0, j = 0, k = 0; k < 10; k++) {
                    String subName = subNameArray.get(i);
                    Log.i(TAG, "findRecallByContext: [" + i + " " + j + "] - " + " [" + (prevPos + i) + " " + (nextPos - j) + "]" + subName);
                    Messages msgPrev = findNext(prevPos + i, subName);
                    Messages msgNext = findNext(nextPos - j, subName);
                    if (msgPrev != null) {
                        if (map.get(i) == null)
                            map.put(i, msgPrev);
                        i++;
                        Log.i(TAG, "map: " + map);
                        if (i == unknownRecalls)
                            break;
                    }
                    if (msgNext != null) {
                        int index = unknownRecalls - j;
                        if (map.get(index) == null)
                            map.put(index, msgNext);
                        j++;
                        Log.i(TAG, "map: " + map);
                        if (j == unknownRecalls)
                            break;
                    }
                }
                Log.i(TAG, "final map: " + map);
                if (map.size() == 0)
                    notFound();
                else
                    for (int i = 0; i < unknownRecalls; i++)
                        addRecall(map.get(i));
            }
        }

        private void findRecallByPrev(int prevPos) {
            for (int i = 0; i < unknownRecalls; i++) {
                String subName = subNameArray.get(i);
                Log.i(TAG, "findRecallByPrev: " + prevPos + " - " + subName);
                Messages messages = findNext(prevPos + i, subName);
                addRecall(messages);
            }
        }

        private void findRecallByNext(int nextPos) {
            for (int i = unknownRecalls - 1; i >= 0; i--) {
                String subName = subNameArray.get(i);
                Log.i(TAG, "findRecallByNext: " + nextPos + " - " + subName);
                Messages messages = findPrev(nextPos - i, subName);
                addRecall(messages);
            }
        }

        private Messages findNext(int prevPos, String subName) {
            Log.i(TAG, "findNext: " + prevPos + " - " + subName);
            Messages messages = null;
            int maxID = dao.getMaxID(title, isWX);
            for (int i = 0; i < 20; i++) {
                prevPos++;
                if (prevPos > maxID) {
                    Log.i(TAG, "findNext: to the end: " + maxID);
                    return null;
                }
                if ((messages = dao.queryById(title, isWX, prevPos)) == null)
                    continue;
                if (!messages.getSubName().equals(subName))
                    continue;
                break;
            }
            return messages;
        }

        /**
         * 根据下文找到第一个名字一样的消息
         *
         * @param nextPos 下文的位置
         * @param subName 下文的名字
         * @return 找到的内容 如果为null则没找到
         */
        private Messages findPrev(int nextPos, String subName) {
            Log.i(TAG, "findPrev: " + nextPos + " - " + subName);
            Messages messages = null;
            // 和真正要找的消息之间可能会参杂着误加的消息 所以需要一个范围 但不会太多
            int i;
            for (i = 0; i < 10; i++) {
                nextPos--;
                // 到底了
                if (nextPos == 0) {
                    Log.i(TAG, "findPrev: to the end: " + 0);
                    return null;
                }
                // 中间可能删过消息
                if ((messages = dao.queryById(title, isWX, nextPos)) == null)
                    continue;
                // 判断撤回人名字是否一致
                if (!messages.getSubName().equals(subName))
                    continue;
                // 找到了
                break;
            }
            if (i == 10)
                messages = null;
            return messages;
        }

        private void notFound() {
            XToast.build(context, "没有找到撤回的消息呢").show();
        }

        private void addRecall(Messages messages) {
            if (messages == null) {
                notFound();
                return;
            }
            Log.e(TAG, "addRecall: " + messages.getMessage());
            if ("[图片]".equals(messages.getMessage())) {
                messages.setImages(searchImageFile(context, messages.getTime(), client));
                XToast.build(context, messages.getSubName() + ": [图片]" + messages.getImage()).show();
            } else {
                XToast.build(context, messages.getSubName() + ": " + messages.getMessage()).show();
            }
            if (dao.existRecall(messages))
                return;
            dao.addRecall(messages, prevSubName, prevMessage, nextSubName, nextMessage);
        }

        private void initContext(AccessibilityEvent event) {
            prevMessage = null;
            nextMessage = null;
            prevSubName = null;
            nextSubName = null;
            subNameArray.clear();
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
                // 获取上下文
                // pos未赋值
                if (pos == 0) {
                    if (message.contains(RECALL)) {
                        Log.i(TAG, "initContext: p-sub-name: " + subName);
                        subNameArray.add(subName);
                    } else {
                        // 保证撤回消息是连续的
                        subNameArray.clear();
                        prevMessage = message;
                        prevSubName = subName;
                        topPos = i;
                    }
                } else {
                    if (message.contains(RECALL)) {
                        Log.i(TAG, "initContext: n-sub-name: " + subName);
                        subNameArray.add(subName);
                    } else {
                        nextMessage = message;
                        nextSubName = subName;
                        botPos = i;
                        break;
                    }
                }
            }
            unknownRecalls = botPos - topPos - 1;
        }
    }

    public void onContentChanged(AccessibilityNodeInfo root) {
        if (!init(root))
            return;
        if (isOtherMsg) {
            onOtherMsg();
            return;
        }

        AccessibilityNodeInfo group;
        int index = chatGroupViewNode.getChildCount() - 2;
        // 如果屏幕内有大于1条消息的话 根据上下两条消息查重
        if (index > 0) {
            group = chatGroupViewNode.getChild(index);
            if (group == null)
                return;
            parser(group);
            pMessage = message;
            pSubName = subName;
        }

        group = chatGroupViewNode.getChild(chatGroupViewNode.getChildCount() - 1);
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
            String string = text + "";
            Log.w(TAG, "Notification text: " + string);
            if (string.equals("你的帐号在电脑登录"))
                return;

            StringBuilder builder = new StringBuilder(string);
            int i1 = string.indexOf("[特别关注]");
            int i2 = string.indexOf("[有新回复]");
            if (i1 != -1 && i1 + 6 < string.length())
                builder.delete(i1, i1 + 6);
            if (i2 != -1 && i2 + 6 < string.length())
                builder.delete(i2, i2 + 6);
            string = builder.toString();

            int i = string.indexOf(':');
            if (i < 1) {
                Log.d(TAG, "Notification does not contains ':'");
                return;
            }
            title = string.substring(0, i);
            message = string.substring(i + 2);
            subName = title;
            //是群消息
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
        if (otherMsgNode == null)
            return;
        String string = otherMsgNode.getText() + "";
        Log.i(TAG, "onOtherMsg: " + string);
        int i = string.indexOf(":");
        int k = string.lastIndexOf("-");
        //如果在联系人列表里出现过的,那么就是在其他人的聊天界面
        message = string.substring(i + 1);
        //包含"-" 可能是群
        for (int l = 0, j = string.indexOf("-", l); ; l++) {
            // "-"存在, l 不大于 lastIndex, - 在 : 前面
            if (j < 0 || l > k || k > i)
                break;
            title = string.substring(0, j);
            subName = string.substring(j + 1, i);
            if (!dao.existTable(title, isWX)) {
                Log.i(TAG, "onOtherMsg: " + title + " " + subName);
                continue;
            }
            addMsg(false);
            return;
        }
        title = string.substring(0, i);
        addMsg(false);
    }

    // TODO: 如果有 qq 表情的话(非常规 ascii) 把它转义成 斜杠+描述 的形式
    public void addMsg(boolean force) {
        String temp = title + "-" + subName + ": " + message;
        if (added.equals(temp))
            return;
        // 不添加"撤回了一条消息"
        if (RECALL.equals(message))
            return;
        if (!force) {
            Log.d(TAG, "Add message: message: " + message + "\t prevMessage: " + pMessage);
            if (dao.existMessage(title, isWX, message, pMessage, subName, pSubName)) {
                Log.d(TAG, "addMsg: already exits");
                return;
            }
        }
        added = temp;
        Log.e(TAG, "Add message: " + added);
        dao.addMessage(title, subName, isWX, message);
    }

}
