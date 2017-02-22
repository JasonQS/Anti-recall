/*
 * Copyright (c) 2017.
 * qsboy.com 版权所有
 */

package com.qiansheng.messagecapture;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.qiansheng.messagecapture.Debug.ServerOnConnected;
import static com.qiansheng.messagecapture.MainActivity.File_Withdraw;
import static com.qiansheng.messagecapture.XBitmap.getImageFileInQQ;

/**
 * 防撤回神器 主要代码
 * 使用了安卓的 辅助功能类 AccessibilityService
 * 所有的高权限的处理都在这里完成
 * 这个类本是Google设计为盲人或者视觉障碍服务的,使他们也能用手机
 * (国外对残疾人的关爱真是很到位)
 * 在经过一系列配置之后,我就能通过这个类来获取屏幕,以及通知栏信息了
 * 我把截获的信息按名称保存到文件中,再在有撤回的时候回去查找
 * 主要技术点:
 * Search类里的系列方法, 我底下的注释已经很详细了
 *
 */
public class MessageCaptor extends AccessibilityService {

    final String TAG = "MessageCaptor";
    final String NameID_qq = "com.tencent.mobileqq:id/title";
    final String TEXT_WITHDRAW = "撤回了一条消息";
    static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm MM/dd", Locale.CHINA);
    List<String> WD_MsgList = XListAdapter.MsgList;
    List<String> WD_NameList = XListAdapter.NameList;
    Set<String> QQ_NameList;
    boolean is_wx;
    String tempMessage;
    long ClickTime = 0;
    long ClickTime2 = 0;
    long ClickTime3 = 0;

    Handler mHandler;
    SingleClick singleClick;
    DoubleClick doubleClick;
    TrebleClick trebleClick;
    AddNewMessage addNewMessage;
    XFile xFile;
    GetNodes getNodes;

    @Override
    protected void onServiceConnected() {
        ServerOnConnected = true;
        xFile = new XFile(this);
        QQ_NameList = getNameList();
        mHandler = new Handler();
        getNodes = new GetNodes();
    }

    @Override
    public void onInterrupt() {
        ServerOnConnected = false;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        int eventType = event.getEventType();
        AccessibilityNodeInfo nodeInfo = event.getSource();
        is_wx = event.getPackageName().equals("com.tencent.mm");

        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                //在屏幕切换时,如果用户是第一次使用app,则推送一条表示成功的通知
                if (xFile.isShowCheckedNotice())
                    new XNotification(this).printSuccess();
                break;

            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                //顶部通知栏状态改变
                getNotification(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if (nodeInfo == null)
                    return;

                //只需在改变类型为文字时执行添加操作
                //大部分change type为 CONTENT_CHANGE_TYPE_SUBTREE
                int types = event.getContentChangeTypes();
                if (types != AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT)
                    break;
                CharSequence cs = nodeInfo.getText();
                if (cs == null)
                    break;

                Log.w(TAG, "Text Changed : " + cs);

                //判断是不是QQ聊天时其他人发的消息
                if (isOtherConversation(cs))
                    break;

                //添加新消息至本地文件
                addNewMessage = new AddNewMessage();
                mHandler.post(addNewMessage);

                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                //点击事件
                if (nodeInfo == null)
                    break;
                if (nodeInfo.getText() == null)
                    break;
                //只有点击了"撤回一条消息"才会继续执行
                if (!nodeInfo.getText().toString().contains(TEXT_WITHDRAW))
                    break;

                String name = getName();

                //处理点击事件,单击双击等
                onClick(event, name);

                break;

        }
    }

    /**
     * 查找的主函数
     */
    class Search {

        Date start = new Date();

        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();

        //读屏幕的变量
        AccessibilityNodeInfo n1;
        AccessibilityNodeInfo n2;
        AccessibilityNodeInfo n3;
        int childCount;
        String contentScreen;

        /**
         * 自己的文件类
         * 有输出前后一行等方法
         */
        XFile.Search search;

        //search的变量
        List<String> aroundSting;
        String line;
        String content;
        String target;                  //撤回前后的内容
        int size;                       //要找的数量
        int num = 0;                    //连续撤回的数量
        boolean flag = false;           //是正着扫还是反着扫

        List<String> screenList = new ArrayList<>();
        List<String> listMsg = new ArrayList<>();

        //给getScreen调用的无参构造方法
        Search() {
        }

        Search(String name) {
            Log.i(TAG, "Searching...");

            if (name == null) {
                Log.e(TAG, "name is null !");
                return;
            }

            search = new XFile.Search(MainActivity.File_Dir + name);

            aroundSting = getPreString();
            size = aroundSting.size();
            Log.d(TAG, "size: " + size);

            /**
             * 从列表右边开始扫 就是先找撤回消息的前一句再根据这个找下一句
             */
            for (int i = size - 1; i >= 0; i--)
                scan(i);

            /**
             * 如果有没找到就换个方向扫 就是先找撤回消息的后一句再根据这个找前一句
             */
            if (listMsg.size() == 0 || num > 0) {
                search.seekEnd();
                num = 0;
                flag = true;
                Log.e(TAG, "scan from bottom");
                aroundSting = getAftString();
                size = aroundSting.size();
                for (int i = 0; i < size; i++)
                    scan(i);
            }

            /**
             * 如果还没找到,就从最近写入的地方读一条出来
             * 最多从最近添加的两条里面找
             */
            if (listMsg.size() == 0 || num > 0) {
                Log.w(TAG, "still not found");
                search.seekEnd();
                for (int i = 0; i < 2; ) {
                    line = search.nextLine();
                    content = getContent(line);
                    Log.i(TAG, "content: " + content);
                    if (content.contains(TEXT_WITHDRAW))
                        continue;
                    i++;
                    if (!screenList.contains(content)) {
                        addToListMsg();
                        break;
                    }
                }
            }

            /**
             * 最后集中写入文件
             */
            if (listMsg.size() > 0) {
                List<String> addedList = new ArrayList<>();
                WD_MsgList = XListAdapter.MsgList;
                int size = 0;
                String addedString = null;

                for (String msg : listMsg)
                    if (!WD_MsgList.contains(getContent(msg))) {
                        Log.i(TAG, msg + " " + getContent(msg));
                        xFile.writeFile(msg + '#' + name, File_Withdraw);
                        addedList.add(msg);
                        size++;
                    } else addedString = getContent(msg);

                if (size == 1)
                    XToast.makeText(getApplicationContext(), getContent(addedList.get(0))).show();
                else if (size > 1)
                    XToast.makeText(getApplicationContext(), "撤回了多条消息\n请在软件里查看").show();
                else XToast.makeText(getApplicationContext(), addedString).show();

            } else
                XToast.makeText(getApplicationContext(), "sorry 并没有截到消息\n可在帮助中查看原因").show();

            xFile.refresh();   //刷新撤回消息列表

            search.closeFile();

            Date end = new Date();
            Log.w(TAG, "searching cost " + (end.getTime() - start.getTime()) + " mm");

        }

        void scan(int i) {

            target = aroundSting.get(i);
            Log.i(TAG, "i: " + i);
            Log.w(TAG, "target : " + target);
            //连续撤回的次数 QQ是null 微信是文字_某某撤回了一条消息
            if (target == null || (target.contains(TEXT_WITHDRAW))) {
                num++;
            } else {
                Log.i(TAG, "num:" + num);
                while (true) {
                    line = search.nextLine();                       //往下找
                    if (line == null)                               //找完了 没找到
                        return;
                    content = getContent(line);                     //提取一行中的内容

                    if (content == null)
                        continue;

                    if (target.equals(content)) {                   //匹配到了list里的内容
                        Log.w(TAG, "search: FOUND " + target);
                        if (flag)                                   //如果找的是后一句
                            line = search.nextLine();               //就找前一句
                        else                                        //如果找的是前一句
                            line = search.preLine();                //就找下一句

                        Log.i(TAG, "read : " + line);
                        if (line == null)
                            continue;                               //这一行可能是之后滚屏加进来的
                        content = getContent(line);
                        Log.e(TAG, "撤回的消息是: " + content);

                        addToListMsg();

                        //连续撤回
                        if (num > 0) {
                            Log.i(TAG, "number > 0");
                            screenList = getScreen();

                            //加个偏置
                            if (flag)
                                line = search.preLine();
                            else
                                search.nextLine();
                            while (true) {
                                if (flag)
                                    line = search.nextLine();
                                else
                                    line = search.preLine();

                                if (line == null) {
                                    search.nextLine();
                                    break;
                                }

                                content = getContent(line);
                                if (screenList.contains(content))
                                    continue;

                                addToListMsg();

                                if (num == 0)
                                    return;
                                num--;
                            }
                        } else break;
                    }
                }
            }
        }

        void addToListMsg() {

            if (listMsg.contains(line))
                return;

            //如果是图片的话把从QQ缓存里找来的图片保存到自己的文件夹下
            if (content.equals("[图片]")) {
                long time = getTime_Long(line);
                boolean b = getImageFileInQQ(time);
                if (b)
                    line = "#image" + time + getTime_String(line);
                else line = "由于该图片曾经发过 所以无法找到...哭" + getTime_String(line);
            }

            listMsg.add(line);

            Log.w(TAG, "add: " + line);

        }

        /**
         * 由于微信控件的ID会经常变
         * 所以不能直接用nodeInfo.findAccessibilityNodeInfosByViewId(resourceID);
         * 所以我的解决方法是通过解析布局,通过根布局慢慢getChild
         * id好改,布局就不好改了
         * 找出来的内容只有对方发送的(可以加上自己发送的但没必要)
         *
         * @return ScreenList
         */
        List<String> getScreen() {

            Date start = new Date();

            List<String> screenList = new ArrayList<>();

            try {
                if (is_wx) {
                    try {
                        n1 = nodeInfo.getChild(0).getChild(0).getChild(4);
                        getScreenList_wx(screenList, n1);
                    } catch (Exception ignored) {
                    }
                    if (screenList.size() == 0) {
                        n1 = nodeInfo.getChild(8).getChild(0).getChild(4);
                        getScreenList_wx(screenList, n1);
                    }
                } else {
                    try {
                        n1 = nodeInfo.getChild(5);
                        getScreenList_qq(screenList, n1);
                    } catch (Exception ignored) {
                    }
                    if (screenList.size() == 0) {
                        n1 = nodeInfo.getChild(4);
                        getScreenList_qq(screenList, n1);
                    }
//                //通过ID查找消息,但测试出来有时候会找不全
//                String resourceID = "com.tencent.mobileqq:id/chat_item_content_layout";
//                List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(resourceID);
//                for (AccessibilityNodeInfo text : list) {
//                    content = text.getText().toString();
//                    screenList.add(content);
//                }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (screenList.size() == 0)
                getNodes.get();

            Date end = new Date();
            Log.i(TAG, "get screen cost " + (end.getTime() - start.getTime()) + " mm");
            Log.w(TAG, "Screen List is: " + screenList);

            return screenList;

        }

        /**
         * 先找到"某某撤回一条消息"然后把这之 前 的内容抓下来存入List
         *
         * @return Item before withdraw
         */
        List<String> getPreString() {

            Date start = new Date();

            List<String> preSting = new ArrayList<>();

            try {
                if (is_wx) {
                    try {
                        n1 = nodeInfo.getChild(0).getChild(0).getChild(4);
                        getPreString_wx(preSting, n1);
                    } catch (Exception ignored) {
                    }
                    if (preSting.size() == 0) {
                        n1 = nodeInfo.getChild(8).getChild(0).getChild(4);
                        getPreString_wx(preSting, n1);
                    }

                } else {
                    // QQ
                    try {
                        n1 = nodeInfo.getChild(5);
                        getPreString_qq(preSting, n1);
                    } catch (Exception ignored) {
                    }

                    if (preSting.size() == 0) {
                        n1 = nodeInfo.getChild(4);
                        getPreString_qq(preSting, n1);
                    }
                }
            } catch (Exception ignored) {
            }

            if (preSting.size() == 0)
                getNodes.get();

            Date end = new Date();
            Log.i(TAG, "get preString cost " + (end.getTime() - start.getTime()) + " mm");

            Log.w(TAG, "pre String List is : " + preSting);

            return preSting;

        }

        /**
         * 先找到"某某撤回一条消息"然后把这之 后 的内容抓下来存入List
         * 用于 撤回的前一句找不到的情况
         *
         * @return Item after withdraw
         */
        List<String> getAftString() {

            Date start = new Date();

            List<String> aftSting = new ArrayList<>();

            try {
                if (is_wx) {
                    try {
                        n1 = nodeInfo.getChild(0).getChild(0).getChild(4);
                        getAftString_wx(aftSting, n1);
                    } catch (Exception ignored) {
                    }
                    if (aftSting.size() == 0) {
                        n1 = nodeInfo.getChild(8).getChild(0).getChild(4);
                        getAftString_wx(aftSting, n1);
                    }

                } else {
                    // QQ
                    try {
                        n1 = nodeInfo.getChild(5);
                        getAftString_qq(aftSting, n1);
                    } catch (Exception ignored) {
                    }

                    if (aftSting.size() == 0) {
                        n1 = nodeInfo.getChild(4);
                        getAftString_qq(aftSting, n1);
                    }
                }
            } catch (Exception ignored) {
            }

            if (aftSting.size() == 0)
                getNodes.get();

            Date end = new Date();
            Log.i(TAG, "get after String cost " + (end.getTime() - start.getTime()) + " mm");

            Log.w(TAG, "after String List is : " + aftSting);

            return aftSting;

        }

        void getScreenList_wx(List<String> screenList, AccessibilityNodeInfo n1) {

            for (int i = 0; i < n1.getChildCount(); i++) {
                n2 = n1.getChild(i);
                childCount = n2.getChildCount();
                if (childCount != 0) {
                    n3 = n2.getChild(childCount - 1);
                    if (n3.getText() != null) {
                        content = n3.getText().toString();
                        screenList.add(content);
                    }
                }
            }
        }

        void getScreenList_qq(List<String> screenList, AccessibilityNodeInfo n1) {

            for (int i = 0; i < n1.getChildCount(); i++) {
                n2 = n1.getChild(i);
                childCount = n2.getChildCount();
                if (childCount != 0) {
                    n3 = n2.getChild(childCount - 1);
                    if (n3.getText() != null) {
                        content = n3.getText().toString();
                        screenList.add(content);
                    } else if (n3.getClassName().equals("android.widget.RelativeLayout")) {
                        content = "[图片]";
                        screenList.add(content);
                    }
                }
            }
        }

        void getPreString_wx(List<String> preSting, AccessibilityNodeInfo n1) {

            String tempSting = null;

            for (int i = 0; i < n1.getChildCount(); i++) {
                n2 = n1.getChild(i);
                childCount = n2.getChildCount();
                if (childCount != 0) {
                    n3 = n2.getChild(childCount - 1);
                    if (n3.getText() != null) {
                        contentScreen = n3.getText().toString();
                        if (contentScreen.contains(TEXT_WITHDRAW))
                            preSting.add(tempSting);
                        tempSting = contentScreen;
                    }
                }
            }
        }

        void getPreString_qq(List<String> preSting, AccessibilityNodeInfo n1) {

            String tempSting = null;

            for (int i = 0; i < n1.getChildCount(); i++) {
                n2 = n1.getChild(i);
                childCount = n2.getChildCount();
                if (childCount != 0) {
                    n3 = n2.getChild(childCount - 1);
                    if (n3.getText() != null) {
                        content = n3.getText().toString();
                        if (content.contains(TEXT_WITHDRAW)) {
                            preSting.add(tempSting);
                            tempSting = null;
                        } else
                            tempSting = content;
                    } else if (n3.getClassName().equals("android.widget.RelativeLayout"))
                        tempSting = "[图片]";
                }
            }
        }

        void getAftString_wx(List<String> aftSting, AccessibilityNodeInfo n1) {

            boolean flag = false;

            for (int i = 0; i < n1.getChildCount(); i++) {
                n2 = n1.getChild(i);
                childCount = n2.getChildCount();
                if (childCount != 0) {
                    n3 = n2.getChild(childCount - 1);
                    if (n3.getText() != null) {
                        content = n3.getText().toString();
                        if (flag)
                            aftSting.add(content);
                        if (content.contains(TEXT_WITHDRAW))
                            flag = true;
                    }
                }
            }
        }

        void getAftString_qq(List<String> aftString, AccessibilityNodeInfo n1) {

            boolean flag = false;

            for (int i = 0; i < n1.getChildCount(); i++) {
                n2 = n1.getChild(i);
                childCount = n2.getChildCount();
                if (childCount != 0) {
                    n3 = n2.getChild(childCount - 1);
                    if (flag) {
                        if (n3.getText() != null) {
                            content = n3.getText().toString();
                            aftString.add(content);
                            flag = false;
                        } else if (n3.getClassName().equals("android.widget.RelativeLayout")) {
                            aftString.add("[图片]");
                            flag = false;
                        }
                    }
                    if (n3.getText() != null) {
                        content = n3.getText().toString();
                        if (content.contains(TEXT_WITHDRAW))
                            flag = true;
                    }
                }
            }
        }

    }

    /**
     * 处理点击事件 单击多击
     * 我这边两次点击时间差为300毫秒
     */
    private void onClick(AccessibilityEvent event, String name) {
        ClickTime3 = ClickTime2;
        ClickTime2 = ClickTime;
        ClickTime = event.getEventTime();
        if ((ClickTime - ClickTime3) < 600) {
            //三击 先取消双击单击的post
            if (doubleClick != null)
                mHandler.removeCallbacks(doubleClick);
            if (singleClick != null)
                mHandler.removeCallbacks(singleClick);
            trebleClick = new TrebleClick(name);
            mHandler.post(trebleClick);
            //防止连按四下多次执行三击操作
            ClickTime3 = 0;
        } else if ((ClickTime - ClickTime2) < 300) {
            //双击 先取消单击的post
            if (singleClick != null)
                mHandler.removeCallbacks(singleClick);
            doubleClick = new DoubleClick(name);
            mHandler.postDelayed(doubleClick, 300);
        } else {
            //单击
            singleClick = new SingleClick(name);
            mHandler.postDelayed(singleClick, 300);
        }
    }
    /**
     * 单击
     * 判断撤回消息列表里是否存在当前的聊天对象 如果有,就直接输出
     * 如果没有,就查找
     */

    class SingleClick implements Runnable {

        String name;

        SingleClick(String name) {
            this.name = name;
        }
        @Override
        public void run() {
            Log.w(TAG, "Single Click");
            if (WD_NameList.contains(name)) {
                String text = WD_MsgList.get(WD_NameList.indexOf(name));
                Log.w(TAG, "text : " + text);
                XToast.makeText(getApplicationContext(), text).show();
            } else {
                new Search(name);
            }
        }

    }
    /**
     * 双击
     * 直接查找
     */

    class DoubleClick implements Runnable {

        String name;

        DoubleClick(String name) {
            this.name = name;
        }
        @Override
        public void run() {
            Log.e(TAG, "Double Click");
            new Search(name);

        }

    }
    /**
     * 三击
     * 删除当前联系人加入的最后一行消息
     * 在滚屏和切换窗口时会多加消息
     * 主要是调试用
     */

    class TrebleClick implements Runnable {

        String name;

        TrebleClick(String name) {
            this.name = name;
        }
        @Override
        public void run() {

            Log.e(TAG, "TREBLE CLICKED");

            new XFile.RemoveLine(name, getApplicationContext()).remove();

        }

    }
    /**
     * 往本地写内容
     */

    class AddNewMessage implements Runnable {
        @Override
        public void run() {
            try {

                List<String> list = new Search().getScreen();
                if (list.size() < 1) {
                    Log.d(TAG, "Screen List is Empty, return");
                    return;
                }
                String item = list.get(list.size() - 1);

                Log.w(TAG, "MESSAGE IS " + item);
                //判断是不是刚刚加过的 这边偷懒了没有去文件里查找确认
                //微信会在滚屏时加入大量历史消息
                if (item.equals(tempMessage)) {
                    Log.d(TAG, "Equal to Last Msg, return");
                    return;
                }
                //给消息加上时间戳
                Date date = new Date();
                String line = item + sdf.format(date);
                tempMessage = item;
                String name = getName();
                if (!QQ_NameList.contains(name)) {
                    QQ_NameList.add(name);
                    Log.w(TAG, "add new name");
                }
                xFile.writeFile(line, name);

            } catch (Exception e) {
                e.printStackTrace();
            }
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
    private boolean isOtherConversation(CharSequence cs) {
        String string = cs.toString();
        int len = cs.length();
        int index1 = string.indexOf(":");
        if (index1 > 0) {
            if (len - index1 == 3)          //是时间
                return true;
            String name = string.substring(0, index1);
            Log.i(TAG, "name: " + name);
            //如果在联系人列表里出现过的,那么就是在其他人的聊天界面
            if (QQ_NameList.contains(name)) {
                String content = string.substring(index1 + 1);
                Date date = new Date();
                String line = content + sdf.format(date);
                xFile.writeFile(line, name);
                return true;
            } else {
                //判断是不是群消息
                int index2 = string.indexOf("-");
                if (index2 > 0) {
                    name = string.substring(0, index2);
                    Log.i(TAG, "name: " + name);
                    if (QQ_NameList.contains(name)) {
                        String content = string.substring(index1 + 1);
                        Date date = new Date();
                        String line = content + sdf.format(date);
                        xFile.writeFile(line, name);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 把已经存下来的名字拉到一个Set里
     *
     * @return Known Name List
     */
    Set<String> getNameList() {

        Set<String> nameList = new HashSet<>();

        File fileDir = getFilesDir();
        for (File file : fileDir.listFiles()) {
            if (file.isFile())
                nameList.add(file.getName());
        }

        return nameList;

    }

    /**
     * 把通知栏里截获的消息处理并写入本地
     */
    void getNotification(AccessibilityEvent event) {
        Log.i(TAG, "Notification Changed");
        List<CharSequence> texts = event.getText();
        if (texts.isEmpty() || texts.size() == 0)
            return;
        for (CharSequence text : texts) {
            if (text == null)
                return;
            String string = text.toString();
            Log.w(TAG, "Notification Text:" + string);
            if (string.equals("你的帐号在电脑登录"))
                return;

            String content;
            String name;
            String time;

            int i = string.indexOf(':');
            if (i < 1) {
                Log.d(TAG, "Notification does not contains ':'");
                return;
            }
            name = string.substring(0, i);
            content = string.substring(i + 2);
            //是QQ群消息
            if (name.charAt(i - 1) == ')' && name.contains("(")) {
                content = string.substring(i + 1);
                name = name.substring(name.indexOf('(') + 1, name.indexOf(')'));
            }
            time = sdf.format(new Date());
            Log.w(TAG, "name : " + name + "    content : " + content + "    time : " + time);
            String line = content + time;
            tempMessage = content;
            xFile.writeFile(line, name);
        }

    }

    /**
     * 根据UI解析出屏幕中Name
     *
     * @return Name
     */
    String getName() {

        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        String s = "";
        if (is_wx) {
            try {
                s = nodeInfo.getChild(0).getChild(0).getChild(1).getText().toString();
            } catch (Exception e) {
                try {
                    s = nodeInfo.getChild(8).getChild(0).getChild(1).getText().toString();
                } catch (Exception ignored) {
                }
            }
        } else {
            try {
                List<AccessibilityNodeInfo> qq = nodeInfo.findAccessibilityNodeInfosByViewId(NameID_qq);
                s = qq.get(0).getText().toString();
            } catch (Exception ignored) {
            }
        }
        if (s.length() != 0) {
            Log.w(TAG, "name : " + s);
            return s;
        } else {
            getNodes.get();
            Log.e(TAG, "Get Name ERROR !");
            return null;
        }
    }

    /**
     * 把自己存入本地的"line"
     * 格式为 Content + Time
     * 中的 Content分离出来
     *
     * @param line line in file
     * @return content
     */
    String getContent(String line) {

        try {
            return line.substring(0, line.length() - 11);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 把Time分离出来
     * 得到的是字符串 12:34 02/18
     *
     * @param line line in file
     * @return time
     */
    String getTime_String(String line) {
        return line.substring(line.length() - 11);
    }

    /**
     * 把Time分出来
     * 并sdf.parse
     * 把String类型的time转换成Long的time
     * 为了能够查找QQ撤回的图片
     * 因为QQ图片文件名是根据第一次收到的时间命名的
     * 之后的图片只会生成一个链
     * 所以QQ只能查看第一次发的图片
     *
     * @param line line in file
     * @return time
     */
    long getTime_Long(String line) {

        try {
            Date date = sdf.parse(line.substring(line.length() - 11));
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 调试工具
     * 用于输出屏幕的node信息
     */
    class GetNodes {

        String print(AccessibilityNodeInfo nodeInfo) {

            CharSequence text = nodeInfo.getText();
            CharSequence description = nodeInfo.getContentDescription();
            CharSequence packageName = nodeInfo.getPackageName();
            CharSequence className = nodeInfo.getClassName();
            boolean focusable = nodeInfo.isFocusable();
            Rect rect = new Rect();
            nodeInfo.getBoundsInScreen(rect);

            StringBuilder builder = new StringBuilder()
                    .append("package name: ").append(packageName).append(" \t")
                    .append("location: ").append(rect).append(" \t")
                    .append("text: ").append(text).append(" \t")
                    .append("description: ").append(description).append(" \t")
                    .append("class name: ").append(className).append(" \t")
                    .append("focusable: ").append(focusable).append(" \t")
                    .append('\n');

            return builder.toString();

        }

        void get() {
            AccessibilityNodeInfo n0 = getRootInActiveWindow();

            try {
                Log.v(TAG, "\nv0                            " + print(n0));
                int v1 = n0.getChildCount();
                for (int i1 = 0; i1 < v1; i1++) {
                    AccessibilityNodeInfo n1 = n0.getChild(i1);
                    Log.v(TAG, "\n    v1: " + i1 + "                     " + print(n1));
                    int v2 = n1.getChildCount();
                    for (int i2 = 0; i2 < v2; i2++) {
                        AccessibilityNodeInfo n2 = n1.getChild(i2);
                        Log.v(TAG, "\n        v2: " + i2 + "                 " + print(n2));
                        int v3 = n2.getChildCount();
                        for (int i3 = 0; i3 < v3; i3++) {
                            AccessibilityNodeInfo n3 = n2.getChild(i3);
                            Log.v(TAG, "\n            v3: " + i3 + "             " + print(n3));
                            int v4 = n3.getChildCount();
                            for (int i4 = 0; i4 < v4; i4++) {
                                AccessibilityNodeInfo n4 = n3.getChild(i4);
                                Log.v(TAG, "\n                v4: " + i4 + "         " + print(n4));
                                int v5 = n4.getChildCount();
                                for (int i5 = 0; i5 < v5; i5++) {
                                    AccessibilityNodeInfo n5 = n4.getChild(i5);
                                    Log.v(TAG, "\n                    v5: " + i5 + "     " + print(n5));
                                    int v6 = n5.getChildCount();
                                    for (int i6 = 0; i6 < v6; i6++) {
                                        AccessibilityNodeInfo n6 = n5.getChild(i6);
                                        Log.v(TAG, "\n                        v6: " + i6 + " " + print(n6));
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
