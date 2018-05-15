/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.qsboy.antirecall.R;
import com.qsboy.utils.UpdateHelper;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import devlight.io.library.ntb.NavigationTabBar;
import ezy.assist.compat.SettingsCompat;

public class SettingsFragment extends Fragment {

    // TODO: 在scroller view 上下滚动时手动呼出底部导航

    String TAG = "SettingsFragment";
    Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ScrollView view = (ScrollView) inflater.inflate(R.layout.fragment_settings, container, false);

        View btnAccessibilityService = view.findViewById(R.id.btn_navigate_accessibility_service);
        View btnNotificationListener = view.findViewById(R.id.btn_navigate_notification_listener);
        View btnOverlays = view.findViewById(R.id.btn_navigate_overlays);
        View btnSettings = view.findViewById(R.id.btn_navigate_settings);

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
            startActivity(intent);
        });

        btnNotificationListener.setOnClickListener(v -> {
            String action = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
            Intent intent = new Intent(action);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        btnOverlays.setOnClickListener(v -> SettingsCompat.manageDrawOverlays(getActivity()));

        btnSettings.setOnClickListener(v -> SettingsCompat.manageWriteSettings(getActivity()));

        // 检查权限

//        btn.doneLoadingAnimation(fillColor, bitmap);
//        btn.revertAnimation();
//        final CircularProgressButton btnCheckPermission = view.findViewById(R.id.circularButton1);
        CircularProgressButton btnCheckPermission = view.findViewById(R.id.btn_check_permission);
        btnCheckPermission.setOnClickListener(v -> {
            btnCheckPermission.startAnimation();
            Log.i(TAG, "onCheckPermission: ");
            ViewGroup llPermission = view.findViewById(R.id.ll_permission);
            llPermission.removeAllViews();
            llPermission.addView(btnCheckPermission);
            handler.postDelayed(() -> addView(llPermission, "悬浮窗权限", true), 500);
            handler.postDelayed(() -> addView(llPermission, "辅助功能服务", false), 1000);
            handler.postDelayed(() -> addView(llPermission, "通知监听服务", true), 1500);
            handler.postDelayed(btnCheckPermission::revertAnimation, 2000);

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
//        if (helper.isWifi())
        // TODO: 02/05/2018 isWifi
        helper.checkUpdate();
        helper.setCheckUpdateListener((needUpdate, versionName) -> {
            if (needUpdate)
                tvRemoteVersion.setText("有更新: " + versionName);
            else
                tvRemoteVersion.setText("已是最新版");
        });
        return view;
    }

    private void addView(ViewGroup mainView, String content, boolean isChecked) {
//         LayoutInflater inflater1=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater inflater = getLayoutInflater();
//        LayoutInflater inflater3 = LayoutInflater.from(getActivity());

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
}
