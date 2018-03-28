/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
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


public class XBitmap {

    private static final String TAG = "X-Bitmap";

    private static String File_Image_Saved;

    /**
     * 从已经缓存下来的图片里找到图片
     * 为的是Toast.makeText能解析图片
     *
     * @param name_time 文件名 在此为时间
     * @return 图片
     */
    public static Bitmap getLocalBitmap(String name_time, Context context) {
        File_Image_Saved = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator;
        File imageFile = new File(File_Image_Saved + name_time + "_0");
        return getBitmap(imageFile);
    }

    /**
     * 把文件转换为图片
     *
     * @param file 文件名
     * @return 图片
     */
    private static Bitmap getBitmap(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
    public static boolean searchImageFile(long time, String client) {
        String path = Environment.getExternalStorageDirectory() + "/Tencent/";
        switch (client) {
            case "QQ":
                path += "MobileQQ/diskcache";
                break;
            case "Tim":
                path += "Tim/diskcache";
                break;
        }

        final File f = new File(path);
        Date start = new Date();
        final long mTime = time / 1000 * 1000;

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                File f = new File(file + File.separator + name);
                long modifiedTime = f.lastModified();                          //系统文件修改时间
                return modifiedTime == mTime;
            }
        };

        File[] files = f.listFiles(filter);
        if (files == null || files.length == 0)
            return false;

        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                long l1 = o1.length();
                long l2 = o2.length();
                if (l1 < l2)
                    return 1;
                if (l1 > l2)
                    return -1;
                else
                    return 0;
            }
        });

        saveBitmap(files, time);

        Date end = new Date();
        Log.i(TAG, "searchImageFile: searching time: " + (end.getTime() - start.getTime()) + " mm");

        return true;
    }

    /**
     * 拷贝文件到图片缓存中
     *
     * @param fileName 原文件名
     * @param time     新文件名
     */
    private static void saveBitmap(File[] fileName, long time) {

        for (int i = 0; i < fileName.length; i++) {
            File source = fileName[i];
            String dest = File_Image_Saved + String.valueOf(time) + "_" + i;
            try (FileChannel inputChannel = new FileInputStream(source).getChannel();
                 FileChannel outputChannel = new FileOutputStream(dest).getChannel()) {
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
