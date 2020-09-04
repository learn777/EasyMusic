package com.pp.neteasemusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.pp.neteasemusic.databinding.ActivityMainBinding;
import com.pp.neteasemusic.netease.receiver.NetEaseReceiver;
import com.pp.neteasemusic.netease.room.RoomManager;
import com.pp.neteasemusic.netease.service.MusicService;
import com.pp.utils.StatusBarUtils;

public class MainActivity extends AppCompatActivity {
    NavController controller;
    AppBarConfiguration configuration;
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
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        StatusBarUtils.setBarTranslucent(this, true);
        StatusBarUtils.setStatusBarFontColor(this, false);
        setContentView(binding.getRoot());

        RoomManager.initRoomManager(getApplicationContext());
        RoomManager.setDisplay(getWindowManager().getDefaultDisplay());
        //注册音乐管理服务
        Intent intent = new Intent("com.pp.neteasemusic.MusicService");
        intent.setComponent(new ComponentName(getApplicationContext(), MusicService.class));
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        //注册通知服务
        receiver = new NetEaseReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(NetEaseReceiver.NOTIFICATION_ITEM_BUTTON_PRE);
        intentFilter.addAction(NetEaseReceiver.NOTIFICATION_ITEM_BUTTON_PLAY);
        intentFilter.addAction(NetEaseReceiver.NOTIFICATION_ITEM_BUTTON_NEXT);
        registerReceiver(receiver, intentFilter);

        controller = Navigation.findNavController(this, R.id.fragment);
        NavigationView navigationView = findViewById(R.id.nav_view);
        configuration = new AppBarConfiguration.Builder(navigationView.getMenu()).build();
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        NavigationUI.setupActionBarWithNavController(this,controller,configuration);
        NavigationUI.setupWithNavController(navigationView, controller);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        unbindService(connection);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            super.onBackPressed();
        }
    }
}