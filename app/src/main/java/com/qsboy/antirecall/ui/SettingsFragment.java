/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qsboy.antirecall.R;
import com.qsboy.utils.UpdateHelper;

public class SettingsFragment extends Fragment {

    // TODO: 帮助
    // TODO: 检查权限

    String TAG = "SettingsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        view.setScrollContainer(true);

        View btnAccessibilityService = view.findViewById(R.id.btn_navigate_accessibility_service);
        View btnNotificationListener = view.findViewById(R.id.btn_navigate_notification_listener);

        // 设置
        // TODO: 跳转到悬浮窗设置界面
        // TODO: 跳转到设置界面
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

//        final CircularProgressButton circularButton1 = view.findViewById(R.id.circularButton1);
//        circularButton1.setIndeterminateProgressMode(true);
//        circularButton1.setOnClickListener(v -> {
//            if (circularButton1.getProgress() == 0) {
//                circularButton1.setProgress(50);
//            } else if (circularButton1.getProgress() == 100) {
//                circularButton1.setProgress(0);
//            } else {
//                circularButton1.setProgress(100);
//            }
//        });
//
//        final CircularProgressButton circularButton2 = view.findViewById(R.id.circularButton2);
//        circularButton2.setIndeterminateProgressMode(true);
//        circularButton2.setOnClickListener(v -> {
//            if (circularButton2.getProgress() == 0) {
//                circularButton2.setProgress(50);
//            } else if (circularButton2.getProgress() == -1) {
//                circularButton2.setProgress(0);
//            } else {
//                circularButton2.setProgress(-1);
//            }
//        });

        // 检查权限

        View btnCheckPermission = view.findViewById(R.id.btn_check_permission);
        btnCheckPermission.setOnClickListener(v -> {
            Log.i(TAG, "onCheckPermission: ");
            addView("悬浮窗权限", true);
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

    private View addView(String content, boolean isChecked) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//         LayoutInflater inflater1=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater inflater = getLayoutInflater();
//        LayoutInflater inflater3 = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.item_check_permission, null);
        TextView tvPermission = view.findViewById(R.id.tv_permission);
        ImageView ivChecked = view.findViewById(R.id.iv_checked);

        tvPermission.setText(content);
        if (isChecked)
            ivChecked.setImageResource(R.drawable.ic_accept);
        else
            ivChecked.setImageResource(R.drawable.ic_cancel);

        view.setLayoutParams(lp);
        return view;
    }
}
