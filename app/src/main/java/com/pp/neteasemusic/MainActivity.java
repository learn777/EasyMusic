package com.pp.neteasemusic;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.pp.neteasemusic.databinding.ActivityMainBinding;
import com.pp.neteasemusic.netease.receiver.NetEaseReceiver;
import com.pp.neteasemusic.netease.room.RoomManager;
import com.pp.neteasemusic.netease.service.MusicService;
import com.pp.neteasemusic.netease.utils.AudioManager;
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
        getApplication().onLowMemory();
        RoomManager.initRoomManager(this);
        RoomManager.setDisplay(getResources().getDisplayMetrics());
        AudioManager.getHasFocus().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                System.out.println("MainActivity.onChanged");
                try {
                    if (!aBoolean && RoomManager.getMusicController() != null && RoomManager.getMusicController().isPlaying()) {
                        sendBroadcast(new Intent(NetEaseReceiver.NOTIFICATION_ITEM_BUTTON_PLAY));
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
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
        requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
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
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            super.onBackPressed();
        }
    }

    void requestPermission(String[] permission) {
        ActivityCompat.requestPermissions(this, permission, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                System.out.println(permissions[i] + "is GRANTED");
            } else {
                System.out.println(permissions[i] + "is DEFINED");
            }
        }
    }
}