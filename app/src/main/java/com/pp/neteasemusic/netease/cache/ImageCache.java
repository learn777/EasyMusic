package com.pp.neteasemusic.netease.cache;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jakewharton.disklrucache.DiskLruCache;
import com.pp.neteasemusic.netease.room.RoomManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ImageCache {
    private static DiskLruCache imageCache = null;

    static {
        try {
            File cache = new File(RoomManager.getContext().getCacheDir(), "image");
            int appVersion = RoomManager.getContext().getPackageManager().getPackageInfo(RoomManager.getContext().getPackageName(), 0).versionCode;
            imageCache = DiskLruCache.open(cache, appVersion, 1, 1024 * 1024 * 20);
        } catch (IOException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Bitmap loadImageCache(String key) {
        Bitmap bitmap = null;
        try {
            DiskLruCache.Snapshot snapshot = imageCache.get(key);
            if (snapshot != null) {
                InputStream in = snapshot.getInputStream(0);
                bitmap = BitmapFactory.decodeStream(in);
                in.close();
                System.out.println("------------>loadImageCache<------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static synchronized void saveImageCache(String key, Bitmap bitmap) {
        try {
            DiskLruCache.Editor editor = imageCache.edit(key);
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                editor.commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
