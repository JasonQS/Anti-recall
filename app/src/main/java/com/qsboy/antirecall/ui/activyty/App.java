/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui.activyty;

import android.app.Application;
import android.content.Context;


public class App extends Application {

    // launch页停留时间
    public static final int LaunchDelayTime = 500;

    // 会员手机号
    public static String phone = "";
    public static boolean isLoggedin = false;


    public static String addedMessage = "";

    // 用于配置列表颜色
    public static final int THEME_RED = 1;
    public static final int THEME_GREEN = 2;
    public static final int THEME_BLUE = 3;

    public static final String pkgTim = "com.tencent.tim";
    public static final String pkgQQ = "com.tencent.mobileqq";
    public static final String pkgWX = "com.tencent.mm";
    public static final String pkgThis = "com.qsboy.antirecall";

    // 有的机型可以针对性的过滤非CONTENT_CHANGE_TYPE_TEXT的事件
    public static boolean isTypeText = false;

    // 设置
    public static boolean isShowAllQQMessages = true;
    public static boolean isWeChatAutoLogin = true;
    public static boolean isSwipeRemoveOn = true;
    public static boolean isCheckUpdateOnlyOnWiFi = false;

    // 检查权限按钮的点击时间
    public static long timeCheckAccessibilityServiceIsWorking = 0;
    public static long timeCheckNotificationListenerServiceIsWorking = 0;

    // 用于调整两个RecyclerView高度
    public static int layoutHeight = -1;
    public static int deviceHeight;
    public static int recyclerViewAllHeight;
    public static int recyclerViewRecalledHeight;
    public static float adjusterY;
    public static float adjusterOriginalY;

    public static int activityPageIndex = 2;

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale);
    }

}
