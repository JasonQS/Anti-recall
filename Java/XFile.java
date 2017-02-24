/*
 * Copyright (c) 2017.
 * qsboy.com 版权所有
 */

package com.qiansheng.messagecapture;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;

import static com.qiansheng.messagecapture.MainActivity.File_Withdraw;

public class XFile extends AppCompatActivity {

    private static final String TAG = "X-File";

    private Context mContext;

    XFile(Context context) {
        this.mContext = context;
    }

    public static class Search {
        private final String TAG = "X-Search";
        private String mPath;
        private RandomAccessFile rf;
        private int seek;
        private int size;

        Search(String path) throws IOException {
            mPath = path;
            rf = new RandomAccessFile(mPath, "r");
            size = (int) rf.length();
            seekEnd();
        }

        /**
         * 虽然名字是叫nextLine
         * 但是因为是从底部开始找的
         * 所以实际是向上一行
         *
         * @return line
         */
        String nextLine() {
            try {
                String line;
                while (true) {

                    seek--;
                    rf.seek(seek);

                    if (seek == 0) {
                        Log.e(TAG, "File Read Over");
                        line = rf.readLine();
                        String s = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                        Log.w(TAG, "The Last Line " + s);
                        return s;
                    }

                    if (rf.read() == '\n') {
                        line = rf.readLine();
                        if (line != null) {
                            String s = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                            Log.v(TAG, "seek: " + seek);
                            Log.d(TAG, "Next Line : " + s);
                            return s;
                        }
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * 向后一行
         *
         * @return line
         */
        String preLine() {
            try {

                String line;
                while (true) {

                    seek++;
                    if (seek == size)
                        return null;
                    rf.seek(seek);

                    if (rf.read() == '\n') {
                        line = rf.readLine();
                        String s = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                        Log.v(TAG, "seek : " + seek);
                        Log.i(TAG, "pre Line : " + s);
                        return s;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 指针调到文件末尾
         */
        void seekEnd() {
            seek = (size - 1);
        }

        void closeFile() {
            try {
                if (rf != null)
                    rf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 删除一行
     * 不传行号就是默认删除最后一行
     */
    public static class RemoveLine {
        private final String TAG = "X-Remove-Line";

        private Context mContext;
        private int lineNumber;
        private String mFileName;

        RemoveLine(String fileName, Context context) {
            this.mContext = context;
            this.mFileName = MainActivity.File_Dir + fileName;
            int num = 0;
            try {
                LineNumberReader lnr = new LineNumberReader(new FileReader(mFileName));
                lnr.skip(Long.MAX_VALUE);
                lnr.close();
                num = lnr.getLineNumber();
            } catch (IOException e) {
                e.printStackTrace();
            }
            lineNumber = num - 1;
            Log.i(TAG, "total " + num + " Line");
        }

        RemoveLine(String fileName, int lineNumber, Context context) {
            this.mContext = context;
            this.mFileName = fileName;
            this.lineNumber = lineNumber;
        }

        void remove() {
            // 边读内容边写到临时文件，如果行号是要删除的就不写
            try {
                File file = new File(mFileName);
                File temp = new File(file + "temp");
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(file)));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(temp)));
                String str;
                int index = 0;
                while (null != (str = reader.readLine())) {
                    if (index != lineNumber) {
                        writer.write(str + "\n");
                        Log.v(TAG, mFileName + " : " + str);
                    } else {
                        Log.w(TAG, "Remove : " + str);
                        XToast.makeText(mContext, str).show();
                    }
                    index++;
                }
                reader.close();
                writer.close();
                file.delete();      // 删除原文件
                temp.renameTo(file);// 临时文件改名成原文件名称
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 把传入的content加上换行写入filename
     */
    public void writeFile(String content, String fileName) {
        Log.e(TAG, "WRite " + content + " to " + fileName + "\n ");

        //在调试模式打开时把要写入的文件推送到通知栏
        if (Debug.DebugEnabled)
            new XNotification(mContext).show(content + " / " + fileName);

        if (content == null || fileName == null)
            return;

        content = format(content);

        try {
            FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            osw.write(content + "\n");
            osw.flush();
            fos.flush();
            osw.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 把换行号换成空格,不然会打乱文件格式
     *
     * @return 没有换行符的String
     */
    String format(String content) {
        if (content == null)
            return null;
        StringBuilder builder = new StringBuilder(content);
        int i;
        while ((i = builder.indexOf("\n")) > 0) {
            builder.replace(i, i + 1, " ");
        }
        return builder.toString();
    }

    /**
     * 从文件读一行 没什么通用性
     * 这里用作记录些永久数据,比如开关的打开情况
     */
    public String readFile(String fileName) {
        String s = null;
        try {
            FileInputStream fis = mContext.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            s = br.readLine();
            isr.close();
            fis.close();
//            Log.i(TAG, "Read " + fileName + " : " + s + "\n");
        } catch (Exception e) {
            Log.e(TAG, "Can Not Read File: File is null");
        }
        return s;
    }

    /**
     * 打印整个文件到控制台
     * 因为手机没有root,只好这么衰
     */
    public void printFile(String fileName) {
        try {
            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(fileName), "utf-8");
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                System.out.println(lineTxt);
            }
            bufferedReader.close();
            read.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 刷新UI配置
     */
    public void refresh() {
        Log.i(TAG, "Refresh...");

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                try {
                    FileInputStream fis = mContext.openFileInput(File_Withdraw);
                    InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                    BufferedReader br = new BufferedReader(isr);
                    XListAdapter.MsgList.clear();
                    XListAdapter.TimeList.clear();
                    XListAdapter.NameList.clear();
                    String line;
                    String content;
                    String time;
                    String name;
                    while ((line = br.readLine()) != null) {
                        int i = line.lastIndexOf('#');
                        if (i < 1)
                            continue;
                        Log.v(TAG, line);

                        content = line.substring(0, i - 11);
                        time = line.substring(i - 11, i);
                        name = line.substring(i + 1);
                        XListAdapter.MsgList.add(0, content);
                        XListAdapter.TimeList.add(0, time);
                        XListAdapter.NameList.add(0, name);

                    }
                    br.close();
                    isr.close();
                    fis.close();
                } catch (IOException e) {
                    Log.w(TAG, "File not found");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    boolean isDebug() {
        int i;
        try {
            FileInputStream fis = mContext.openFileInput("debug");
            i = fis.read();
            fis.close();
        } catch (IOException e) {
            return false;
        }
        return i == 1;
    }

    void setDebug(boolean flag) {
        try {
            FileOutputStream fos = mContext.openFileOutput("debug", MODE_PRIVATE);
            if (flag)
                fos.write(1);
            else
                fos.write(0);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isShowCheckedNotice() {
        int i;
        try {
            FileInputStream fis = mContext.openFileInput("showCheck");
            i = fis.read();
            fis.close();
        } catch (IOException e) {
            return true;
        }
        return i != 1;
    }

    public void setNotShowCheckedNotice() {

        try {
            FileOutputStream fos = mContext.openFileOutput("showCheck", MODE_PRIVATE);
            fos.write(1);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
