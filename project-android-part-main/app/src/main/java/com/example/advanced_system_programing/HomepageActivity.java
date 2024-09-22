package com.example.advanced_system_programing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomepageActivity extends AppCompatActivity {

    private user user; // User object representing the logged-in user
    private RecyclerView recyclerView; // RecyclerView to display the list of videos
    private VideoAdapter videoAdapter; // Adapter for the RecyclerView
    private static final int WATCH_REQUEST_CODE = 1; // Request code for the Watch activity
    private videoListSingleton videoList; // Singleton instance for the list of videos
    private boolean isRecreating = false; // Flag to check if the activity is being recreated

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Check if night mode preference exists, if not, set default to light mode
        if (!preferences.contains("night_mode")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("night_mode", false);
            editor.apply();
        }

        // Set night mode based on preference
        boolean isNightMode = preferences.getBoolean("night_mode", false);
        AppCompatDelegate.setDefaultNightMode(isNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);

        // Retrieve the user object from the Intent
        user = getIntent().getParcelableExtra("user");

        if (user == null) {
            // Set layout for non-logged-in users
            setContentView(R.layout.home_page_no_user);
            // Find the buttons for sign-up and sign-in
            ImageButton signUpButton = findViewById(R.id.sign_up_here);
            ImageButton signInButton = findViewById(R.id.login_here);

            // Set onClickListeners for the buttons
            signUpButton.setOnClickListener(v -> {
                Intent signUpIntent = new Intent(HomepageActivity.this, signup.class);
                startActivity(signUpIntent);
            });

            signInButton.setOnClickListener(v -> {
                Intent signInIntent = new Intent(HomepageActivity.this, Signin.class);
                startActivity(signInIntent);
            });
        } else {
            // Set layout for logged-in users
            setContentView(R.layout.home_page_for_user);

            // Find the logout button, add video button, and user name TextView
            ImageButton logoutButton = findViewById(R.id.logout_here);
            ImageButton addVideoButton = findViewById(R.id.add_video);
            TextView userNameTextView = findViewById(R.id.user_name);

            // Set the user's name in the TextView
            userNameTextView.setText("hello " + user.getDisplayName() + "!");

            // Set onClickListener for the logout button
            logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());

            // Set onClickListener for the add video button
            addVideoButton.setOnClickListener(v -> {
                Intent addVideoIntent = new Intent(HomepageActivity.this, upload_activity.class);
                addVideoIntent.putExtra("user", user);
                startActivity(addVideoIntent);
            });
        }

        // Setup the night mode switch
        Switch nightModeSwitch = findViewById(R.id.nightswitch);
        nightModeSwitch.setChecked(isNightMode); // Ensure the switch reflects the current mode
        nightModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isRecreating) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("night_mode", isChecked);
                editor.apply();

                isRecreating = true;
                AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                recreate();
            }
        });

        // Initialize RecyclerView and set its layout manager
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the instance of the video list singleton and set up the adapter
        videoList = videoListSingleton.getInstance(this);
        videoAdapter = new VideoAdapter(videoList, user, this);
        recyclerView.setAdapter(videoAdapter);

        // Handle search bar input
        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            String searchQuery = searchBar.getText().toString().trim();
            if (!searchQuery.isEmpty()) {
                Video video = videoList.getVideoByTitle(searchQuery);
                if (video != null) {
                    Intent watchIntent = new Intent(HomepageActivity.this, Watch.class);
                    watchIntent.putExtra("video", video);
                    if (user != null) {
                        watchIntent.putExtra("user", user);
                    }
                    startActivity(watchIntent);
                } else {
                    Toast.makeText(HomepageActivity.this, "No video found with this title", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRecreating = false; // Reset the recreating flag
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WATCH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Video updatedVideo = data.getParcelableExtra("video");
            if (updatedVideo != null) {
                // Update the video in the list
                videoList.updateVideo(updatedVideo);
                videoAdapter.notifyDataSetChanged(); // Notify adapter of data changes
            }
        }
    }

    // Show a confirmation dialog for logout
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logout())
                .setNegativeButton("No", null)
                .show();
    }

    // Perform logout
    private void logout() {
        Intent intent = new Intent(HomepageActivity.this, HomepageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
