package com.pp.neteasemusic.netease.utils;

import android.widget.Toast;

import com.pp.neteasemusic.netease.room.RoomManager;

public class ToastUtils {
    public static Toast ToastNoAppName(String msg) {
        Toast toast = Toast.makeText(RoomManager.getContext(), null, Toast.LENGTH_SHORT);
        toast.setText(msg);
        return toast;
    }
}
