/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.access;

import android.app.Notification;
import android.os.Bundle;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.qsboy.antirecall.ui.activyty.App;

import java.util.Date;

import static com.qsboy.antirecall.ui.activyty.App.pkgThis;
import static com.qsboy.antirecall.ui.activyty.App.pkgWX;

public class NotificationListener extends NotificationListenerService {

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
        Object oTitle = extras.get(Notification.EXTRA_TITLE);
        Object oText = extras.get(Notification.EXTRA_TEXT);
        if (oTitle == null || oText == null)
            return;
        title = oTitle.toString();
        text = oText.toString();

        Log.d(TAG, "Notification - : " +
                " \npackageName: " + packageName +
                " \nTitle      : " + title +
                " \nText       : " + text);

        switch (packageName) {
            case pkgWX:
                new WXClient(getApplicationContext()).onNotification(title, text);
                break;
            case pkgThis:
                App.timeCheckNotificationListenerServiceIsWorking = new Date().getTime();
                Log.i(TAG, "onNotificationPosted: time: " + App.timeCheckNotificationListenerServiceIsWorking);
                break;
        }
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

}
