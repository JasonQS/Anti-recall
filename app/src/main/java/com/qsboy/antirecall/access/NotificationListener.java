/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.access;

import android.app.KeyguardManager;
import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;


public class NotificationListener extends NotificationListenerService {

    final String pkgTim = "com.tencent.tim";
    final String pkgQQ = "com.tencent.mobileqq";
    final String pkgWX = "com.tencent.mm";
    String TAG = "NotificationListener";
    private String packageName;
    private String title;
    private String text;

    private PowerManager pm;
    private PowerManager.WakeLock wl = null;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Bundle extras = sbn.getNotification().extras;

        packageName = sbn.getPackageName();
        title = (String) extras.get(Notification.EXTRA_TITLE);
        text = (String) extras.get(Notification.EXTRA_TEXT);

        Log.d(TAG, "Notification - : " +
                " \npackageName: " + packageName +
                " \nTitle      : " + title +
                " \nText       : " + text);

        switch (packageName) {
            case pkgWX:
                new WXClient(getApplicationContext()).onNotification(title, text);
                break;
        }
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

}
