package com.example.advanced_system_programing;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class videoListSingleton {

    private static videoListSingleton instance;
    private List<Video> videoList;

    // Private constructor to prevent instantiation outside of getInstance method
    private videoListSingleton() {
        videoList = new ArrayList<>();
    }

    // Singleton instance getter with initialization
    public static synchronized videoListSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new videoListSingleton();
            instance.initialize(context);
        }
        return instance;
    }

    // Getter for the list of videos
    public List<Video> getVideoList() {
        return videoList;
    }

    // Load videos from a JSON file stored in resources
    public void loadVideosFromJson(Context context, int resourceId) {
        try {
            // Read JSON file from resources
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            inputStream.close();

            // Parse JSON string to JSONObject and retrieve videos array
            String json = stringBuilder.toString();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray videosArray = jsonObject.getJSONArray("videos");

            // Iterate through JSON array to create Video objects and add them to videoList
            for (int i = 0; i < videosArray.length(); i++) {
                JSONObject videoObject = videosArray.getJSONObject(i);
                String title = videoObject.getString("title");
                String description = videoObject.getString("description");
                String videoUrl = videoObject.getString("video");
                int likes = videoObject.getInt("likes");
                int dislikes = videoObject.getInt("dislikes");
                int views = videoObject.getInt("views");

                // Create Video object and add to videoList
                Video video = new Video(title, description, videoUrl, likes, dislikes, views, context);
                videoList.add(video);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    // Update an existing video in the list
    public void updateVideo(Video updatedVideo) {
        for (int i = 0; i < videoList.size(); i++) {
            Video video = videoList.get(i);
            if (video.getVideoUrl().equals(updatedVideo.getVideoUrl())) {
                // Update video properties
                video.setTitle(updatedVideo.getTitle());
                video.setDescription(updatedVideo.getDescription());
                video.setVideoUrl(updatedVideo.getVideoUrl());
                video.setLikes(updatedVideo.getLikes());
                video.setDislikes(updatedVideo.getDislikes());
                video.setComments(updatedVideo.getComments());
                break;
            }
        }
    }

    // Add a new video to the list
    public void addVideo(Video video) {
        videoList.add(video);
    }

    // Remove a video from the list
    public void removeVideo(Video video) {
        videoList.remove(video);
    }

    // Initialize the video list by loading videos from JSON resource
    public void initialize(Context context) {
        loadVideosFromJson(context, R.raw.videos);
    }

    // Check if a video with given URL exists in the list
    public boolean videoExists(String videoUrl) {
        for (Video video : videoList) {
            if (video.getVideoUrl().equals(videoUrl)) {
                return true;
            }
        }
        return false;
    }

    // Retrieve a video from the list by its title
    public Video getVideoByTitle(String title) {
        for (Video video : videoList) {
            if (video.getTitle().equalsIgnoreCase(title)) {
                return video;
            }
        }
        return null;
    }
}
