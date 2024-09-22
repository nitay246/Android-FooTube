package com.example.advanced_system_programing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Watch extends AppCompatActivity {
    private static final int EDIT_REQUEST_CODE = 2;

    private Video video;
    private user user;
    private boolean isVideoPlaying = false;
    private CommentAdapter commentAdapter;
    private RecyclerView commentsRecyclerView;

    private enum LikeState {
        NEUTRAL,
        LIKED,
        DISLIKED
    }

    private LikeState currentLikeState = LikeState.NEUTRAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch);

        Button editButton = findViewById(R.id.edit);
        Button homeButton = findViewById(R.id.home);

        // Restore state if exists
        if (savedInstanceState != null) {
            video = savedInstanceState.getParcelable("video");
            user = savedInstanceState.getParcelable("user");
        } else {
            video = getIntent().getParcelableExtra("video");
            user = getIntent().getParcelableExtra("user");
        }

        if (video == null) {
            finish();
            return;
        }

        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        final VideoView videoView = findViewById(R.id.videoView2);
        TextView textViewLikes = findViewById(R.id.likesTextView);
        TextView textViewDislikes = findViewById(R.id.dislikesTextView);
        SeekBar seekBar = findViewById(R.id.seekBar);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch nightModeSwitch = findViewById(R.id.nightmodeswitch);

        titleTextView.setText(video.getTitle());
        descriptionTextView.setText(video.getDescription());
        textViewLikes.setText(String.valueOf(video.getLikes()));
        textViewDislikes.setText(String.valueOf(video.getDislikes()));
        videoView.setVideoPath(video.getVideoUrl());

        if (user == null) {
            // Handle the case where the user object is not available
            ImageButton likeButton = findViewById(R.id.likeButton);
            ImageButton dislikeButton = findViewById(R.id.dislikeButton);
            ImageButton commentButton = findViewById(R.id.addCommentButton);
            likeButton.setEnabled(false);
            dislikeButton.setEnabled(false);
            commentButton.setEnabled(false);

            // Make the edit button invisible
            editButton.setVisibility(View.INVISIBLE);
        } else {
            editButton.setOnClickListener(v -> {
                Intent editIntent = new Intent(Watch.this, Edit_activity.class);
                editIntent.putExtra("video", video);
                editIntent.putExtra("user", user);
                startActivityForResult(editIntent, EDIT_REQUEST_CODE);
            });

            ImageButton likeButton = findViewById(R.id.likeButton);
            likeButton.setOnClickListener(v -> {
                switch (currentLikeState) {
                    case NEUTRAL:
                        // Increment likes count in the Video object
                        video.setLikes(video.getLikes() + 1);
                        textViewLikes.setText(String.valueOf(video.getLikes()));
                        currentLikeState = LikeState.LIKED;
                        break;
                    case LIKED:
                        // Undo like
                        video.setLikes(video.getLikes() - 1);
                        textViewLikes.setText(String.valueOf(video.getLikes()));
                        currentLikeState = LikeState.NEUTRAL;
                        break;
                    case DISLIKED:
                        // Undo dislike and add like
                        video.setDislikes(video.getDislikes() - 1);
                        textViewDislikes.setText(String.valueOf(video.getDislikes()));
                        video.setLikes(video.getLikes() + 1);
                        textViewLikes.setText(String.valueOf(video.getLikes()));
                        currentLikeState = LikeState.LIKED;
                        break;
                }
            });

            ImageButton dislikeButton = findViewById(R.id.dislikeButton);
            dislikeButton.setOnClickListener(v -> {
                switch (currentLikeState) {
                    case NEUTRAL:
                        // Increment dislikes count in the Video object
                        video.setDislikes(video.getDislikes() + 1);
                        textViewDislikes.setText(String.valueOf(video.getDislikes()));
                        currentLikeState = LikeState.DISLIKED;
                        break;
                    case DISLIKED:
                        // Undo dislike
                        video.setDislikes(video.getDislikes() - 1);
                        textViewDislikes.setText(String.valueOf(video.getDislikes()));
                        currentLikeState = LikeState.NEUTRAL;
                        break;
                    case LIKED:
                        // Undo like and add dislike
                        video.setLikes(video.getLikes() - 1);
                        textViewLikes.setText(String.valueOf(video.getLikes()));
                        video.setDislikes(video.getDislikes() + 1);
                        textViewDislikes.setText(String.valueOf(video.getDislikes()));
                        currentLikeState = LikeState.DISLIKED;
                        break;
                }
            });
        }

        homeButton.setOnClickListener(v -> {
            Intent goHome = new Intent(Watch.this, HomepageActivity.class);
            if (user != null) {
                goHome.putExtra("user", user);
            }
            startActivity(goHome);
        });

        // Start video playback
        videoView.start();
        isVideoPlaying = true;

        // SeekBar listener
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

        // Night mode switch listener
        nightModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update UI based on switch state
            if (isChecked) {
                // Set night mode (change colors, etc.)
                // For example:
                getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.black));
            } else {
                // Set day mode (change colors back to normal)
                // For example:
                getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.background_light));
            }
        });

        // VideoView listener to pause/play video on click
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

        commentAdapter = new CommentAdapter(video.getComments(), user != null ? user.getDisplayName() : null);
        commentsRecyclerView.setAdapter(commentAdapter);

        ImageButton addCommentButton = findViewById(R.id.addCommentButton);
        addCommentButton.setOnClickListener(v -> {
            if (user != null) {
                Comment newComment = new Comment(user.getDisplayName(), "");
                video.addComment(newComment);
                commentAdapter.notifyItemInserted(video.getComments().size() - 1);
                commentAdapter.editComment(video.getComments().size() - 1);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("video", video);
        outState.putParcelable("user", user);
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
                // Pass the updated video back to HomepageActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("video", updatedVideo);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }
    }
}
