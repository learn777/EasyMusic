package com.pp.neteasemusic.netease.json;

/*
NeteaseResult:歌单信息
 */
public class NeteaseResult {
    private NeteaseSongs result;
    private String coverImgUrl;

    public NeteaseResult(NeteaseSongs result) {
        this.result = result;
    }

    public NeteaseSongs getResult() {
        return result;
    }

    public void setResult(NeteaseSongs result) {
        this.result = result;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }
}

