package com.example.advanced_system_programing;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class testing_watch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a Video object with the given URL and details
        Video video = null;
        try {
            video = new Video(
                    "Companies Act Video",
                    "A detailed overview of the Companies Act.",
                    "https://www.taxmann.com/emailer/images/CompaniesAct.mp4",
                    100,
                    10,
                    5,
                    this
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Start the watch activity with the Video object
        Intent intent = new Intent(this, Watch.class);
        intent.putExtra("video", video);
        startActivity(intent);
    }
}