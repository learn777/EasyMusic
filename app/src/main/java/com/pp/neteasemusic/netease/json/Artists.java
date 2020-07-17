package com.pp.neteasemusic.netease.json;

/*
Artist:作者信息
 */
public class Artists {
    private String name;
    private int id;
    private String picUrl;

    public Artists(String name, int id, String picUrl) {
        this.name = name;
        this.id = id;
        this.picUrl = picUrl;
    }

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

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
