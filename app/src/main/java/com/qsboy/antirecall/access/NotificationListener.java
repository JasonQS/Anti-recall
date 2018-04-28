/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.access;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;


public class NotificationListener extends NotificationListenerService {

    String TAG = "NotificationListener";
    private String packageName;
    private String title;
    private String text;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, sbn.toString());
        Bundle extras = sbn.getNotification().extras;

        packageName = sbn.getPackageName();
        title = (String) extras.get(Notification.EXTRA_TITLE);
        text = (String) extras.get(Notification.EXTRA_TEXT);

        Log.v(TAG, "Notification - : " +
                " \npackageName: " + packageName +
                " \nTitle      : " + title +
                " \nText       : " + text);

        switch (packageName) {
            case "com.tencent.mm":
                new WXClient(getApplicationContext()).onNotification(title, text);
                break;

        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

}
