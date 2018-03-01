package com.qsboy.antirecall.db;

/**
 * Created by JasonQS
 */

public class Messages {

    private int id;

    private boolean isWX;
    private String name;
    private String subName;
    private String message;
    private String time;
    private String image;

    public Messages(int id, boolean isWX, String name, String subName, String message, String time) {
        this.id = id;
        this.isWX = isWX;
        this.name = name;
        this.subName = subName;
        this.message = message;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isWX() {
        return isWX;
    }

    public void setWX(boolean WX) {
        isWX = WX;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubName() {
        if (this.subName == null)
            return name;
        else
            return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
