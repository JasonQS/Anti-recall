/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class XNotification extends AppCompatActivity {

    private Context context;
    private NotificationManager manager;
    private PendingIntent pendingIntent;
    private final String TAG = "X-Notification";
    private final String ChannelID = "qsboy";
    private final String title = "Anti-recall";

    public XNotification(Context context, Class<Activity> activity) {
        this.context = context;
        manager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this.context.getApplicationContext(), activity);
        pendingIntent = PendingIntent.getActivity(this.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    public XNotification(Context context, Intent intent) {
        this.context = context;
        manager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        pendingIntent = PendingIntent.getActivity(this.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    public void show(String text) {

        manager.cancel(10);             //删除原先的通知

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, ChannelID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true);
        manager.notify(10, mBuilder.build());

        Log.w(TAG, "show " + text);

    }

    public void printSuccess() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, ChannelID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText("软件已经可以使用了! excited !")
                .setAutoCancel(true);
        manager.notify(1, mBuilder.build());

        Log.w(TAG, "show Success Notification");

    }


    public void showInstall() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ChannelID);
        builder.setAutoCancel(true);
//        builder.setSmallIcon(R.mipmap.icon);
        builder.setContentTitle(title);
        builder.setContentText("软件有更新,点击安装");
        builder.setContentIntent(pendingIntent);
        context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(2, builder.build());

        Log.w(TAG, "show Update Notification");

    }

}
