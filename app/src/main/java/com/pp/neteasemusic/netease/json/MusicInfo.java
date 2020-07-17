package com.pp.neteasemusic.netease.json;

import java.util.List;

/*
MusicInfo:歌曲信息
 */
public class MusicInfo {
    private String name;
    private String id;
    private int duration;
    private List<Artists> artists;
    private Album album;

    public MusicInfo(String name, String id, int duration, List<Artists> artists, Album album) {
        this.name = name;
        this.id = id;
        this.duration = duration;
        this.artists = artists;
        this.album = album;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getDuration() {
        return duration;
    }

    public List<Artists> getArtists() {
        return artists;
    }

    public Album getAlbum() {
        return album;
    }
}
