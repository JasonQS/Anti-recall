/*
 * Copyright (c) 2017.
 * qsboy.com 版权所有
 */

package com.qiansheng.messagecapture;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import static com.qiansheng.messagecapture.XBitmap.getLocalBitmap;


class XToast {

    private Context mContext;
    private WindowManager wm;
    private View mView;
    private static final String TAG = "X-Toast";

    private XToast(Context context) {
        mContext = context.getApplicationContext();
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 显示文字,如果传入的是图片,则去本地找到相应的图片显示
     */
    static XToast makeText(Context context, CharSequence text) {
        XToast xtoast = new XToast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.toast, null);
        ImageView iv = (ImageView) view.findViewById(R.id.toast_iv);
        TextView tv = (TextView) view.findViewById(R.id.toast_tv);
        String s = text.toString();
        if (s.startsWith("#image")) {
            Log.w(TAG, "text : " + s);
            String imageTime = s.substring(6,19);
            Bitmap bitmap = getLocalBitmap(imageTime);
            iv.setImageBitmap(bitmap);
            iv.setVisibility(View.VISIBLE);
            Log.w(TAG, "显示的是图片");
        } else {
            tv.setText(text);
            iv.setVisibility(View.GONE);
            Log.w(TAG, "显示的是: " + text);
        }
        xtoast.mView = view;
        return xtoast;
    }

    void show() {
        if (mView == null)
            return;
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = R.style.Animation_Toast;
        params.y = dip2px(mContext, 120);
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        wm.addView(mView, params);
        int mDuration = 2500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mView != null) {
                    wm.removeView(mView);
                    mView = null;
                    wm = null;
                }
            }
        }, mDuration);
    }

    /**
     * dip到px单位转换
     * @param dipValue dip 的值
     * @return px值
     */
    private static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}