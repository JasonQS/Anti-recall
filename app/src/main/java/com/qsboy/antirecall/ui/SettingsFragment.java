/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dd.CircularProgressButton;
import com.qsboy.antirecall.R;

public class SettingsFragment extends Fragment {

    // TODO: 帮助
    // TODO: 检查权限
    // TODO: 是否显示所有QQ记录
    // TODO: 是否显示所有微信记录
    // TODO: 是否电脑端微信自动登录
    // TODO: 打开辅助功能开关
    // TODO: 打开通知拦截开关
    // TODO: 检查更新
    // TODO: 关于
    // TODO: 打开通知拦截开关


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settins, container, false);

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

        return view;
    }
}
