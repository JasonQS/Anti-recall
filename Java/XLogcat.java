/*
 * Copyright (c) 2017.
 * qsboy.com 版权所有
 */

package com.qiansheng.messagecapture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


class XLogcat {

    private static XLogcat INSTANCE = null;
    private static String PATH_LOGCAT;
    private LogDumper mLogDumper = null;
    private int mPId;


    /**
     * 初始化目录
     */
    private void init() {
        // 优先保存到SD卡中
        PATH_LOGCAT = MainActivity.File_External_Storage;

        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }


    }

    static XLogcat getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new XLogcat();
        }
        return INSTANCE;
    }

    private XLogcat() {
        init();
        mPId = android.os.Process.myPid();
    }

    void start() {
        if (mLogDumper == null) {
            mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);
            mLogDumper.start();
        }
    }

    void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private class LogDumper extends Thread {

        private Process logcatProcess;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmd = null;
        private String mPID;
        private FileOutputStream out = null;
        SimpleDateFormat sdf;

        LogDumper(String pid, String dir) {
            sdf = new SimpleDateFormat("MM-dd", Locale.CHINA);
            mPID = pid;
            try {
                out = new FileOutputStream(new File(dir, "MC-"
                        + sdf.format(new Date()) + ".log"), true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            /**
             *
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
             *
             * 显示当前mPID程序的 E和W等级的日志.
             *
             * */
            cmd = "logcat *:e *:d | grep \"(" + mPID + ")\"";
//            cmd = "logcat -s | grep \"(" + mPID + ")\"";
//            cmd = "logcat -s";

        }

        void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
            try {
                logcatProcess = Runtime.getRuntime().exec(cmd);
                mReader = new BufferedReader(new InputStreamReader(
                        logcatProcess.getInputStream()), 1024);
                String line;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (out != null && line.contains(mPID)) {
                        out.write((line + "\n")
                                .getBytes());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProcess != null) {
                    logcatProcess.destroy();
                    logcatProcess = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
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