/*
 * Copyright (c) 2017.
 * qsboy.com 版权所有
 */

package com.qiansheng.messagecapture;

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
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.qiansheng.messagecapture.MainActivity.File_Image_Saved;


class XBitmap {

    private static final String TAG = "X-Bitmap";

    /**
     * 从已经缓存下来的图片里找到图片
     * 为的是Toast.makeText能解析图片
     *
     * @param name_time 文件名 在此为时间
     * @return 图片
     */
    static Bitmap getLocalBitmap(String name_time) {

        String dir = File_Image_Saved;
        File imageFile = new File(dir + name_time);
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
    static boolean getImageFileInQQ(final long time) {

        final String path = Environment.getExternalStorageDirectory() + File.separator +
                "tencent/MobileQQ/diskcache";
        final File f = new File(path);
        final SimpleDateFormat sdf = MessageCaptor.sdf;
        Date start = new Date();

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                if (!name.endsWith("_hd"))
                    return false;
                try {
                    File f = new File(file + File.separator + name);
                    long l = f.lastModified();                          //系统文件修改时间
                    String myString = sdf.format(l);                    //设为自己格式的时间
                    Date myDate = sdf.parse(myString);                  //转换成Date
                    long last = myDate.getTime();                       //转换成Long
                    long diff = Math.abs(last - time);                  //求时间差
                    if (diff < 10000) {
                        Log.d(TAG, " ");
                        Log.d(TAG, "File Name : " + name);
                        Log.d(TAG, "last time : " + last);
                        Log.d(TAG, "now  time : " + time);
                        Log.d(TAG, "Diff Time : " + diff);
                    }
                    return (diff < 100);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return false;
            }
        };

        File[] files = f.listFiles(filter);
        if (files == null || files.length == 0)
            return false;
        File file = null;
        if (files.length == 1)
            file = files[0];
        else {
            //因为这边的查找精度只精确到分 所以可能会有重复的情况,这样就输出最近的那张图片
            long l0 = 0;
            for (File fi : files) {
                long l = fi.lastModified();
                if (l > l0) {
                    l0 = l;
                    file = fi;
                }
            }
        }

        assert file != null;
        Log.i(TAG, "image: " + file.getName());

        saveBitmap(file, time);

        Date end = new Date();
        Log.w(TAG, "searching image file cost : " + (end.getTime() - start.getTime()) + " mm");

        return true;
    }

    /**
     * 拷贝文件到图片缓存中
     *
     * @param fileName 原文件名
     * @param time     新文件名
     */
    private static void saveBitmap(File fileName, long time) {

        String dest = File_Image_Saved + String.valueOf(time);
        try (FileChannel inputChannel = new FileInputStream(fileName).getChannel();
             FileChannel outputChannel = new FileOutputStream(dest).getChannel()) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
