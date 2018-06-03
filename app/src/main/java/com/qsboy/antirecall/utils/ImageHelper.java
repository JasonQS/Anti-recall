/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;


public class ImageHelper {

    private static final String TAG = "X-Bitmap";

    private static String File_Image_Saved;

    /**
     * 从已经缓存下来的图片里找到图片
     * 为的是Toast.makeText能解析图片
     */
//    public static Bitmap getLocalBitmap(Context context, String fileName) {
//        Log.i(TAG, "getLocalBitmap: filename: " + fileName);
//        File_Image_Saved = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator;
//        File imageFile = new File(File_Image_Saved + fileName);
//        return getBitmap(imageFile);
//    }

    /**
     * 把文件转换为图片
     */
    public static Bitmap getBitmap(String file) {
        Log.d(TAG, "getBitmap: " + file);
        try {
            FileInputStream fis = new FileInputStream(file);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            Log.w(TAG, "getBitmap: file not found: " + file);
            return null;
        }
    }

    /**
     * 在QQ缓存中找到创建时间为time的文件
     * 图片我是到QQ的图片缓存目录
     * 拉一张与撤回消息同时期创建的文件
     * 但如果这张图片曾经发过, QQ就不会新生成文件只会在QQ内部文件夹生成链
     * 所以我就找不到了
     *
     * @param time 图片的创建时间
     * @return 是否找到了图片
     */
    // TODO: 28/04/2018 加一个网络状态参数 是wifi的话 误差就小一点 不是误差就大一点
    public static String[] searchImageFile(Context context, long time, String client) {
        Log.i(TAG, "searchImageFile: time: " + time + " client: " + client);
        String path = getPath(client);

        final File f = new File(path);
        Date start = new Date();
        final long mTime = time / 1000 * 1000;

        FilenameFilter filter = (file, name) -> {
            File f1 = new File(file + File.separator + name);
            //文件修改时间
            long modifiedTime = f1.lastModified();
//                Log.v(TAG, "accept: time: " + modifiedTime);
            long diff = modifiedTime - mTime;
            return diff < 10000 && diff >= 0;
        };

        File[] files = f.listFiles(filter);
        if (files == null || files.length == 0)
            return new String[0];

        Arrays.sort(files, (o1, o2) -> {
            long l1 = o1.length();
            long l2 = o2.length();
            if (l1 < l2)
                return 1;
            if (l1 > l2)
                return -1;
            else
                return 0;
        });

        Date end = new Date();
        Log.d(TAG, "searchImageFile: searching time: " + (end.getTime() - start.getTime()) + " mm");
        return saveBitmap(context, files, time);
    }

    @NonNull
    private static String getPath(String client) {
        String path = Environment.getExternalStorageDirectory() + "/Tencent/";
        switch (client) {
            case "QQ":
                path += "MobileQQ/diskcache";
                break;
            case "Tim":
                path += "Tim/diskcache";
                break;
        }
        return path;
    }

    /**
     * 拷贝文件到图片缓存中
     *
     * @param fileName 原文件名
     * @param time     新文件名
     */
    private static String[] saveBitmap(Context context, File[] fileName, long time) {
        File_Image_Saved = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator;

        String[] images = new String[fileName.length];
        for (int i = 0; i < fileName.length; i++) {
            File source = fileName[i];
            String dest = File_Image_Saved + String.valueOf(time) + "_" + i;
            Log.i(TAG, "saveBitmap: dest: " + dest);
            images[i] = dest;
            try (FileChannel inputChannel = new FileInputStream(source).getChannel();
                 FileChannel outputChannel = new FileOutputStream(dest).getChannel()) {
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return images;
    }


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        Log.i(TAG, "decodeSampledBitmapFromResource: in sample size: " + options.inSampleSize);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
