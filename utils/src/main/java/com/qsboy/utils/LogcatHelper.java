/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.utils;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class LogcatHelper {

    private static LogcatHelper INSTANCE = null;
    private static String PATH_LOGCAT;
    private LogDumper logDumper = null;
    private int pid;


    /**
     * 初始化目录
     */
    private void init() {
        // 优先保存到SD卡中
        PATH_LOGCAT = Environment.getExternalStorageDirectory() + File.separator + "Anti-recall";

        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            System.out.println("logcat: make dir: " + file.mkdirs());
        }


    }

    public static LogcatHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LogcatHelper();
        }
        return INSTANCE;
    }

    private LogcatHelper() {
        init();
        pid = android.os.Process.myPid();
    }

    public void start() {
        if (logDumper == null) {
            logDumper = new LogDumper(String.valueOf(pid), PATH_LOGCAT);
            logDumper.start();
        }
    }

    public void stop() {
        if (logDumper != null) {
            logDumper.stopLogs();
            logDumper = null;
        }
    }

    private class LogDumper extends Thread {

        private Process logcatProcess;
        private BufferedReader reader = null;
        private boolean isRunning = true;
        String cmd = null;
        private String pid;
        private FileOutputStream out = null;
        SimpleDateFormat sdf;

        LogDumper(String pid, String dir) {
            sdf = new SimpleDateFormat("MM-dd", Locale.CHINA);
            this.pid = pid;
            try {
                out = new FileOutputStream(new File(dir, "Anti-recall-"
                        + sdf.format(new Date()) + ".log"), true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            /*
             *
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
             *
             * 显示当前mPID程序的 E和W等级的日志.
             *
             */
            cmd = "logcat *:e *:i | grep \"(" + this.pid + ")\"";
//            cmd = "logcat -s";

        }

        private void stopLogs() {
            isRunning = false;
        }

        @Override
        public void run() {
            sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
            try {
                logcatProcess = Runtime.getRuntime().exec(cmd);
                reader = new BufferedReader(new InputStreamReader(
                        logcatProcess.getInputStream()), 4096);
                String line;
                while (isRunning && (line = reader.readLine()) != null) {
                    if (!isRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }

                    if (out != null && line.contains(pid)) {
                        out.write((line + "\n").getBytes());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProcess != null) {
                    logcatProcess.destroy();
                    logcatProcess = null;
                }
                if (reader != null) {
                    try {
                        reader.close();
                        reader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }

            }

        }

    }

}