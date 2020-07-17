package com.pp.neteasemusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.pp.neteasemusic.netease.notification.NeteaseNotification;
import com.pp.neteasemusic.netease.receiver.NetEaseReceiver;
import com.pp.neteasemusic.netease.room.RoomManager;
import com.pp.neteasemusic.netease.service.MusicService;

public class MainActivity extends AppCompatActivity {
    IMusicController musicController;
    NetEaseReceiver receiver;
    IntentFilter intentFilter;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicController = IMusicController.Stub.asInterface(service);
            RoomManager.setMusicController(musicController);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent("com.pp.neteasemusic.MusicService");
        intent.setComponent(new ComponentName(getApplicationContext(), MusicService.class));
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        RoomManager.initRoomManager(getApplicationContext());
        RoomManager.setViewPager(viewPager);
        RoomManager.setDisplay(getWindowManager().getDefaultDisplay());

        NeteaseNotification.initInstance();
        receiver = new NetEaseReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(NetEaseReceiver.NOTIFICATION_ITEM_BUTTON_PRE);
        intentFilter.addAction(NetEaseReceiver.NOTIFICATION_ITEM_BUTTON_PLAY);
        intentFilter.addAction(NetEaseReceiver.NOTIFICATION_ITEM_BUTTON_NEXT);
        registerReceiver(receiver, intentFilter);

        int height = 0;
        int resourceId = getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }
        System.out.println("Status bar height-----------");
        System.out.println(height);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        unbindService(connection);
        super.onDestroy();
    }
}