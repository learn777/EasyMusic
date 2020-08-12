package com.pp.neteasemusic.netease.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.pp.neteasemusic.R;
import com.pp.neteasemusic.netease.receiver.NetEaseReceiver;
import com.pp.neteasemusic.netease.room.RoomManager;
import com.pp.neteasemusic.netease.utils.ScreenSizeUtils;
import com.pp.neteasemusic.netease.volley.VolleySingleton;
import com.pp.neteasemusic.ui.songlist.SongListViewModel;

public class NeteaseNotification {
    private static Notification instance;
    private static NotificationManager notificationManager;
    private static int NOTIFICATION_REQUEST_CODE_MUSIC = 0;
    private static int PENDING_REQUEST_CODE_MUSIC = 1;


    public synchronized static void initInstance() {
        int px = ScreenSizeUtils.dip2px(60);
        if (instance == null && RoomManager.getContext() != null) {
            notificationManager = (NotificationManager) RoomManager.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel channel = new NotificationChannel(RoomManager.getContext().getPackageName(), "简易云音乐", NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(false);
                channel.enableVibration(false);
                if (Build.VERSION.SDK_INT >= 29) {
                    channel.setAllowBubbles(false);
                }
                notificationManager.createNotificationChannel(channel);
            }
            final RemoteViews remoteViews = new RemoteViews(RoomManager.getContext().getPackageName(), R.layout.cell_notification_music);
            remoteViews.setTextViewText(R.id.song_name, "歌名");
            VolleySingleton.getInstance(RoomManager.getContext()).add(new ImageRequest("http://p2.music.126.net/IfEkDyu9LjSXYnOp90eP5g==/109951164767350572.jpg", new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    remoteViews.setImageViewBitmap(R.id.notification_cover, response);
                    remoteViews.setImageViewResource(R.id.notification_play, R.drawable.ic_play_arrow_red_36dp);
                    PendingIntent preIntent = PendingIntent.getBroadcast(RoomManager.getContext(), PENDING_REQUEST_CODE_MUSIC, new Intent(NetEaseReceiver.NOTIFICATION_ITEM_BUTTON_PRE), PendingIntent.FLAG_UPDATE_CURRENT);
                    PendingIntent playIntent = PendingIntent.getBroadcast(RoomManager.getContext(), PENDING_REQUEST_CODE_MUSIC, new Intent(NetEaseReceiver.NOTIFICATION_ITEM_BUTTON_PLAY), PendingIntent.FLAG_UPDATE_CURRENT);
                    PendingIntent nextIntent = PendingIntent.getBroadcast(RoomManager.getContext(), PENDING_REQUEST_CODE_MUSIC, new Intent(NetEaseReceiver.NOTIFICATION_ITEM_BUTTON_NEXT), PendingIntent.FLAG_UPDATE_CURRENT);
                    remoteViews.setOnClickPendingIntent(R.id.notification_pre, preIntent);
                    remoteViews.setOnClickPendingIntent(R.id.notification_play, playIntent);
                    remoteViews.setOnClickPendingIntent(R.id.notification_next, nextIntent);

                    instance = new NotificationCompat.Builder(RoomManager.getContext(), RoomManager.getContext().getPackageName())
//                .setContentTitle("通知1") // 创建通知的标题
//                .setContentText("这是第一个通知") // 创建通知的内容
//                .setSmallIcon(R.drawable.ic_launcher_background) // 创建通知的小图标
//                .setLargeIcon(BitmapFactory.decodeResource(RoomManager.getContext().getResources(), R.drawable.ic_launcher_background))
                            .setContent(remoteViews)
                            .setSmallIcon(R.drawable.ic_toys_green_24dp)
                            .setTicker("有一个通知")
                            .setOngoing(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .build(); // 创建通知（每个通知必须要调用这个方法来创建）
                    notificationManager.notify(NOTIFICATION_REQUEST_CODE_MUSIC, instance);
                }
            }, px, px, ImageView.ScaleType.FIT_XY, Bitmap.Config.RGB_565, null));
        }
    }

    public static void update(final String resCover, final int resPlay, boolean reload) {
        int px = ScreenSizeUtils.dip2px(60);
        if (instance != null) {
            if (reload) {
                VolleySingleton.getInstance(null).add(new ImageRequest(resCover, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        instance.contentView.setImageViewBitmap(R.id.notification_cover, response);
                        instance.contentView.setTextViewText(R.id.notification_name, SongListViewModel.getMusicInfo().getValue().getName());
                        instance.contentView.setTextViewText(R.id.notification_singer, SongListViewModel.getMusicInfo().getValue().getArtists().get(0).getName());
                        instance.contentView.setImageViewResource(R.id.notification_play, resPlay);
                        notificationManager.notify(NOTIFICATION_REQUEST_CODE_MUSIC, instance);
                    }
                }, px, px, ImageView.ScaleType.FIT_XY, Bitmap.Config.RGB_565, null));
            } else {
                instance.contentView.setImageViewResource(R.id.notification_play, resPlay);
                notificationManager.notify(NOTIFICATION_REQUEST_CODE_MUSIC, instance);
            }
        }
    }
}
