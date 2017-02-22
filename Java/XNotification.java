/*
 * Copyright (c) 2017.
 * qsboy.com 版权所有
 */

package com.qiansheng.messagecapture;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class XNotification extends AppCompatActivity {

    private Context mContext;
    private NotificationManager manager;
    private PendingIntent pendingIntent;
    private final String TAG = "X-Notification";

    public XNotification(Context context) {
        mContext = context;
        manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(mContext.getApplicationContext(), MainActivity.class);
        pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    public XNotification(Context context,Intent intent) {
        mContext = context;
        manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    public void show(String text) {

        manager.cancel(10);             //删除原先的通知

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle("Message Captor")
                .setContentText(text)
                .setAutoCancel(true);
        manager.notify(10, mBuilder.build());

        Log.w(TAG, "show " + text);

    }

    public void printSuccess() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle("Message Captor")
                .setContentText("软件已经可以使用了! excited !")
                .setAutoCancel(true);
        manager.notify(1, mBuilder.build());

        Log.w(TAG, "show Success Notification");

        new XFile(mContext).setNotShowCheckedNotice();

    }


    public void showInstall() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.icon);
        builder.setContentTitle("MessageCaptor");
        builder.setContentText("软件有更新,点击安装");
        builder.setContentIntent(pendingIntent);
        mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(2, builder.build());

        Log.w(TAG, "show Update Notification");

    }

}
