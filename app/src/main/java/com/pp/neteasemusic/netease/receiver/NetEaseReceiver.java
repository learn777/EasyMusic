package com.pp.neteasemusic.netease.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pp.neteasemusic.netease.utils.OperationFrom;
import com.pp.neteasemusic.ui.music.songlist.SongListViewModel;

import java.util.Objects;

public class NetEaseReceiver extends BroadcastReceiver {
    public static final String NOTIFICATION_ITEM_BUTTON_PRE = "COM.PP.NET_EASE_MUSIC.PRE";
    public static final String NOTIFICATION_ITEM_BUTTON_PLAY = "COM.PP.NET_EASE_MUSIC.PLAY";
    public static final String NOTIFICATION_ITEM_BUTTON_NEXT = "COM.PP.NET_EASE_MUSIC.NEXT";

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("NetEaseReceiver:-------------" + intent.getAction() + "----------------");
        switch (Objects.requireNonNull(intent.getAction())) {
            case NOTIFICATION_ITEM_BUTTON_PRE:
                SongListViewModel.pressPre(OperationFrom.NOTIFICATION);
                break;
            case NOTIFICATION_ITEM_BUTTON_PLAY:
                SongListViewModel.pressPlay(OperationFrom.NOTIFICATION);
                break;
            case NOTIFICATION_ITEM_BUTTON_NEXT:
                SongListViewModel.pressNext(OperationFrom.NOTIFICATION);
                break;
        }
    }
}
