/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.utils;

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
import android.widget.ImageView;
import android.widget.TextView;

import static com.qsboy.utils.XBitmap.getLocalBitmap;

public class XToast {

    private static String TAG = "X-Toast";
    WindowManager.LayoutParams params;
    private Context context;
    private WindowManager wm;
    private View view;
    private ImageView iv;
    private TextView tv;
    private int duration = 2500;
    private int y = 100;
    private int offsetY = y;
    private int pos;
    Handler handler;


    public XToast(Context context) {
        this.context = context.getApplicationContext();
        wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        view = LayoutInflater.from(context).inflate(R.layout.toast, null);
        iv = view.findViewById(R.id.toast_iv);
        tv = view.findViewById(R.id.toast_tv);
        handler = new Handler();

        params = new WindowManager.LayoutParams();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = R.style.Animation_Toast;
        params.y = dip2px(context, y);
        if (Build.VERSION.SDK_INT >= 26)
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        else params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    }

    /**
     * 显示文字,如果传入的是图片,则去本地找到相应的图片显示
     */
    public XToast build(Context context, String text) {
        if (text.startsWith("#image")) {
            Log.w(TAG, "text : " + text);
            String imageTime = text.substring(6, 19);
            Bitmap bitmap = getLocalBitmap(imageTime, context);
            iv.setImageBitmap(bitmap);
            iv.setVisibility(View.VISIBLE);
            Log.w(TAG, "显示的是图片");
        } else {
            tv.setText(text);
            iv.setVisibility(View.GONE);
            Log.w(TAG, "显示的是: " + text);
        }
        pos = 0;

        return this;
    }

    public XToast setPos(int pos) {
        this.pos = pos;
        return this;
    }

    public void show() {
        if (view == null)
            return;
        if (pos == 0)
            offsetY = 0;
        params.y += offsetY;
        offsetY += params.height + dip2px(context, 20);
        wm.addView(view, params);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (view != null) {
                    wm.removeView(view);
                    view = null;
                    wm = null;
                }
            }
        }, duration);
    }

    /**
     * dip到px单位转换
     *
     * @param dipValue dip 的值
     * @return px值
     */
    private static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}