package com.example.advanced_system_programing;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VideoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVideo(Video video);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Video> videos);

    @Query("SELECT * FROM videos")
    LiveData<List<Video>> getAllVideos();

    @Query("SELECT * FROM videos WHERE creator = :userId")
    LiveData<List<Video>> getVideosByUserId(String userId);

    @Query("DELETE FROM videos")
    void clearAllVideos();

    @Query("UPDATE videos SET title = :title, description = :description WHERE _id = :id")
    void updateVideo(String id, String title, String description);

    @Query("DELETE FROM videos WHERE _id = :id")
    void deleteVideo(String id);
}
