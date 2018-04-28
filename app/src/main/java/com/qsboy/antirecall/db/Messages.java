/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.db;

public class Messages {

    private int id;
    private int recalledID;
    private String name;
    private String subName;
    private String message;
    private String pkgName;
    private long time;
    private String images;

    public Messages() {

    }

    public Messages(int id, String name, String subName, String message) {
        this.id = id;
        this.name = name;
        this.subName = subName;
        this.message = message;
    }

    public Messages(int id, String name, String subName, String message, long time) {
        this(id, name, subName, message);
        this.time = time;
    }

    public Messages(int id, boolean isWX, String name, String subName, String message, long time, String images) {
        this(id, name, subName, message, time);
        this.images = images;
    }

    @Override
    public String toString() {
        return id + "\t" + name + "\t" + subName + "\t" + message + "\t" + time + "\n";
    }

    public int getId() {
        return id;
    }

    public Messages setId(int id) {
        this.id = id;
        return this;
    }

    public int getRecalledID() {
        return recalledID;
    }

    public Messages setRecalledID(int recalledID) {
        this.recalledID = recalledID;
        return this;
    }

    public String getName() {
        return name;
    }

    public Messages setName(String name) {
        this.name = name;
        return this;
    }

    public String getSubName() {
        if (this.subName == null)
            return name;
        else
            return subName;
    }

    public Messages setSubName(String subName) {
        this.subName = subName;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Messages setMessage(String message) {
        this.message = message;
        return this;
    }

    public long getTime() {
        return time;
    }

    public Messages setTime(long time) {
        this.time = time;
        return this;
    }

    public String getImage() {
        if (images.contains(" "))
            return images.split(" ")[0];
        else return images;
    }

    public String getImages() {
        return images;
    }

    public Messages setImages(String images) {
        if (images == null)
            this.images = "";
        else
            this.images = images;
        return this;
    }

    public Messages setImages(String[] images) {
        StringBuilder builder = new StringBuilder();
        if (images != null && images.length != 0) {
            for (String image : images) {
                builder.append(image);
                builder.append(" ");
            }
            builder.deleteCharAt(builder.length() - 1);
        }
        this.images = builder.toString();
        return this;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }
}
