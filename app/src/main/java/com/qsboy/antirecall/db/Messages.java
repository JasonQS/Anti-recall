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
    private String text;
    private String pkgName;
    private long time;
    private String images;

    public Messages() {

    }

    public Messages(int id, String name, String subName, String text) {
        this.id = id;
        this.name = name;
        this.subName = subName;
        this.text = text;
    }

    public Messages(int id, String name, String subName, String text, long time) {
        this(id, name, subName, text);
        this.time = time;
    }

    public Messages(int id, boolean isWX, String name, String subName, String text, long time, String images) {
        this(id, name, subName, text, time);
        this.images = images;
    }

    @Override
    public String toString() {
        return id + "\t" + name + "\t" + subName + "\t" + text + "\t" + time + "\n";
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

    public String getText() {
        return text;
    }

    public Messages setText(String text) {
        this.text = text;
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

    public Messages setImages(String images) {
        if (images == null)
            this.images = "";
        else
            this.images = images;
        return this;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }
}
