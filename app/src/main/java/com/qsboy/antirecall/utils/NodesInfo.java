/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.utils;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

public class NodesInfo {

    private static int padding = 10;
    private static StringBuilder builder;

    public static void show(AccessibilityNodeInfo node, String TAG) {
        builder = new StringBuilder("");
        Log.v(TAG, "<----------------" + "\n" + iter(node, 0) + "\n" + "---------------->");
        Log.v(TAG, "\t\n---------------->");
    }

    public static void show(AccessibilityNodeInfo node, String TAG, String level) {
        builder = new StringBuilder("");
        log(level, TAG, "<----------------\n" + iter(node, 0) + "\n\n---------------->");
        log(level, TAG, "\t\n---------------->");
    }

    private static String iter(AccessibilityNodeInfo node, int num) {
        if (node == null)
            return "";
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            builder
                    .append("\n\t")
                    .append(addPadding(num))
                    .append(i)
                    .append(addPadding(num, padding))
                    .append(getLog(node.getChild(i)));
            iter(node.getChild(i), num + 1);
        }
        return builder.toString();
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

    private static String getLog(AccessibilityNodeInfo nodeInfo) {

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

        return "| "
                + "text: " + text + " \t"
                + "description: " + description + " \t"
                + "ID: " + viewId + " \t"
                + "class: " + className + " \t"
                + "location: " + rect + " \t"
                + "focusable: " + focusable + " \t"
                + "clickable: " + clickable + " \t"
                + "package: " + packageName + " \t"
                ;

    }

    private static void log(String msg) {
        Log.v("NodesInfo", msg);
    }

    private static void log(String level, String TAG, String msg) {
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
