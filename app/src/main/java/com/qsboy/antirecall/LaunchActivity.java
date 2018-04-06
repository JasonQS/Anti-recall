/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

public class LaunchActivity extends AppCompatActivity {

    String TAG = "Launch";
    BitmapFactory.Options opts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);


        opts = new BitmapFactory.Options();
        // 不读取像素数组到内存中，仅读取图片的信息如高宽
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.start, opts);
        // 从Options中获取图片的分辨率
        int imageHeight = opts.outHeight;
        int imageWidth = opts.outWidth;

        // 获取Android屏幕的服务
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Point size = new Point();
        if (wm != null) {
            wm.getDefaultDisplay().getSize(size);
        }

        // 计算采样率
        int scaleX = imageWidth / size.x;
        int scaleY = imageHeight / size.y;
        int scale = 1;
        // 采样率依照最大的方向为准
//        if (scaleX > scaleY && scaleY >= 1) {
//            scale = scaleX;
//        }
//        if (scaleX < scaleY && scaleX >= 1) {
//            scale = scaleY;
//        }

//        scale = scaleX < scaleY ? scaleX : scaleY;
        scale = scaleX;

        // false表示读取图片像素数组到内存中，依照设定的采样率
        // 采样率
        opts.inSampleSize = scale;
        opts.inJustDecodeBounds = false;

        Bitmap bitmap =
                BitmapFactory.decodeResource(getResources(), R.drawable.start, opts);
//                ImageHelper.decodeSampledBitmapFromResource(getResources(), R.drawable.start, size.x, size.y);

        ImageView img = findViewById(R.id.img_launch);
//        img.setImageBitmap(bitmap);

        Log.i(TAG, "image-height :\t" + imageHeight);
        Log.i(TAG, "image-width  :\t" + imageWidth);
        Log.i(TAG, "window-height:\t" + size.y);
        Log.i(TAG, "window-width :\t" + size.x);
        Log.i(TAG, "scaleX       :\t" + scaleX);
        Log.i(TAG, "scaleY       :\t" + scaleY);
        Log.i(TAG, "scale        :\t" + scale);
        Log.i(TAG, "final-height :\t" + bitmap.getHeight());
        Log.i(TAG, "final-width  :\t" + bitmap.getWidth());

        Handler handler = new Handler(); //当计时结束时，跳转至主界面
        handler.postDelayed(() -> {
//            Intent intent = new Intent(LaunchActivity.this, com.qsboy.antirecall.MainActivity.class);
            Intent i = new Intent();
            ComponentName name = new ComponentName("com.qsboy.antirecall", "com.qsboy.antirecall.MainActivity");
            i.setComponent(name);
            startActivity(i);
//            overridePendingTransition(R.anim.activity_enter, 0);
            LaunchActivity.this.finish();
        }, 2000);

    }
}
