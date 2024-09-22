package com.example.advanced_system_programing;

import android.app.Application;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import api.VideoAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WatchViewModel extends AndroidViewModel {
    private VideoAPI videoAPI;
    private MutableLiveData<Video> videoLiveData;

    public WatchViewModel(@NonNull Application application) {
        super(application);
        videoAPI = new VideoAPI();
        videoLiveData = new MutableLiveData<Video>();
    }

    public MutableLiveData<Video> getVideo() {


        return videoLiveData;
    }

    public void fetchVideoDetails(String userId, String videoId) {
        videoAPI.getVideo(userId, videoId).enqueue(new Callback<Video>() {
            @Override
            public void onResponse(@androidx.annotation.NonNull Call<Video> call, @androidx.annotation.NonNull Response<Video> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("WatchViewModel", "Video: " + response.body().toString());
                    videoLiveData.setValue(response.body());
                    Log.d("WatchViewModel", "Video after setting: " + videoLiveData.getValue().toString());
                } else {
                    Log.e("WatchViewModel", "Failed to fetch video details");
                }
            }

            @Override
            public void onFailure(@androidx.annotation.NonNull Call<Video> call, @androidx.annotation.NonNull Throwable t) {
                // Handle the failure
                Log.e("WatchViewModel", "Failed to fetch video details", t);
            }
        });
    }
}

