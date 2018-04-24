/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.utils;


import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MySocket {

    public void client() {

        try {
            Socket socket = new Socket("192.168.1.32", 1989);
            InputStream inputStream = new FileInputStream("e://a.txt");
            OutputStream outputStream = socket.getOutputStream();

            byte buffer[] = new byte[1024];
            int temp;
            while ((temp = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, temp);
            }

            // 发送
            outputStream.flush();

            // 或创建一个报文，使用BufferedWriter写入,看你的需求 **/
            String socketData = "[hello]";
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(socketData.replace("\n", " ") + "\n");
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void server() {

        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();

            byte buffer[] = new byte[1024];
            int temp;
            while ((temp = inputStream.read(buffer)) != -1) {
                System.out.println(new String(buffer, 0, temp));
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
