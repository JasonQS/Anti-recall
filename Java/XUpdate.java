/*
 * Copyright (c) 2017.
 * qsboy.com 版权所有
 */

package com.qiansheng.messagecapture;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
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

class XUpdate {

    private final String PATH;
    private final String mAppName;
    private final String mSavePath;
    private String mVersion_code;
    private String mVersion_desc;
    private String mVersion_path;
    private File apkFile;
    private Handler handler = new Handler();
    private Context mContext;
    private String TAG = "X-Update";
    private Intent intent;

    XUpdate(Context context) {

        mContext = context;
        intent = new Intent(mContext.getApplicationContext(), MainActivity.class);
        mSavePath = MainActivity.File_External_Storage;
        mAppName = mContext.getString(R.string.app_name) + ".apk";
        PATH =
                "http://www.qsboy.com/MessageCaptor/version.html";
//                "http://www.qsboy.com/MessageCapture/version.html";
//                "http://www.qsboy.com/mc/version";
    }

    void checkUpdate() {

        Log.d(TAG, "checkUpdate");

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        JsonObjectRequest request = new JsonObjectRequest(PATH, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
//                Message msg = Message.obtain();
//                msg.obj = jsonObject;
                try {
                    Log.d(TAG, "json: " + String.valueOf(jsonObject));
                    mVersion_code = jsonObject.getString("version_code");
                    mVersion_desc = jsonObject.getString("version_desc");
                    mVersion_path = jsonObject.getString("version_path");

                    Log.d(TAG, "version code: " + mVersion_code);
                    Log.d(TAG, "version desc: " + mVersion_desc);
                    Log.d(TAG, "version path: " + mVersion_path);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                apkFile = new File(mSavePath, mAppName);
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

    private boolean needUpdate() {
        int serverVersion = Integer.parseInt(mVersion_code);
        int localVersion = 1;
        try {
            localVersion = mContext.getPackageManager().getPackageInfo("com.qiansheng.messagecapture", 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        boolean a = serverVersion > localVersion;
        Log.i(TAG, "local version code : " + localVersion);
        Log.i(TAG, "need update?    " + a);
        return a;
    }

    private void showNoticeDialog() {
        Builder builder = new Builder(mContext);
        builder.setTitle("软件有更新");
        String message = mVersion_desc;

        builder.setMessage(message);
        builder.setPositiveButton("安装", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                installAPK();
                String date = String.valueOf(System.currentTimeMillis());
                new XFile(mContext).writeFile(date, MainActivity.File_Version);
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
                    HttpURLConnection conn = (HttpURLConnection) new URL(mVersion_path).openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    File apkFile = new File(mSavePath, mAppName);
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

                            new XNotification(mContext,intent).showInstall();

                            break;
                        }
                        fos.write(buffer, 0, readNumber);
                    }
                    fos.close();
                    is.close();

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
        mContext.startActivity(intent);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                apkFile.delete();
                Log.w(TAG, "Apk has been deleted");
            }
        }, 600000);                             //十分钟后删除安装包

    }

}
