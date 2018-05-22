/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;

import android.app.Application;
import android.content.Context;


public class App extends Application {

    public static final int THEME_RED = 1;
    public static final int THEME_GREEN = 2;
    public static final int THEME_BLUE = 3;
    /**
     * dip到px单位转换
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale);
    }

    static public long timeClickedCheckPermissionButton;
}
