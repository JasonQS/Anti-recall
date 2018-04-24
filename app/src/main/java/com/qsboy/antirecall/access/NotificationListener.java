/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.access;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;


@SuppressLint("OverrideAbstract")
public class NotificationListener extends NotificationListenerService {

    private String packageName;
    private String title;
    private String text;

    String TAG = "NotificationListener";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, sbn.toString());
        Bundle extras = sbn.getNotification().extras;

        packageName = sbn.getPackageName();
        title = (String) extras.get(Notification.EXTRA_TITLE);
        text = (String) extras.get(Notification.EXTRA_TEXT);

        Log.i(TAG, "Notification - : " +
                " \npackageName: " + packageName +
                " \nTitle: " + title +
                " \nText : " + text);

        super.onNotificationPosted(sbn);
    }

    public boolean isPCApplyLogin() {

        if (!"com.tencent.mm".equals(packageName))
            return false;
        if ("微信".equals(title))
            if ("Mac 微信登录确认".equals(text) || "Windows 微信登录确认".equals(text))
                return true;
        if ("WeChat".equals(title))
            if ("Confirm your login to Mac WeChat".equals(text) || "Mac WeChat登入確認".equals(text))
                return true;

        return false;
    }

}
