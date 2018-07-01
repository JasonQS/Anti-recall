/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qsboy.antirecall.R;
import com.qsboy.antirecall.ui.activyty.App;

public class XToastPro {
    private static String TAG = "X-Toast-Pro";
    private static LinearLayout ll;
    private WindowManager.LayoutParams params;
    private Context context;
    private WindowManager wm;
    private int duration = 2500;
    private int top;
    private int bottom;
    private FrameLayout item;

    private XToastPro(Context context) {
        this.context = context.getApplicationContext();
        top = App.deviceHeight / 2;
        bottom = App.deviceHeight / 2;
        wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);

        params = new WindowManager.LayoutParams();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = R.style.Animation_Toast;
        if (Build.VERSION.SDK_INT >= 26)
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        else params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

    }

    public static XToastPro build(Context context, String text) {
        XToastPro toast = new XToastPro(context);
        if (ll == null)
            ll = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.toast_pro, null);
        FrameLayout item = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.toast_pro_item, null);
        ImageView iv = item.findViewById(R.id.toast_iv);
        TextView tv = item.findViewById(R.id.toast_tv);
        int i = text.indexOf("[图片]");
        if (i >= 0 && text.length() > i + 4) {
            Log.w(TAG, "text : " + text);
            String imageName = text.substring(i + 4);
            Bitmap bitmap = ImageHelper.getBitmap(imageName);
            iv.setImageBitmap(bitmap);
            iv.setVisibility(View.VISIBLE);
            Log.w(TAG, "显示的是图片");
        } else {
            tv.setText(text);
            iv.setVisibility(View.GONE);
            Log.w(TAG, "show: " + text);
        }
        toast.item = item;
        return toast;
    }

    public XToastPro setPosition(int top, int bottom) {
        this.top = top;
        this.bottom = bottom;
        return this;
    }

    public void show() {
        if (item == null || ll == null)
            return;
        ll.addView(item);
        ll.measure(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        params.y = (top + bottom - ll.getMeasuredHeight()) / 2;
        params.y -= App.dip2px(context, 20);
        if (ll.isShown())
            wm.updateViewLayout(ll, params);
        else
            wm.addView(ll, params);
        new Handler().postDelayed(() -> {
            if (ll != null) {
                wm.removeView(ll);
                ll = null;
                item = null;
                wm = null;
            }
        }, duration);
    }
}
