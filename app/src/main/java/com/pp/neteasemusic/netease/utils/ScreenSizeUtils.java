package com.pp.neteasemusic.netease.utils;

import com.pp.neteasemusic.netease.room.RoomManager;

public class ScreenSizeUtils {
    public static int dip2px(float dpSize) {
        return (int) (dpSize * RoomManager.getDisplayMetrics().density + 0.5f);
    }

    public static int px2dip(float pxSize) {
        return (int) (pxSize / RoomManager.getDisplayMetrics().density + 0.5f);
    }
}
