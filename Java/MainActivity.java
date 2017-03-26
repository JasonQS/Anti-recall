/*
 * Copyright (c) 2017.
 * qsboy.com 版权所有
 */

package com.qiansheng.messagecapture;


import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private XListAdapter mAdapter;
    private XFile xFile;
    private final String TAG = "Activity";
    static final String File_Version = "version";
    static final String File_Withdraw = "withdraw";
    static String File_Withdraw_Msg;
    static String File_Image_Saved;
    static String File_Dir;
    static String File_External_Storage;
    XUpdate xUpdate;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "onCreate");

        File_Dir = getFilesDir() + File.separator;
        File_Withdraw_Msg = File_Dir + File_Withdraw + File.separator;
        File_Image_Saved = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator;
        File_External_Storage = Environment.getExternalStorageDirectory() + File.separator + "MessageCaptor";

        initView();

        xFile = new XFile(this);

        checkUpdate();

        checkPermission();

    }


    void initView() {

        setContentView(R.layout.activity);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new XListAdapter();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        xFile.refresh();
                        recyclerView.setAdapter(mAdapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 700);
            }
        });

        mAdapter.setOnItemClickListener(new XListAdapter.OnItemClickListener() {

            @Override
            public void onItemLongClick(View view, final int position) {
                MainActivity.this.itemOnLongClick(position);
            }
        });

        findViewById(R.id.btn_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOnClick();
            }

        });
        xUpdate = new XUpdate(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        xFile.refresh();
        recyclerView.setAdapter(mAdapter);

    }

    void checkPermission() {
        if (!XCheckPermission.checkFloatWindowPermission(this)) {
            AlertDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
            builder.setMessage("检测到您是小米手机, 请打开悬浮窗权限以保证软件正常运行");
            builder.setPositiveButton("带我去设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    XCheckPermission.applyMiuiPermission(MainActivity.this);
                }
            });

            dialog = builder.create();
            dialog.show();

        }

    }

    public void removeItem(int position, XListAdapter mAdapter) {
        Log.i(TAG, "RemoveItem");
        XListAdapter.MsgList.remove(position);
        XListAdapter.TimeList.remove(position);
        XListAdapter.NameList.remove(position);
        mAdapter.notifyItemRemoved(position);
        int count = mAdapter.getItemCount();
        int p = count - position;

        new XFile.RemoveLine(File_Withdraw_Msg, p, this).remove();

    }

    private void menuOnClick() {

        Fragment menu = new FMenu();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction;
        transaction = fm.beginTransaction();
        transaction.setCustomAnimations(R.anim.menu_enter, R.anim.menu_exit, R.anim.menu_enter, R.anim.menu_exit);
        transaction.add(R.id.fragment_message_list, menu, "menu");
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private void itemOnLongClick(final int position) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setPositiveButton("复制", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                String text = XListAdapter.MsgList.get(position);
                ClipData cd = ClipData.newPlainText("text", text);
                cm.setPrimaryClip(cd);
                dialog.dismiss();
                XToast.makeText(MainActivity.this, "已复制到剪切板").show();
            }
        });
        builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeItem(position, mAdapter);
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();

        Button neg = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

        neg.setTextColor(ContextCompat.getColor(this, R.color.colorTextLight));
        pos.setTextColor(ContextCompat.getColor(this, R.color.colorTextLight));
        neg.setTextSize(20);
        pos.setTextSize(20);

    }

    private boolean ifCheckVersion() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.CHINA);

        XFile xFile = new XFile(this);
        long lastDate;
        try {
            lastDate = Long.parseLong(xFile.readFile(File_Version));
        } catch (NumberFormatException e) {
            String date = String.valueOf(System.currentTimeMillis());
            new XFile(getApplicationContext()).writeFile(date, MainActivity.File_Version);
            return true;
        }
        Calendar calendar = Calendar.getInstance();
        int now = calendar.get(Calendar.DAY_OF_MONTH);
        int last = Integer.parseInt(sdf.format(lastDate));

        int diff = now - last;
        Log.i(TAG, "dif " + diff + " day");

        return (diff >= 1);      //一天
        // TODO: 2017/1/21 加入检测周活跃量
    }

    private boolean isWifi() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.i(TAG, "isWifi");
                return true;
            }
        }
        return false;
    }

    public void checkUpdate(View view) {
        //wifi环境下检查更新

        xUpdate.checkUpdate();
                        new Handler().postDelayed(new CheckVersion(),500);
    }

    class CheckVersion implements Runnable {

        @Override
        public void run() {

            if (!xUpdate.needUpdate)
                XToast.makeText(MainActivity.this, "已是最新版").show();
        }
    }

    void checkUpdate() {
        //wifi环境下检查更新
        if (isWifi())
            if (ifCheckVersion())
                new XUpdate(this).checkUpdate();
    }

    /**
     * 之前的版本加了"清除数据"的按钮,后来去掉了
     */
/*
    private void showConfirmClear() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确定要清除数据吗");
        builder.setMessage("文本数据很小,若是清除了就再也找不回来了");
        builder.setNeutralButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearData();
                XToast.makeText(getApplicationContext(), "数据已清除").show();
            }
        });
        builder.setPositiveButton("取消", null);
        builder.create().show();
    }

    private void clearData() {
        Log.i(TAG, "CLEAR DATA");
        String path = getFilesDir().getParentFile().getPath();
        File file = new File(path);
        File[] childFile = file.listFiles();
        Log.w(TAG, Arrays.toString(childFile));
        RemoveFile(file);
        XListAdapter.MsgList.clear();
        XListAdapter.TimeList.clear();
        XListAdapter.NameList.clear();
        recyclerView.setAdapter(mAdapter);
        Log.i(TAG, path + "is clear!");
    }

    private void RemoveFile(File file) {
        if (file.isFile()) {
            Log.i(TAG, file.getAbsolutePath());
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                Log.w(TAG, "directory : " + file.getName());
                file.delete();
                return;
            }
            for (File f : childFile) {
                RemoveFile(f);
            }
            file.delete();
        }
    }
*/

}
