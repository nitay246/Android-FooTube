package com.example.advanced_system_programing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.JsonObject;

public class Edit_activity extends AppCompatActivity {

    private EditText videoNameEditText; // EditText for video name
    private EditText descriptionEditText; // EditText for video description
    private Video video; // Video object to be edited
    private String jwtUser; // User object


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Receive user object from Watch activity
        jwtUser = getIntent().getStringExtra("jwtUser");

        // Ensure user object is not null before using it
        if (jwtUser == null) {
            // Handle the case where user information is missing
            finish(); // Finish the activity if user information is not available
            return;
        }
        setContentView(R.layout.edit_screen); // Set the layout for the activity

        // Receive video object from the intent
        video = getIntent().getParcelableExtra("video");

        // Ensure video object is not null before using it
        if (video == null) {
            finish();
            return;
        }

        // Initialize EditTexts
        videoNameEditText = findViewById(R.id.video_name);
        descriptionEditText = findViewById(R.id.description);

        // Initialize buttons
        Button saveButton = findViewById(R.id.saveBtn);
        Button backButton = findViewById(R.id.go_back); // Assuming the back button has an id go_back
        Button deleteButton = findViewById(R.id.delete_button);

        // Set current video details in EditTexts
        videoNameEditText.setText(video.getTitle());
        descriptionEditText.setText(video.getDescription());

        // Listen for delete button click
        deleteButton.setOnClickListener(v -> {
          deleteVideoFromServer(jwtUser,video.getCreator(),video.getId());
            Intent addVideoIntent = new Intent(Edit_activity.this, HomepageActivity.class); // Create intent to navigate to Homepage
            addVideoIntent.putExtra("user", jwtUser); // Pass user object to the intent
            startActivity(addVideoIntent); // Start Homepage activity
            // Set result to indicate video is deleted
            Intent resultIntent = new Intent();
            resultIntent.putExtra("video_deleted", true);
            setResult(RESULT_OK, resultIntent);
            finish(); // Finish current activity
        });

        // Listen for save button click
        saveButton.setOnClickListener(v -> {
            String title = videoNameEditText.getText().toString(); // Get updated title
            String description = descriptionEditText.getText().toString(); // Get updated description
            JsonObject editedVideo = new JsonObject();
            editedVideo.addProperty("title",title);
            editedVideo.addProperty("description",description);
            updateVideoOnServer(jwtUser,video.getCreator(),video.getId(),editedVideo);
            // Update video details
            video.setTitle(title);
            video.setDescription(description);
            // Set result to indicate video is edited
            Intent resultIntent = new Intent(Edit_activity.this, Watch.class);
            resultIntent.putExtra("userId", video.getCreator());
            resultIntent.putExtra("videoId", video.getId());
            setResult(RESULT_OK, resultIntent);
            startActivity(resultIntent);
        });

        // Listen for back button click
        backButton.setOnClickListener(v -> finish()); // Finish current activity
    }

    private void deleteVideoFromServer(String token,String userId,String videoId){
       VideosViewModel videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        videosViewModel.deleteVideo(token,userId,videoId);
    }


    private void updateVideoOnServer(String token,String userId,String videoId,JsonObject editedVideo){
        VideosViewModel videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        videosViewModel.updateVideo(token,userId,videoId,editedVideo);

    }
}