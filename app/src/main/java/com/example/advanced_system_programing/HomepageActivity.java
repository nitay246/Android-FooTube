package com.example.advanced_system_programing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.jwt.JWT;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import api.TokenValidator;
import api.UserAPI;

public class HomepageActivity extends AppCompatActivity {

    private User user; // User object representing the logged-in user
    private RecyclerView recyclerView; // RecyclerView to display the list of videos
    private VideoAdapter videoAdapter; // Adapter for the RecyclerView
    private boolean isRecreating = false; // Flag to check if the activity is being recreated
    private UserAPI userAPI; // UserAPI instance for network requests

    private VideosViewModel videosViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        // Initialize UserAPI
        userAPI = new UserAPI();

        // Retrieve the token from shared preferences
        String token = getTokenFromSharedPreferences();

        if (token != null && TokenValidator.isTokenValid(token)) {
            String userId = TokenValidator.decodeTokenAndGetUserId(token);
            userAPI.getUserProfile(userId).enqueue(new retrofit2.Callback<JsonObject>() {
                @Override
                public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject userJson = response.body();
                        user = new User(
                                userJson.get("_id").getAsString(),
                                userJson.get("username").getAsString(),
                                userJson.get("displayname").getAsString(),
                                userJson.get("password").getAsString(),
                                userJson.get("img").getAsString()
                        );
                        initializeUI(user, preferences, isNightMode);
                    } else {
                        Log.d("HomepageActivity", "User retrieved unsuccessfully");
                        initializeUI(null, preferences, isNightMode);
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                    initializeUI(null, preferences, isNightMode);
                }
            });
        } else {
            initializeUI(null, preferences, isNightMode);
        }
    }

    private void initializeUI(@Nullable User user, SharedPreferences preferences, boolean isNightMode) {
        if (user == null) {
            // Set layout for non-logged-in users
            setContentView(R.layout.home_page_no_user);
            // Find the buttons for sign-up and sign-in
            ImageButton signUpButton = findViewById(R.id.sign_up_here);
            ImageButton signInButton = findViewById(R.id.login_here);

            // Set onClickListeners for the buttons
            signUpButton.setOnClickListener(v -> {
                Intent signUpIntent = new Intent(HomepageActivity.this, Signup.class);
                startActivity(signUpIntent);
            });

            signInButton.setOnClickListener(v -> {
                Intent signInIntent = new Intent(HomepageActivity.this, Signin.class);
                startActivity(signInIntent);
            });
        } else {

            // Set layout for logged-in users
            setContentView(R.layout.home_page_for_user);

            // Find the logout button, add video button, and user profile ImageView
            FloatingActionButton logoutButton = findViewById(R.id.logout_here);
            FloatingActionButton addVideo = findViewById(R.id.fab_upload_video);
            ImageView userProfileImageView = findViewById(R.id.user_image);
            TextView usernameTextView = findViewById(R.id.user_name);
            usernameTextView.setText("hello " + user.getDisplayName() + "!");

            FloatingActionButton userProfile = findViewById(R.id.user_profile);

            // Set the user's profile picture in the ImageView using Base64
            if (user.getProfilePicBase64() != null && !user.getProfilePicBase64().isEmpty()) {
                byte[] decodedString = Base64.decode(user.getProfilePicBase64(), Base64.DEFAULT);
                userProfileImageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
            }

            // Set onClickListener for the logout button
            logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());

            // Set onClickListener for the add video button
            addVideo.setOnClickListener(v -> {
                Intent addVideoIntent = new Intent(HomepageActivity.this, upload_activity.class);
                addVideoIntent.putExtra("jwtToken", getTokenFromSharedPreferences());
                startActivity(addVideoIntent);
            });

            userProfile.setOnClickListener(v -> {
                Intent userProfileIntent = new Intent(HomepageActivity.this, MyVideos.class);
                userProfileIntent.putExtra("userID", user.get_id());
                startActivity(userProfileIntent);
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
        videoAdapter = new VideoAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(videoAdapter);
        videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);

        // Observe the LiveData from the ViewModel

        videosViewModel.getVideos().observe(this, new Observer<List<Video>>() {
                @Override
                public void onChanged(@Nullable List<Video> videos) {
                    Log.d("HomepageActivity", "Observed videos: " + videos);
                    // Update the UI (e.g., update the adapter with the new list of videos)
                    videoAdapter.setVideos(videos);
                }
            });
        // Optionally, fetch videos from the server when the activity is created
        videosViewModel.refreshVideosIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRecreating = false; // Reset the recreating flag
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
        userAPI.disconnectUser(getTokenFromSharedPreferences(),user.get_id()).enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.d("HomepageActivity", "User disconnected successfully");
                } else {
                    Log.d("HomepageActivity", "User disconnection failed");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                Log.e("HomepageActivity", "Failed to disconnect user", t);
            }
        });
        // Clear the token from shared preferences
        clearTokenFromSharedPreferences();
        Intent intent = new Intent(HomepageActivity.this, HomepageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // Method to retrieve the token from shared preferences
    private String getTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }

    // Method to clear the token from shared preferences
    private void clearTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("token");
        editor.apply();
    }
}
