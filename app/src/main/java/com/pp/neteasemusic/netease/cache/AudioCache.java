package com.pp.neteasemusic.netease.cache;

import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.jakewharton.disklrucache.DiskLruCache;
import com.pp.neteasemusic.netease.room.RoomManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AudioCache {
    private static DiskLruCache audioCache = null;
    //    private static SaveAudioCache saveTask = null;
    private static Thread saveTask = null;

    static {
        try {
            File cache = new File(RoomManager.getContext().getCacheDir(), "music");
            int appVersion = RoomManager.getContext().getPackageManager().getPackageInfo(RoomManager.getContext().getPackageName(), 0).versionCode;
            audioCache = DiskLruCache.open(cache, appVersion, 1, 500 * 1024 * 1024);
        } catch (PackageManager.NameNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void setSize(int size) {
        if (audioCache != null) {
            audioCache.setMaxSize(size * 1024 * 1024);
        }
    }

    public static String loadAudioCache(String key) {
        String path = null;
        //加载缓存
        try {
            DiskLruCache.Snapshot snapshot = audioCache.get(key);
            if (snapshot != null) {
                path = audioCache.getDirectory().getAbsolutePath() + "/" + key + ".0";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    //    public static void saveAudioCache(String key, String url) {
//        //取消未完成的缓存任务
//        if (saveTask != null && saveTask.getStatus() == AsyncTask.Status.RUNNING) {
//            saveTask.cancel(true);
//            saveTask = null;
//        }
//        saveTask = new SaveAudioCache();
//        saveTask.execute(key, url);
//    }
    public static void saveAudioCache(final String key, final String uri) {
        //取消未完成的缓存任务
        if (saveTask != null && saveTask.getState() == Thread.State.BLOCKED) {
            saveTask.interrupt();
            saveTask = null;
        }
        saveTask = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream ins;
                OutputStream ous;
                DiskLruCache.Editor editor;
                try {
                    URL url = new URL(uri);
                    editor = audioCache.edit(key);
                    if (editor != null) {
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        if (connection.getContentType() == null) {
                            return;
                        }
                        ins = connection.getInputStream();
                        ous = editor.newOutputStream(0);
                        int read;
                        int count = 0;
                        byte[] buf = new byte[8 * 1024];
                        while ((read = ins.read(buf)) != -1) {
                            count += read;
                            ous.write(buf, 0, read);
                            System.out.println(count);
                        }
                        ous.flush();
                        ous.close();
                        ins.close();
                        editor.commit();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        saveTask.start();
    }


    static class SaveAudioCache extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            InputStream ins;
            OutputStream ous;
            DiskLruCache.Editor editor;
            try {
                URL url = new URL(strings[1]);
                editor = audioCache.edit(strings[0]);
                if (editor != null) {
                    ins = url.openConnection().getInputStream();
                    ous = editor.newOutputStream(0);
                    int read;
                    int count = 0;
                    byte[] buf = new byte[8 * 1024];
                    while (!isCancelled() && (read = ins.read(buf)) != -1) {
                        count += read;
                        ous.write(buf, 0, read);
                        System.out.println(count);
                    }
                    ous.flush();
                    ous.close();
                    ins.close();
                    if (isCancelled()) {
                        editor.abort();
                    } else {
                        editor.commit();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
