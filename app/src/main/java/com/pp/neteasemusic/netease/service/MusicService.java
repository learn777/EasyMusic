package com.pp.neteasemusic.netease.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.pp.neteasemusic.IMusicController;
import com.pp.neteasemusic.netease.utils.OperationFrom;
import com.pp.neteasemusic.netease.utils.ToastUtils;
import com.pp.neteasemusic.ui.songlist.SongListViewModel;

import java.io.IOException;

public class MusicService extends Service {
    MediaPlayer mediaPlayer;
    String playNow = "";
    IMusicController.Stub iBinder = new IMusicController.Stub() {

        @Override
        public boolean play(String id) {
            boolean flag = false;
            String uri = "http://music.163.com/song/media/outer/url?id=" + id + ".mp3";
            System.out.println(uri);
            if (uri.equals(playNow)) {
                flag = pause();
            } else {
                try {
                    playNow = uri;
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(uri);
                    mediaPlayer.prepareAsync();
                    flag = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return flag;
        }

        @Override
        public boolean pause() {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                return false;
            } else {
                mediaPlayer.start();
                return true;
            }
        }

        @Override
        public void next(String url) {

        }

        @Override
        public void previous(String url) {

        }

        @Override
        public void progress(int duration) {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(duration);
            }
        }

        @Override
        public int getCurrentTime() {
            return mediaPlayer.getCurrentPosition();
        }

        @Override
        public boolean isPlaying() {
            return mediaPlayer.isPlaying();
        }

    };

    public MusicService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(false);
                mp.seekTo(0);
                mp.start();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mediaPlayer.reset();
                ToastUtils.ToastNoAppName("当前歌曲无法播放，自动播放下一首").show();
                SongListViewModel.pressNext(OperationFrom.MUSIC_SERVICE);
                return true;
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNow = "";
                if (SongListViewModel.getSongsList().getValue() != null) {
                    SongListViewModel.pressNext(OperationFrom.MUSIC_SERVICE);
                } else {
                    SongListViewModel.pressPlay(OperationFrom.MUSIC_SERVICE);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        mediaPlayer.reset();
        mediaPlayer.release();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Nullable

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }
}
