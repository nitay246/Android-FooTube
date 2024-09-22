package com.example.advanced_system_programing;

import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class test_watch extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch);

        // Initialize VideoView
        videoView = findViewById(R.id.videoView2);

        // Set video path
        videoView.setVideoPath("https://www.taxmann.com/emailer/images/CompaniesAct.mp4");

        // Create and set MediaController
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Start video playback
        videoView.start();
    }
}
