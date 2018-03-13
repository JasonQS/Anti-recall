package com.qsboy.antirecall.db;

import java.util.Date;

import javax.xml.namespace.NamespaceContext;

/**
 * Created by JasonQS
 */

public class Messages {

    private int id;
    private int recalledID;

    private boolean isWX;
    private String name;
    private String subName;
    private String message;
    private String time;
    private String[] images;

    public Messages(int id, boolean isWX, String name, String subName, String message) {
        this.id = id;
        this.isWX = isWX;
        this.name = name;
        this.subName = subName;
        this.message = message;
    }

    public Messages(int id, boolean isWX, String name, String subName, String message, String time) {
        this(id, isWX, name, subName, message);
        this.time = time;
    }

    public Messages(int id, boolean isWX, String name, String subName, String message, String time, String[] images) {
        this(id, isWX, name, subName, message, time);
        this.images = images;
    }

    @Override
    public String toString() {
        return id + "\t" + name + "\t" + subName + "\t" + message + "\t" + time + "\n";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecalledID() {
        return recalledID;
    }

    public void setRecalledID(int recalledID) {
        this.recalledID = recalledID;
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

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

}
