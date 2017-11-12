package com.qsboy.antirecall.db;

/**
 * Created by JasonQS
 */

public class Messages {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String name;
    public int id;
    public int time;
    public String message;
    public String img;

    public Messages(int id, int time, String message) {
        this.id = id;
        this.message = message;
        this.time = time;
    }

    public Messages(int id, int time, String message, String img) {
        this(id, time, message);
        this.img = img;
    }

}
