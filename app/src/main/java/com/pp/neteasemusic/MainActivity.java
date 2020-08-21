package com.pp.neteasemusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

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
        SoftReference<String> softReference = new SoftReference<>("", new ReferenceQueue<String>());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 全透明实现
            //getWindow.setStatusBarColor(Color.TRANSPARENT)
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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
}