/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui.fragment;

import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.qsboy.antirecall.R;
import com.qsboy.antirecall.access.MainService;
import com.qsboy.antirecall.ui.activyty.App;
import com.qsboy.antirecall.ui.widget.MySwitchCompat;
import com.qsboy.antirecall.utils.CheckAuthority;
import com.qsboy.antirecall.utils.UpdateHelper;

import java.io.File;
import java.util.Date;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import devlight.io.library.ntb.NavigationTabBar;
import ezy.assist.compat.SettingsCompat;

public class SettingsFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    String TAG = "SettingsFragment";
    Handler handler = new Handler();
    long clickTime = new Date().getTime();
    int clicks = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        ScrollView view = (ScrollView) inflater.inflate(R.layout.fragment_settings, container, false);

        initPermissionCheck(view);

        initSettings(view);

        initBottomNavigationBar(view);

        initAbout(view);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        menu.clear();
        inflater.inflate(R.menu.toolbar_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_my)
            if (getFragmentManager() != null)
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.activity_main, new MyFragment())
                        .addToBackStack("my")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
        return super.onOptionsItemSelected(item);
    }

    private void initSettings(ScrollView view) {
        // 设置
        ((MySwitchCompat) view.findViewById(R.id.switch_show_all_qq_messages))
                .setAttr(App.class, "isShowAllQQMessages")
                .setOnCheckedChangeListener((compoundButton, b) -> App.layoutHeight = -1);
        ((MySwitchCompat) view.findViewById(R.id.switch_we_chat_auto_login))
                .setAttr(App.class, "isWeChatAutoLogin");
        ((MySwitchCompat) view.findViewById(R.id.switch_swipe_remove_on))
                .setAttr(App.class, "isSwipeRemoveOn");
        ((MySwitchCompat) view.findViewById(R.id.switch_check_update_only_on_wifi))
                .setAttr(App.class, "isCheckUpdateOnlyOnWiFi");
    }

    private void initPermissionCheck(ScrollView view) {
        // TODO: 28/06/2018 跳转跟到权限检查里去
        // 跳转
        View btnAccessibilityService = view.findViewById(R.id.btn_navigate_accessibility_service);
        View btnNotificationListener = view.findViewById(R.id.btn_navigate_notification_listener);
        View btnOverlays = view.findViewById(R.id.btn_navigate_overlays);

        btnAccessibilityService.setOnClickListener(v -> jumpToAccessSetting());

        btnNotificationListener.setOnClickListener(v -> jumpToNotificationListenerSetting());

        btnOverlays.setOnClickListener(v -> SettingsCompat.manageDrawOverlays(getActivity()));

        // TODO: 14/06/2018 外部文件读写权限检查
        // TODO: 14/06/2018 通知拦截功能检查
        // 检查权限
        CircularProgressButton btnCheckPermission = view.findViewById(R.id.btn_check_permission);
        btnCheckPermission.setOnClickListener(v -> {
            btnCheckPermission.performAccessibilityAction(AccessibilityEvent.TYPE_VIEW_CLICKED, null);
            btnCheckPermission.startAnimation();
            ViewGroup llPermission = view.findViewById(R.id.ll_permission);
            llPermission.removeAllViews();
            llPermission.addView((View) btnCheckPermission.getParent());

            boolean accessibilityServiceSettingEnabled = isAccessibilityServiceSettingEnabled();
            handler.postDelayed(() -> addView(llPermission, "辅助功能权限授予",
                    accessibilityServiceSettingEnabled,
                    v1 -> jumpToAccessSetting()), 500);

            handler.postDelayed(() -> addView(llPermission, "辅助功能正常工作",
                    isAccessibilityServiceWork(),
                    v1 -> jumpToAccessSetting()), 1500);

            boolean notificationListenerSettingEnabled = isNotificationListenerSettingEnabled();
            handler.postDelayed(() -> {
                addView(llPermission, "通知监听服务",
                        notificationListenerSettingEnabled,
                        v1 -> jumpToNotificationListenerSetting());
                if (notificationListenerSettingEnabled)
                    sendNotification();
            }, 1000);

            handler.postDelayed(() -> addView(llPermission, "通知监听服务正常工作",
                    isNotificationListenerWork(),
                    v1 -> jumpToNotificationListenerSetting()), 2000);

            handler.postDelayed(() -> addView(llPermission, "悬浮窗权限",
                    checkFloatingPermission(),
                    v1 -> SettingsCompat.manageDrawOverlays(getActivity())), 2500);

            handler.postDelayed(() -> {
                if (checkFloatingPermission() && accessibilityServiceSettingEnabled &&
                        isAccessibilityServiceWork() && notificationListenerSettingEnabled && isNotificationListenerWork())
                    btnCheckPermission.doneLoadingAnimation(
                            getResources().getColor(R.color.colorCorrect),
                            getBitmap(R.drawable.ic_accept));
                else btnCheckPermission.doneLoadingAnimation(
                        getResources().getColor(R.color.colorError),
                        getBitmap(R.drawable.ic_cancel));
            }, 3000);

            handler.postDelayed(btnCheckPermission::revertAnimation, 5000);
        });
    }

    private void jumpToNotificationListenerSetting() {
        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void jumpToAccessSetting() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void initBottomNavigationBar(ScrollView view) {
        // 底部navigation bar的show hide
        view.setOnTouchListener((v, event) -> {
            NavigationTabBar navigationTabBar = getActivity().findViewById(R.id.ntb);
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (event.getHistorySize() < 1)
                        return false;
                    float y = event.getY();
                    float historicalY = event.getHistoricalY(event.getHistorySize() - 1);
                    if (y > historicalY)
                        navigationTabBar.show();
                    else
                        navigationTabBar.hide();
            }
            return false;
        });
    }

    private void initAbout(ScrollView view) {
        // 关于

        View btnCheckUpdate = view.findViewById(R.id.btn_check_update);
        TextView tvLocalVersion = view.findViewById(R.id.tv_local_version);
        TextView tvRemoteVersion = view.findViewById(R.id.tv_remote_version);
        btnCheckUpdate.setOnClickListener((v) -> {
            UpdateHelper helper = new UpdateHelper(getActivity());
            helper.checkUpdate();
            helper.setCheckUpdateListener(new UpdateHelper.CheckUpdateListener() {

                @Override
                public void needUpdate(boolean needUpdate, String versionName) {
                    if (needUpdate) {
                        tvRemoteVersion.setText("有更新: " + versionName);
                        Toast.makeText(getContext(), "有更新: " + versionName, Toast.LENGTH_SHORT).show();
                    } else {
                        tvRemoteVersion.setText("已是最新版");
                        Toast.makeText(getContext(), "已是最新版", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void error() {
                    Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // 当前版本号
        try {
            tvLocalVersion.setText(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        initLogViewer(view);

    }

    private void initLogViewer(ScrollView view) {
        // TODO: 2018/7/2 查看日志
        view.findViewById(R.id.copyright).setOnClickListener(v -> {
            long clickTime = new Date().getTime();
            if (clickTime - this.clickTime > 1000)
                clicks = 1;
            else
                clicks++;
            this.clickTime = clickTime;
            if (clicks == 5) {
                File file = new File(getActivity().getExternalFilesDir("logs"), "Anti-recall-06-14.log");
                Log.i(TAG, "onCreateView: file: " + file);
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri uri = FileProvider.getUriForFile(getActivity(), "com.qsboy.provider", file.getParentFile());
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(uri, "*/*");
                } else {
                    intent.setDataAndType(Uri.fromFile(file), "*/*");
                }
                startActivity(intent);
            }
//            public static void shareFile(Context context, Uri uri) {
            // File file = new File("\sdcard\android123.cwj"); //附件文件地址
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra("subject", ""); //
            intent.putExtra("body", ""); // 正文
//                intent.putExtra(Intent.EXTRA_STREAM, uri); // 添加附件，附件为file对象
//                if (uri.toString().endsWith(".gz")) {
//                    intent.setType("application/x-gzip"); // 如果是gz使用gzip的mime
//                } else if (uri.toString().endsWith(".txt")) {
//                    intent.setType("text/plain"); // 纯文本则用text/plain的mime
//                } else {
            intent.setType("application/octet-stream"); // 其他的均使用流当做二进制数据来发送
//                }
            startActivity(intent); // 调用系统的mail客户端进行发送
//            }
        });
    }

    private boolean checkFloatingPermission() {
        return new CheckAuthority(getContext()).checkAlertWindowPermission();
    }

    public boolean isAccessibilityServiceSettingEnabled() {
        if (getContext() == null)
            return false;
        final String service = getContext().getPackageName() + "/" + MainService.class.getCanonicalName();
        int accessibilityEnabled = Settings.Secure.getInt(getContext().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0);
        if (accessibilityEnabled != 1)
            return false;
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        String settingValue = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

        mStringColonSplitter.setString(settingValue);
        while (mStringColonSplitter.hasNext()) {
            String accessibilityService = mStringColonSplitter.next();
            if (accessibilityService.equalsIgnoreCase(service)) {
                return true;
            }
        }

        return false;
    }

    private boolean isAccessibilityServiceWork() {
        Log.w(TAG, "isAccessibilityServiceWork: clickTime: " + (new Date().getTime() - App.timeCheckAccessibilityServiceIsWorking));
        return (new Date().getTime() - App.timeCheckAccessibilityServiceIsWorking) < 5000;
    }

    private boolean isNotificationListenerSettingEnabled() {
        if (getContext() == null)
            return false;
        String notificationEnabled = Settings.Secure.getString(getContext().getContentResolver(), "enabled_notification_listeners");
        if (TextUtils.isEmpty(notificationEnabled))
            return false;
        for (String name : notificationEnabled.split(":")) {
            ComponentName cn = ComponentName.unflattenFromString(name);
            if (cn != null) {
                if (TextUtils.equals(getContext().getPackageName(), cn.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNotificationListenerWork() {
        Log.w(TAG, "isNotificationListenerWork: clickTime: " + (new Date().getTime() - App.timeCheckNotificationListenerServiceIsWorking));

        FragmentActivity activity = getActivity();
        if (activity == null)
            return false;
        NotificationManager manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null)
            return false;
        manager.cancel(12);

        return (new Date().getTime() - App.timeCheckNotificationListenerServiceIsWorking) < 5000;
    }

    private void sendNotification() {
        FragmentActivity activity = getActivity();
        if (activity == null)
            return;
        NotificationManager manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "1", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(activity, "1")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("title")
                .setContentText("test")
                .setDefaults(Notification.FLAG_ONLY_ALERT_ONCE)
                .build();
//        notification.defaults = ;
        manager.notify(12, notification);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void addView(ViewGroup mainView, String content, boolean isChecked, View.OnClickListener onClickListener) {

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.item_check_permission, mainView, false);
        TextView tvPermission = view.findViewById(R.id.tv_permission);
        ImageView ivChecked = view.findViewById(R.id.iv_checked);
        ImageView ivFix = view.findViewById(R.id.iv_fix);

        tvPermission.setText(content);
        if (isChecked) {
            ivChecked.setImageResource(R.drawable.ic_accept);
            ivChecked.setColorFilter(0xCC00FF00);
            ivFix.setVisibility(View.GONE);
        } else {
            ivChecked.setImageResource(R.drawable.ic_cancel);
            ivChecked.setColorFilter(0xAAFF0000);
        }

        if (!isChecked)
            view.setOnClickListener(onClickListener);
        mainView.addView(view);
    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = VectorDrawableCompat.create(getResources(), drawableRes, null);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
