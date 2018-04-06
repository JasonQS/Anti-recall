/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.utils;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class UpdateHelper {

    private Context context;
    private Handler handler = new Handler();
    private String TAG = "X-Update";
    private Intent intent;
    private final String PATH;
    private final String appName;
    private final String savePath;
    private String code;
    private String desc;
    private String path;
    private File apkFile;
    boolean needUpdate = true;

    // TODO: 02/04/2018
    public UpdateHelper(Context context, Class<Activity> mainActivity) {

        this.context = context;
        intent = new Intent(this.context.getApplicationContext(), mainActivity);
        savePath = Environment.getExternalStorageDirectory() + File.separator + "Anti-recall";

        appName = "anti-recall.apk";
        PATH =
                "https://anti-recall.qsboy.com/version.json";
//                "http://www.qsboy.com/MessageCaptor/version.html";
    }

    public void checkUpdate() {

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(PATH, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    Log.d(TAG, "json: " + String.valueOf(jsonObject));
                    code = jsonObject.getString("code");
                    desc = jsonObject.getString("desc");
                    path = jsonObject.getString("path");

                    Log.d(TAG, "code: " + code);
                    Log.d(TAG, "desc: " + desc);
                    Log.d(TAG, "path: " + path);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                apkFile = new File(savePath, appName);
                if (needUpdate()) {
                    if (apkFile.exists()) {
                        Log.w(TAG, "show notice dialog");
                        showNoticeDialog();
                    } else {
                        Log.w(TAG, "download apk");
                        downloadAPK();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.w(TAG, error.toString());
            }
        });
        requestQueue.add(request);
    }

    public boolean isWifi() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "isWifi");
                return true;
            }
        }
        return false;
    }

    private boolean needUpdate() {
        int serverVersion = Integer.parseInt(code);
        int localVersion = 1;
        try {
            localVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        boolean b = serverVersion > localVersion;
        Log.d(TAG, "local version code : " + localVersion);
        Log.d(TAG, "need update?    " + b);

        needUpdate = b;
        return b;
    }

    private void showNoticeDialog() {
        Builder builder = new Builder(context);
        builder.setTitle("软件有更新");
        String message = desc;

        builder.setMessage(message);
        builder.setPositiveButton("安装", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                installAPK();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("下次再说", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void downloadAPK() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Date start = new Date();
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                        return;
                    HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    File apkFile = new File(savePath, appName);
                    FileOutputStream fos = new FileOutputStream(apkFile);

                    byte[] buffer = new byte[1024];
                    Log.w(TAG, "正在下载");
                    int i = 0;
                    while (true) {
                        i++;
                        int readNumber = is.read(buffer);
                        if (readNumber < 0) {
                            Log.w(TAG, "下载完毕");
                            Log.w(TAG, "文件大小: " + i + "kb");
                            Date end = new Date();
                            Log.w(TAG, "用时 " + (end.getTime() - start.getTime()) + " mm");
                            Log.w(TAG, "存储位置 : " + apkFile);

                            break;
                        }
                        fos.write(buffer, 0, readNumber);
                    }
                    fos.close();
                    is.close();
                    new XNotification(context, intent).showInstall();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void installAPK() {
        if (!apkFile.exists()) {
            Log.w(TAG, "apk isn't exists");
            return;
        }
        Log.w(TAG, "install apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://" + apkFile.toString());
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                apkFile.delete();
                Log.w(TAG, "Apk has been deleted");
            }
        }, 600000);                             //十分钟后删除安装包

    }

}
