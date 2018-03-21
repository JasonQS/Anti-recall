/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.access;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;


/**
 * Created by JasonQS
 */

public class GetNodes {

    private static int padding = 10;

    private static String TAG = "GetNodes";

    static String border = getBorder();

    public static void show(AccessibilityNodeInfo node) {
        log(border);
        iter(node, 0, "v");
        log(border);
    }

    public static void show(AccessibilityNodeInfo node, String level) {
        log(level, border);
        iter(node, 0, level);
        log(level, border);
    }

    private static String getBorder(){
        StringBuilder builder = new StringBuilder();
        builder.append("\t");
        for (int i = 0; i < padding; i++)
            builder.append("--------");
        builder.append("--------");
        return builder.toString();
    }

    private static void iter(AccessibilityNodeInfo node, int num, String level) {
        if (node == null) return;
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            log(level, "\n\t" + addPadding(num) + i + addPadding(num, padding) + print(node.getChild(i)));
            iter(node.getChild(i), num + 1, level);
        }
    }

    @NonNull
    private static String addPadding(int num) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < num; i++)
            builder.append("\t");
        return builder.toString();
    }

    @NonNull
    private static String addPadding(int num, int amount) {
        if (amount < num)
            return "";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < amount - num; i++)
            builder.append("\t");
        return builder.toString();
    }

    private static String print(AccessibilityNodeInfo nodeInfo) {

        if (nodeInfo == null)
            return "";

        CharSequence text = nodeInfo.getText();
        CharSequence description = nodeInfo.getContentDescription();
        CharSequence packageName = nodeInfo.getPackageName();
        CharSequence className = nodeInfo.getClassName();
        boolean focusable = nodeInfo.isFocusable();
        boolean clickable = nodeInfo.isClickable();
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        String viewId = nodeInfo.getViewIdResourceName();

        return "| " +
                "text: " + text + " \t" +
                "description: " + description + " \t" +
                "ID: " + viewId + " \t" +
                "class: " + className + " \t" +
                "location: " + rect + " \t" +
                "focusable: " + focusable + " \t" +
                "clickable: " + clickable + " \t" +
                "package: " + packageName + " \t" +
                '\n';

    }

    private static void log(String msg) {
        Log.v(TAG, msg);
    }

    private static void log(String level, String msg) {
        switch (level) {
            case "v":
                Log.v(TAG, msg);
                break;
            case "d":
                Log.d(TAG, msg);
                break;
            case "i":
                Log.i(TAG, msg);
                break;
            case "w":
                Log.w(TAG, msg);
                break;
            case "e":
                Log.e(TAG, msg);
                break;
        }
    }
}
