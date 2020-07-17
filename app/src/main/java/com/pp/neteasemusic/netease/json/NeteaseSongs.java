package com.pp.neteasemusic.netease.json;

import java.util.List;

/**
 * @author PPM
 * @time 2020/5/13 1:01
 * @class NeteaseResult
 * @description 获取歌曲结果集
 */
public class NeteaseSongs {
    private String name;
    private String coverImgUrl;
    private PlayListCreator creator;
    private List<MusicInfo> tracks;
    private String id;

    public NeteaseSongs(String name, String coverImgUrl, List<MusicInfo> tracks) {
        this.name = name;
        this.coverImgUrl = coverImgUrl;
        this.tracks = tracks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public List<MusicInfo> getTracks() {
        return tracks;
    }

    public void setTracks(List<MusicInfo> tracks) {
        this.tracks = tracks;
    }

    public PlayListCreator getCreator() {
        return creator;
    }

    public void setCreator(PlayListCreator creator) {
        this.creator = creator;
    }
}
