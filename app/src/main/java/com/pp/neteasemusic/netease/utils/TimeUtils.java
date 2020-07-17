package com.pp.neteasemusic.netease.utils;

public class TimeUtils {
    public static String timeFormat(int duration) {
        duration = duration / 1000;
        int m = duration / 60;
        int s = duration % 60;
        return m + ":" + (s < 10 ? "0" + s : s);
    }
}
