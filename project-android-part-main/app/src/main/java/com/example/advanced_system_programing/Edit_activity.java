package com.example.advanced_system_programing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Edit_activity extends AppCompatActivity {

    private EditText videoNameEditText; // EditText for video name
    private EditText descriptionEditText; // EditText for video description

    private Video video; // Video object to be edited
    private user user; // User object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Receive user object from Watch activity
        user = getIntent().getParcelableExtra("user");

        // Ensure user object is not null before using it
        if (user == null) {
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

        // Get instance of the singleton Video list
        videoListSingleton V_list = videoListSingleton.getInstance(this);

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
            V_list.removeVideo(video); // Remove video from the list
            Intent addVideoIntent = new Intent(Edit_activity.this, HomepageActivity.class); // Create intent to navigate to Homepage
            addVideoIntent.putExtra("user", user); // Pass user object to the intent
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

            // Update video details
            video.setTitle(title);
            video.setDescription(description);

            // Set result to indicate video is edited
            Intent resultIntent = new Intent();
            resultIntent.putExtra("video", video);
            setResult(RESULT_OK, resultIntent);
            finish(); // Finish current activity
        });

        // Listen for back button click
        backButton.setOnClickListener(v -> finish()); // Finish current activity
    }
}