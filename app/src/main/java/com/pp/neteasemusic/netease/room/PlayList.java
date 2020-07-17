package com.pp.neteasemusic.netease.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author PPM
 * @time 2020/5/13 20:54
 * @class PlayList
 * @description 歌单信息
 */
@Entity(tableName = "PlayList")
public class PlayList {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    private int id;

    @ColumnInfo
    private String playID;

    @ColumnInfo
    private String name;

    @ColumnInfo
    private String cover;

    public PlayList(String playID, String name, String cover) {
        this.playID = playID;
        this.name = name;
        this.cover = cover;
    }

    public String getPlayID() {
        return playID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

}
