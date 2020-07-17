package com.pp.neteasemusic.netease.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = PlayList.class, version = 1, exportSchema = false)
public abstract class PlayListDatabase extends RoomDatabase {
    private static PlayListDatabase database;

    synchronized static PlayListDatabase getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context, PlayListDatabase.class, "playlist")
                    .allowMainThreadQueries()
                    .build();
        }
        return database;
    }

    public abstract PlayListDao getPlayListDao();
}
