/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.qsboy.antirecall.utils.CheckAuthority;
import com.qsboy.antirecall.utils.UpdateHelper;

import java.util.Date;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import devlight.io.library.ntb.NavigationTabBar;
import ezy.assist.compat.SettingsCompat;

public class SettingsFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    // TODO: 在scroller view 上下滚动时手动呼出底部导航

    String TAG = "SettingsFragment";
    Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ScrollView view = (ScrollView) inflater.inflate(R.layout.fragment_settings, container, false);

        View btnAccessibilityService = view.findViewById(R.id.btn_navigate_accessibility_service);
        View btnNotificationListener = view.findViewById(R.id.btn_navigate_notification_listener);
        View btnOverlays = view.findViewById(R.id.btn_navigate_overlays);
        View btnCheckUpdate = view.findViewById(R.id.btn_check_update);

        ((MySwitchCompat) view.findViewById(R.id.switch_show_all_qq_messages))
                .setAttr(App.class, "isShowAllQQMessages");
        ((MySwitchCompat) view.findViewById(R.id.switch_we_chat_auto_login))
                .setAttr(App.class, "isWeChatAutoLogin");
        ((MySwitchCompat) view.findViewById(R.id.switch_swipe_remove_on))
                .setAttr(App.class, "isSwipeRemoveOn");
        ((MySwitchCompat) view.findViewById(R.id.switch_check_update_only_on_wifi))
                .setAttr(App.class, "isCheckUpdateOnlyOnWiFi");

        // 底部navigation bar
        view.setOnTouchListener((v, event) -> {
            NavigationTabBar navigationTabBar = getActivity().findViewById(R.id.ntb_horizontal);
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

        // 设置
        btnAccessibilityService.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        btnNotificationListener.setOnClickListener(v -> {
            // TODO: 28/05/2018
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        btnOverlays.setOnClickListener(v -> SettingsCompat.manageDrawOverlays(getActivity()));

        btnCheckUpdate.setOnClickListener((v) -> {
            UpdateHelper helper = new UpdateHelper(getActivity());
            helper.checkUpdate();
            helper.setCheckUpdateListener((needUpdate, versionName) -> {
                if (needUpdate)
                    Toast.makeText(getContext(), "有更新: " + versionName, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "已是最新版", Toast.LENGTH_SHORT).show();
            });
        });

        // 检查权限

        CircularProgressButton btnCheckPermission = view.findViewById(R.id.btn_check_permission);
        btnCheckPermission.setOnClickListener(v -> {
            btnCheckPermission.performAccessibilityAction(AccessibilityEvent.TYPE_VIEW_CLICKED, null);
            btnCheckPermission.startAnimation();
            ViewGroup llPermission = view.findViewById(R.id.ll_permission);
            llPermission.removeAllViews();
            llPermission.addView((View) btnCheckPermission.getParent());
            handler.postDelayed(() -> addView(llPermission, "悬浮窗权限", checkFloatingPermission()), 500);
            handler.postDelayed(() -> addView(llPermission, "辅助功能权限授予", isAccessibilitySettingsOn()), 1000);
            handler.postDelayed(() -> addView(llPermission, "辅助功能正常使用", isAccessibilityServiceWork()), 1500);
            handler.postDelayed(() -> addView(llPermission, "通知监听服务", isNotificationListenersEnabled()), 2000);

            handler.postDelayed(() -> {
                if (checkFloatingPermission() && isAccessibilitySettingsOn() && isAccessibilityServiceWork() && isNotificationListenersEnabled())
                    btnCheckPermission.doneLoadingAnimation(
                            getResources().getColor(R.color.colorCorrect),
                            getBitmap(R.drawable.ic_accept));
                else btnCheckPermission.doneLoadingAnimation(
                        getResources().getColor(R.color.colorError),
                        getBitmap(R.drawable.ic_cancel));
            }, 2500);

            handler.postDelayed(btnCheckPermission::revertAnimation, 5000);

        });

        // 关于

        TextView tvLocalVersion = view.findViewById(R.id.tv_local_version);
        TextView tvRemoteVersion = view.findViewById(R.id.tv_remote_version);

        try {
            tvLocalVersion.setText(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        UpdateHelper helper = new UpdateHelper(getActivity());

        if (App.isCheckUpdateOnlyOnWiFi && !helper.isWifi())
            return view;

        helper.checkUpdate();
        helper.setCheckUpdateListener((needUpdate, versionName) -> {
            if (needUpdate)
                tvRemoteVersion.setText("有更新: " + versionName);
            else
                tvRemoteVersion.setText("已是最新版");
        });
        return view;
    }

    private boolean checkFloatingPermission() {
        return new CheckAuthority(getContext()).checkAlertWindowPermission();
    }

    public boolean isAccessibilitySettingsOn() {
        if (getContext() == null)
            return false;
        final String service = getContext().getPackageName() + "/" + MainService.class.getCanonicalName();  //这里改成自己的class
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
        Log.w(TAG, "isAccessibilityServiceWork: time: " + (new Date().getTime() - App.timeClickedCheckPermissionButton));
        return (new Date().getTime() - App.timeClickedCheckPermissionButton) < 5000;
    }

    private boolean isNotificationListenersEnabled() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void addView(ViewGroup mainView, String content, boolean isChecked) {

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.item_check_permission, null);
        TextView tvPermission = view.findViewById(R.id.tv_permission);
        ImageView ivChecked = view.findViewById(R.id.iv_checked);

        tvPermission.setText(content);
        if (isChecked) {
            ivChecked.setImageResource(R.drawable.ic_accept);
            ivChecked.setColorFilter(Color.GREEN);
        } else {
            ivChecked.setImageResource(R.drawable.ic_cancel);
            ivChecked.setColorFilter(Color.RED);
        }

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
