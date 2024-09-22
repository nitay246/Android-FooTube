package com.example.advanced_system_programing;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;

import java.util.List;

import api.UserAPI;
import api.VideoAPI;
import okhttp3.ResponseBody;
import repository.VideoRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideosViewModel extends AndroidViewModel {

    private VideoRepository repository;

    private LiveData<List<Video>> videoList;

    private MutableLiveData<List<Video>> videoListForUser;



    public VideosViewModel(@NonNull Application application) {
        super(application);
        repository = new VideoRepository(application);
        videoList = repository.getAllVideos();
        videoListForUser = new MutableLiveData<>();

    }


    public LiveData<List<Video>> getVideos() {

        Log.d("VideosViewModel", "Retrieving videos from Room");
        return videoList;
    }

    public void refreshVideosIfNeeded() {
        // Check if Room already has videos
        videoList.observeForever(videos -> {
            if (videos == null || videos.isEmpty()) {
                // If Room is empty, fetch videos from the server
                repository.fetchVideosFromServer();
            }
            // If videos exist in Room, do nothing (avoid re-fetching)
        });
    }

    public LiveData<List<Video>> getVideosForUser(String userId) {
        // First, observe the videos stored in Room for this user
        LiveData<List<Video>> localVideos = repository.getVideosByUserId(userId);
        localVideos.observeForever(videos -> {
            if (videos != null && !videos.isEmpty()) {

                videoListForUser.setValue(videos);
                Log.d("VideosViewModel", "Retrieved videos from Room for user " + videoListForUser);
                Log.d("VideosViewModel",videos.toString());
                Log.d("VideosViewModel", "Retrieved videos from Room for user " + videoListForUser);
            } else {
                // If no local videos found, fetch from server
                repository.fetchVideosForUser(userId);
            }
        });
        repository.fetchVideosForUser(userId);
        return videoListForUser;
    }

    public void updateVideo(String token,String userId, String videoId, JsonObject edtiedVideo) {
        repository.updateVideoOnServer(token,userId,videoId,edtiedVideo);
    }

    public void deleteVideo(String token,String userId,String videoId) {
        repository.deleteVideoFromServer(token,userId,videoId);
    }

    public void uploadVideo(String videoTitle, String videoDescription, Uri videoUri, String jwtToken) {
        repository.uploadVideoToServer(videoTitle,videoDescription,videoUri,jwtToken);
    }


    public void clearAllVideos() {
        repository.clearAllVideos();
    }







}

