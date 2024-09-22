package com.example.advanced_system_programing;



import static api.TokenValidator.decodeTokenAndGetUserId;
import static api.TokenValidator.getTokenFromSharedPreferences;

import android.annotation.SuppressLint;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.JsonObject;

import api.TokenValidator;
import api.UserAPI;
import api.VideoAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Watch extends AppCompatActivity {
    private static final int EDIT_REQUEST_CODE = 2;

    private WatchViewModel watchViewModel;
    private Video video;
    private String videoCreatorName;
    private User user;
    private boolean isVideoPlaying = false;
    private CommentAdapter commentAdapter;
    private RecyclerView commentsRecyclerView;
    private UserAPI userAPI;
    private VideoAPI videoAPI;
    private TextView username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch);

        userAPI = new UserAPI(); // Initialize UserAPI
        videoAPI = new VideoAPI(); // Initialize UserAPI

        // Retrieve the token from shared preferences
        String token = getTokenFromSharedPreferences(this);
        Log.d("Watch", "Token: " + token);
        if (token != null && TokenValidator.isTokenValid(token)) {
            String userId = decodeTokenAndGetUserId(token);
            userAPI.getUserProfile(userId).enqueue(new retrofit2.Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                    Log.d("Watch", "Response: " + response.isSuccessful());
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject userJson = response.body();
                        user = new User(
                                userJson.get("_id").getAsString(),
                                userJson.get("username").getAsString(),
                                userJson.get("displayname").getAsString(),
                                userJson.get("password").getAsString(),
                                userJson.get("img").getAsString()
                        );
                      //  initializeUI(user);
                    }
                }
                @Override
                public void onFailure(@NonNull retrofit2.Call<JsonObject> call, @NonNull Throwable t) {
                    if (video != null) {
                        initializeUI(null);
                    }
                }
            });
        }

        watchViewModel = new ViewModelProvider(this).get(WatchViewModel.class);

        // Observe the video LiveData
        watchViewModel.getVideo().observe(this, new Observer<Video>() {
            @Override
            public void onChanged(Video video) {
                if (video != null) {
                    Log.d("WatchViewModel", "Video observed: " + video.toString());
                    Watch.this.video = video;
                    Log.d("Watch","user is :"  + user);

                        initializeUI(user);

                } else {
                    Log.d("Watch", "Video is null, finishing activity");
                    finish();
                }
            }
        });
        // Restore state if exists
        if (savedInstanceState != null) {
           Log.d("Watch", "Restoring state");
            video = savedInstanceState.getParcelable("video");
            if (video != null) {
                initializeUI(user);
            }
        } else {
            String userId = getIntent().getStringExtra("userId");
            String videoId = getIntent().getStringExtra("videoId");
            Log.d("Watch", "Fetching video details for userId: " + userId + ", videoId: " + videoId);
            watchViewModel.fetchVideoDetails(userId, videoId);
        }
    }

    private void initializeUI(@Nullable User user) {
        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        username = findViewById(R.id.user_name);
        userAPI.getUserProfile(video.getCreator()).enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject userJson = response.body();
                    videoCreatorName = userJson.get("displayname").getAsString();
                    username.setText(videoCreatorName);
                }
            }
            @Override
            public void onFailure(@NonNull retrofit2.Call<JsonObject> call, @NonNull Throwable t) {
                if (video != null) {
                    initializeUI(null);
                }
            }
        });

        final VideoView videoView = findViewById(R.id.videoView2);
        TextView textViewLikes = findViewById(R.id.likesTextView);
        TextView textViewDislikes = findViewById(R.id.dislikesTextView);
        TextView viewButton = findViewById(R.id.viewsTextView);
        SeekBar seekBar = findViewById(R.id.seekBar);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch nightModeSwitch = findViewById(R.id.nightmodeswitch);
        // Handle the case where the user object is not available
        ImageButton likeButton = findViewById(R.id.likeButton);
        ImageButton dislikeButton = findViewById(R.id.dislikeButton);
        titleTextView.setText(video.getTitle());
        viewButton.setText(String.valueOf(video.getViews()));
        descriptionTextView.setText(video.getDescription());
        textViewLikes.setText(String.valueOf(video.getLikes()));
        textViewDislikes.setText(String.valueOf(video.getDislikes()));
        addView(this.video.getCreator(), this.video.getId());

        videoView.setVideoPath("http://10.0.2.2:8000/" + video.getSource());

        Button editButton = findViewById(R.id.edit);
        editButton.setVisibility(View.INVISIBLE);
        username.setOnClickListener(v -> {
            Intent MyVideosIntent = new Intent(Watch.this, MyVideos.class);
            MyVideosIntent.putExtra("flag", 1 );
            MyVideosIntent.putExtra("userID", video.getCreator());
            startActivity(MyVideosIntent);
        });

        if (user == null) {
            Log.d("Watch", "User is null");
            likeButton.setEnabled(false);
            dislikeButton.setEnabled(false);
            // Make the edit button invisible
            editButton.setVisibility(View.INVISIBLE);

        } else {
           if(user.get_id().equals(video.getCreator())){
                editButton.setVisibility(View.VISIBLE);
            }
           editButton.setOnClickListener(v -> {
                Intent editIntent = new Intent(Watch.this, Edit_activity.class);
                editIntent.putExtra("video", video);
                editIntent.putExtra("jwtUser", getTokenFromSharedPreferences(this));
               startActivity(editIntent);
            });
            likeButton.setEnabled(true);
            dislikeButton.setEnabled(true);
            likeButton.setOnClickListener(v -> addLike(getTokenFromSharedPreferences(this), video.getId(), textViewLikes, textViewDislikes, likeButton, dislikeButton));
            dislikeButton.setOnClickListener(v -> addDislike(getTokenFromSharedPreferences(this), video.getId(), textViewLikes, textViewDislikes, likeButton, dislikeButton));
            updateRecommend(user.get_id(), video.getId());
        }

        Button homeButton = findViewById(R.id.home);
        homeButton.setOnClickListener(v -> {
            Intent goHome = new Intent(Watch.this, HomepageActivity.class);
            startActivity(goHome);
        });

        videoView.start();
        isVideoPlaying = true;

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int duration = videoView.getDuration();
                    int newPosition = (duration * progress) / 1000;
                    videoView.seekTo(newPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        nightModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.black));
            } else {
                getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.background_light));
            }
        });

        videoView.setOnClickListener(view -> {
            if (isVideoPlaying) {
                videoView.pause();
                isVideoPlaying = false;
            } else {
                videoView.start();
                isVideoPlaying = true;
            }
        });

        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentAdapter = new CommentAdapter(video.getComments(), user != null ? getTokenFromSharedPreferences(this) : null, video.getId());
        commentsRecyclerView.setAdapter(commentAdapter);
    }

    private void updateRecommend(String userId, String videoId) {
        userAPI.updateRecommend(userId, videoId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Watch", "Recommend updated successfully");
                } else {
                    Log.e("Watch", "Failed to update recommend: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e("Watch", "Failed to update recommend: " + t.getMessage());
            }
        });
    }

    private void addLike(String token, String videoId, TextView textViewLikes, TextView textViewDislikes, ImageButton likeButton, ImageButton dislikeButton) {



        Call<JsonObject> call = videoAPI.addLike(token, videoId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject responseBody = response.body();
                    int likes = responseBody.get("likes").getAsInt();
                    int dislikes = responseBody.get("dislikes").getAsInt();

                    video.setLikes(likes);
                    video.setDislikes(dislikes);
                    textViewLikes.setText(String.valueOf(likes));
                    textViewDislikes.setText(String.valueOf(dislikes));

                    likeButton.setEnabled(false);
                    dislikeButton.setEnabled(true);
                } else {
                    Log.e("Watch", "Like request failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Watch", "Like request failed: " + t.getMessage());
            }
        });
    }

    private void addDislike(String token, String videoId, TextView textViewLikes, TextView textViewDislikes, ImageButton likeButton, ImageButton dislikeButton) {
        Call<JsonObject> call = videoAPI.addDislike(token, videoId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject responseBody = response.body();
                    int likes = responseBody.get("likes").getAsInt();
                    int dislikes = responseBody.get("dislikes").getAsInt();

                    video.setLikes(likes);
                    video.setDislikes(dislikes);
                    textViewLikes.setText(String.valueOf(likes));
                    textViewDislikes.setText(String.valueOf(dislikes));

                    dislikeButton.setEnabled(false);
                    likeButton.setEnabled(true);
                } else {
                    Log.e("Watch", "Dislike request failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Watch", "Dislike request failed: " + t.getMessage());
            }
        });
    }
    private void addView(String userID, String videoId) {
        userAPI.addView(userID, videoId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Watch", "View added successfully");
                } else {
                    Log.e("Watch", "Failed to add view: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e("Watch", "Failed to add view: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("videoId", video.getId());
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("video", video);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Video updatedVideo = data.getParcelableExtra("video");
            if (updatedVideo != null) {
                this.video = updatedVideo;
                Intent resultIntent = new Intent();
                resultIntent.putExtra("video", updatedVideo);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }
    }






}
