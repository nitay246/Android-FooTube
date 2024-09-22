package com.example.advanced_system_programing;



import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import java.io.IOException;

public class upload_activity extends AppCompatActivity {
    private static final int PICK_VIDEO_REQUEST = 1;
    private EditText title;
    private EditText description;
    private Uri videoUri;
    private SharedPreferences sharedPreferences;

    private String jwtToken; // Add JWT token field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.upload_page);

        // Retrieve JWT token and user object from intent
        Intent intent = getIntent();
        jwtToken = intent.getStringExtra("jwtToken");

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
            } else {
                try {


                    uploadVideoToServer(videoTitle, videoDescription, videoUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Set onClickListener for the home button
        homeButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(upload_activity.this, HomepageActivity.class);
            homeIntent.putExtra("jwtToken", jwtToken); // Pass JWT token back to HomepageActivity
            startActivity(homeIntent);
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


    // Method to upload video to server
    private void uploadVideoToServer (String videoTitle, String videoDescription, Uri videoUri) throws IOException {
        //   extractVideoThumbnail(videoUri);
        Log.d("upload_activity", "Videouri is: " + videoUri);
        VideosViewModel videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        videosViewModel.uploadVideo(videoTitle, videoDescription, videoUri, jwtToken);
        Toast.makeText(upload_activity.this, "Upload successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(upload_activity.this, HomepageActivity.class);
        intent.putExtra("jwtToken", jwtToken); // Pass JWT token back to HomepageActivity
        startActivity(intent);

    }
}

