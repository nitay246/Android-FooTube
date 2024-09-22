package com.example.advanced_system_programing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class upload_activity extends AppCompatActivity {
    private static final int PICK_VIDEO_REQUEST = 1;
    private EditText title;
    private EditText description;
    private Uri videoUri;
    private Bitmap videoThumbnail;

    private user user; // Add user field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.upload_page);
        user = getIntent().getParcelableExtra("user"); // Retrieve user object from intent
        videoListSingleton V_list = videoListSingleton.getInstance(this);
        if (V_list == null) {
            V_list.initialize(this);
        }

        Button upload = findViewById(R.id.upload_button);
        Button homeButton = findViewById(R.id.go_home); // Add reference to home button
        Button uploadVideoButton = findViewById(R.id.upload_video_button); // Button to upload video
        title = findViewById(R.id.video_name);
        description = findViewById(R.id.description);

        upload.setOnClickListener(v -> {
            // Get the text from EditText fields and trim them
            String videoTitle = title.getText().toString().trim();
            String videoDescription = description.getText().toString().trim();

            // Check if any of the fields are empty
            if (videoTitle.isEmpty() || videoDescription.isEmpty() || videoUri == null) {
                // Display a toast message
                Toast.makeText(upload_activity.this, "Please fill all fields and upload a video", Toast.LENGTH_SHORT).show();
            } else if (V_list.videoExists(videoUri.toString())) {
                // Check if video already exists
                Toast.makeText(upload_activity.this, "Video already exists", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    extractVideoThumbnail(videoUri);
                    Video video = new Video(videoTitle, videoDescription, videoUri.toString(),
                            0, 0, 0, videoThumbnail, this);
                    V_list.addVideo(video);
                    Toast.makeText(upload_activity.this, "Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(upload_activity.this, HomepageActivity.class);
                    intent.putExtra("user", user); // Pass user object back to HomepageActivity
                    startActivity(intent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Set onClickListener for the home button
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(upload_activity.this, HomepageActivity.class);
            startActivity(intent);
        });

        // Set onClickListener for the upload video button
        uploadVideoButton.setOnClickListener(v -> pickVideo());
    }

    // Method to pick a video
    private void pickVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    // Method to handle the result of picking a video
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            Toast.makeText(this, "Video selected: " + videoUri.getPath(), Toast.LENGTH_SHORT).show();
        }
    }

    // Method to extract the first frame of the video as a thumbnail
    private void extractVideoThumbnail(Uri videoUri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this, videoUri);
            videoThumbnail = retriever.getFrameAtTime(0);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
    }
}
