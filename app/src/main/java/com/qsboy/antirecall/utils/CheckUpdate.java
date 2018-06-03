///*
// * Copyright © 2016 - 2018 by GitHub.com/JasonQS
// * anti-recall.qsboy.com
// * All Rights Reserved
// */
//
//package com.qsboy.utils;
//
//
//import android.app.Activity;
//import android.content.ActivityNotFoundException;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.content.FileProvider;
//import android.support.v7.app.AlertDialog;
//import android.widget.Toast;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//import java.net.URLConnection;
//
//public class CheckUpdate {
//
//    public static int getVersionCode(Context context) {
//        int versionCode1 = 0;
//        try {
//            if (context.getPackageManager() != null) {
//                // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
//                versionCode1 = context.getPackageManager().getPackageInfo(
//                        getPackageName(context), 0).versionCode;
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return versionCode1;
//    }
//
//    /**
//     * 获取应用的包名
//     **/
//    public static String getPackageName(Context context) {
//        return context.getPackageName();
//    }
//
//    /**
//     * 打开文件
//     * 兼容7.0
//     *
//     * @param context     activity
//     * @param file        File
//     * @param contentType 文件类型如：文本（text/html）
//     *                    当手机中没有一个app可以打开file时会抛ActivityNotFoundException
//     */
//    public static void startActionFile(Context context, File file, String contentType) throws ActivityNotFoundException {
//        if (context == null) {
//            return;
//        }
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//增加读写权限
//        intent.setDataAndType(getUriForFile(context, file), contentType);
//        if (!(context instanceof Activity)) {
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        }
//        context.startActivity(intent);
//
//    }
//
//    private static Uri getUriForFile(Context context, File file) {
//        if (context == null || file == null) {
//            throw new NullPointerException();
//        }
//        Uri uri;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
//        } else {
//            uri = Uri.fromFile(file);
//        }
//        return uri;
//    }
//
//    /**
//     * 显示升级提示
//     */
//    private void showUpdateVersionDialog() {
//        String updateLog = model.getChangelog();
//        final String apkURL = model.getInstall_url();
//        FileSize = model.getBinary().getFsize();
//        FileName = model.getName() + ".apk";
//        new AlertDialog.Builder(mContext)  //新建弹出框
//                .setTitle("更新提示")
//                .setIcon(R.mipmap.ic_launcher)
//                .setMessage(updateLog.equals("") ? "暂无更新提示信息" : updateLog)
//                // 更新
//                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        down_dialog = builder.show();
//                        Upload(apkURL);
//                    }
//                })
//                .setNegativeButton("稍后升级", null).show();
//    }
//
//    /**
//     * 开始下载操作
//     *
//     * @param apkURL 下载路径
//     */
//    private void Upload(final String apkURL) {
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    URL u = new URL(apkURL);
//                    URLConnection conn = u.openConnection();
//                    conn.connect();
//                    InputStream is = conn.getInputStream();
//                    if (is == null) {
//                        mHandler.sendEmptyMessage(DOWNLOAD_ERROR);
//                    } else {
//                        FileOutputStream fos = new FileOutputStream(Util.createFilePath(model.getName() + ".apk"));
//                        byte[] bytes = new byte[1024];
//                        int len;
//                        while ((len = is.read(bytes)) != -1) {
//                            fos.write(bytes, 0, len);
//                            downloadSize += len;
//                            res = downloadSize * 100 / FileSize;  //正在下载 下载的进度
//                            if (res == 100) {
//                                mHandler.sendEmptyMessage(DOWNLOAD_OK);
//                            } else {
//                                mHandler.sendEmptyMessage(DOWNLOAD_WORK);
//                            }
//                        }
//                        is.close();
//                        fos.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    mHandler.sendEmptyMessage(DOWNLOAD_NOT_FILE);
//                }
//                super.run();
//            }
//        }.start();
//    }
//
//    private Handler mHandler;
//
//    {
//        mHandler = new Handler() {
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case DOWNLOAD_WORK:
//                        mPrg.setMax(FileSize);
//                        mPrg.setProgress(downloadSize);
//                        mPrgTx.setText("已下载" + res + "%");
//                        break;
//                    case DOWNLOAD_OK:
//                        down_dialog.dismiss();
//                        try {
//                            File file = new File(Util.createFilePath(FileName));
//                            Util.startActionFile(mContext, file, "application/vnd.android.package-archive");
//                        } catch (ActivityNotFoundException E) {
//                            Toast.makeText(mContext, "沒有合适的程序打开此文件", Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//                    case DOWNLOAD_ERROR:
//                        down_dialog.dismiss();
//                        Toast.makeText(mContext, "下载出错", Toast.LENGTH_SHORT).show();
//                        break;
//                    case DOWNLOAD_NOT_FILE:
//                        down_dialog.dismiss();
//                        Toast.makeText(mContext, "未找到文件", Toast.LENGTH_SHORT).show();
//                        break;
//                    case DOWNLOAD_SHOW_DLG:
//                        PgDialog.dismiss();
//                        showUpdateVersionDialog();// 显示提示对话框
//                        break;
//                    default:
//                        break;
//                }
//            }
//        };
//    }
//
//    /**
//     * 检查版本
//     */
//    public void checkUpdate() {
//        if (Util.GetNetype(getBaseContext()) == -1) {  //判断网络状态
//            Toast.makeText(getBaseContext(), "网络断开了，请检查网络", Toast.LENGTH_SHORT).show();
//        } else {
//            DownLoad update = new DownLoad(MainActivity.this);  //开始下载
//            update.checkVersion(Util.GET_VERSION_URL);
//        }
//    }
//}
