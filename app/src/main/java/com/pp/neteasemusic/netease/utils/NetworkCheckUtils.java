package com.pp.neteasemusic.netease.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.pp.neteasemusic.netease.room.RoomManager;

public class NetworkCheckUtils {
    public synchronized static boolean checkNet() {
        ConnectivityManager connManager = (ConnectivityManager) RoomManager.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取代表联网状态的NetWorkInfo对象
        assert connManager != null;
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        return networkInfo.isAvailable();
    }
}
