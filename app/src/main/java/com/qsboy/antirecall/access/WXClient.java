/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.access;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.qsboy.antirecall.db.Dao;
import com.qsboy.antirecall.ui.activyty.App;

import static android.content.Context.KEYGUARD_SERVICE;


public class WXClient {

    String TAG = "Wx";

    private Context context;
    private Dao dao;
    private String title;
    private String name;
    private String message;

    public static int WeChatAutoLoginTimes;

    public static void autoLoginFlagEnable() {
        if (App.isWeChatAutoLogin)
            WeChatAutoLoginTimes = 10;
    }

    public WXClient(Context context) {
        this.context = context;
        dao = Dao.getInstance(this.context, Dao.DB_NAME_WE_CHAT);
    }

    public void onNotification(String title, String text) {
        if (title == null || text == null)
            return;
        this.title = title;
        int i = text.indexOf(':');
        if (i >= 1) {
            name = text.substring(0, i);
            message = text.substring(i + 2);
            //多条消息
            int j;
            if (name.startsWith("["))
                if ((j = name.indexOf("]")) > 0)
                    name = name.substring(j + 1);
        } else {
            name = title;
            message = text;
        }

        Log.w(TAG, "onNotification: " + title + " - " + name + " : " + message);
        dao.addMessage(title, name, message);

        if (isPCApplyLogin()) {
            wakeUpAndUnlock();
            autoLoginFlagEnable();
        }

    }

    public boolean isPCApplyLogin() {
        if ("微信".equals(title))
            if ("Mac 微信登录确认".equals(message) || "Windows 微信登录确认".equals(message) || "Windows WeChat登入確認".equals(message))
                return true;
        if ("WeChat".equals(title))
            if ("Confirm your login to Mac WeChat".equals(message) || "Confirm your login to Windows WeChat".equals(message) || "Mac WeChat登入確認".equals(message))
                return true;

        return false;
    }

    public void wakeUpAndUnlock() {
        // TODO: 24/05/2018 解锁密码
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm == null)
            return;
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire(1000); // 点亮屏幕
            wl.release(); // 释放
        }
        // 屏幕解锁
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
        // 屏幕锁定
        keyguardLock.reenableKeyguard();
        keyguardLock.disableKeyguard(); // 解锁
    }
}
