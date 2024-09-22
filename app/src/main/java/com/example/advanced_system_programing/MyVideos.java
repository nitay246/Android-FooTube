package com.example.advanced_system_programing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.auth0.android.jwt.JWT;
import com.google.gson.JsonObject;

import api.UserAPI;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyVideos extends AppCompatActivity {
    private VideoAdapter videoAdapter;
    private RecyclerView recyclerView;
    private ImageButton backButton;
    private VideosViewModel videosViewModel;
    private SharedPreferences sharedPreferences;
    private TextView userDisplayNameTextView;
    private TextView userNameTextView;
    private ImageView userImageView;
    private String userID;
    private User userPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_videos);
        userID = getIntent().getStringExtra("userID");
        sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        initializeUI();
        setupRecyclerView();
        setupListeners();
        fetchUserData(); // Fetch user data first
    }

    private void initializeUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Button editUser = findViewById(R.id.editUser);
        Button deleteUser = findViewById(R.id.deleteUser);
        userDisplayNameTextView = findViewById(R.id.display_name);
        userNameTextView = findViewById(R.id.user_name);
        backButton = findViewById(R.id.imageButton);
        userImageView = findViewById(R.id.imageView6);

        // Check for token and user identity
        String token = sharedPreferences.getString("token", null);
        if (token == null) {
            // No token, user not logged in
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            editUser.setVisibility(Button.GONE);
            deleteUser.setVisibility(Button.GONE);
        } else {
            // Token exists, check if the user is the same
            JWT jwt = new JWT(token);
            String userId = jwt.getClaim("id").asString();
            if (!userId.equals(userID)) {
                // User ID from token does not match the userID of this page
                editUser.setVisibility(Button.GONE);
                deleteUser.setVisibility(Button.GONE);
            }

            editUser.setOnClickListener(v -> {
                Intent editUserIntent = new Intent(MyVideos.this, Edit_user.class);
                startActivity(editUserIntent);
            });

            deleteUser.setOnClickListener(v -> showDeleteConfirmationDialog());
        }

        setupRecyclerView();
        setupListeners();
        fetchUserData(); // Fetch user data
    }


    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete the user?")
                .setPositiveButton("Yes", (dialog, which) -> deleteUser())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteUser() {
        String token = sharedPreferences.getString("token", null);
        UserAPI userAPI = new UserAPI();
        assert token != null;

        userAPI.deleteUser(token, userID).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("token");
                    editor.apply();
                    Intent intent = new Intent(MyVideos.this, HomepageActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MyVideos.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("MyVideos", "Failed to delete user", t);
                Toast.makeText(MyVideos.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.myVideosRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);
    }

    private void fetchVideos(User user) {
        Log.d("MyVideos", "Fetching videos for user: " + user.get_id());
        videosViewModel.getVideosForUser(user.get_id()).observe(this, videos -> {
            Log.d("Videos", "Observed videos: " + videos);
            // Update the adapter when the video list changes
            if (videoAdapter == null) {
                Log.d("Videos", "Initializing VideoAdapter with videos: " + videos);
                videoAdapter = new VideoAdapter(videos, this);
                recyclerView.setAdapter(videoAdapter);
            } else {
                Log.d("HomepageActivity", "Updating VideoAdapter with videos: " + videos);
                videoAdapter.setVideos(videos);
                videoAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> onBackPressed());

    }

    private void fetchUserData() {
        UserAPI userAPI = new UserAPI();
        userAPI.getUserProfile(userID).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject userData = response.body();
                    userPage = new User(
                            userData.get("_id").getAsString(),
                            userData.get("username").getAsString(),
                            userData.get("displayname").getAsString(),
                            userData.get("password").getAsString(),
                            userData.get("img").getAsString()
                    );
                    userDisplayNameTextView.setText(userPage.getDisplayName());
                    userNameTextView.setText(userPage.getUsername());

                    // Decode Base64 image and set it to ImageView
                    byte[] decodedString = Base64.decode(userPage.getProfilePicBase64(), Base64.DEFAULT);
                    userImageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));

                    // Now that we have the user data, fetch the videos
                    fetchVideos(userPage);
                } else {
                    Toast.makeText(MyVideos.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(MyVideos.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

