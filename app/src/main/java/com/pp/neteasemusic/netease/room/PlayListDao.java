package com.pp.neteasemusic.netease.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlayListDao {
    @Insert
    void insert(PlayList... lists);

    @Delete
    void delete(PlayList... lists);

    @Update
    void update(PlayList... lists);

    @Query("SELECT * FROM PLAYLIST ORDER BY id ASC")
    List<PlayList> getAll();

    @Query("SELECT * FROM PLAYLIST WHERE playID = :exist")
    List<PlayList> getExist(String exist);
}
