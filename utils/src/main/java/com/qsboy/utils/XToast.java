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

import java.util.HashSet;
import java.util.Set;

import static com.qsboy.utils.ImageHelper.getBitmap;

public class XToast {

    private static String TAG = "X-Toast";
    private WindowManager.LayoutParams params;
    private Context context;
    private WindowManager wm;
    private View view;
    private static Set<View> viewList = new HashSet<>();
    // TODO: 持续时间设置
    private int duration = 2500;
    private int y = 100;
    private static int offsetY = 0;
    private Handler handler;


    private XToast(Context context) {
        this.context = context.getApplicationContext();
        wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        handler = new Handler();

        params = new WindowManager.LayoutParams();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
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
    public static XToast build(Context context, String text) {
        XToast toast = new XToast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.toast, null);
        ImageView iv = view.findViewById(R.id.toast_iv);
        TextView tv = view.findViewById(R.id.toast_tv);
        int i = text.indexOf("[图片]");
        if (i >= 0 && text.length() > i + 4) {
            Log.w(TAG, "text : " + text);
            String imageName = text.substring(i + 4);
            Bitmap bitmap = getBitmap(imageName);
            iv.setImageBitmap(bitmap);
            iv.setVisibility(View.VISIBLE);
            Log.w(TAG, "显示的是图片");
        } else {
            tv.setText(text);
            iv.setVisibility(View.GONE);
            Log.w(TAG, "show: " + text);
        }
        toast.view = view;
        return toast;
    }

    /**
     * 支持多条消息同时显示 中间间隔10dp
     */
    public void show() {
        if (view == null)
            return;
        Log.i(TAG, "show: view list: " + viewList);

        // TODO: 09/05/2018 做成一个layout 一个view 增加子view
        view.measure(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        offsetY = view.getMeasuredHeight() + dip2px(context, 10);
        for (View view : viewList) {
            view.animate().setDuration(0).translationYBy(-offsetY);
        }
        wm.addView(view, params);
        viewList.add(view);
        Log.i(TAG, "show: height: " + offsetY);
        handler.postDelayed(() -> {
            if (view != null) {
                viewList.remove(view);
                wm.removeView(view);
                view = null;
                wm = null;
            }
        }, 5000);
    }

    /**
     * dip到px单位转换
     */
    private static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale);
    }
}