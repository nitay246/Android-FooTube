package com.example.advanced_system_programing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private videoListSingleton videoList; // Reference to the singleton video list
    private Context context; // Context reference for starting activities
    private user user; // User object associated with the adapter

    private static final int WATCH_REQUEST_CODE = 1; // Request code for watching a video

    // Constructor for VideoAdapter
    public VideoAdapter(videoListSingleton videoList, user user, Context context) {
        this.videoList = videoList;
        this.context = context;
        this.user = user;
    }

    // ViewHolder for holding video item views
    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the video item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }

    // Bind data to views in ViewHolder
    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        // Get the video object at the given position
        Video video = videoList.getVideoList().get(position);

        // Set title and description of the video
        holder.title.setText(video.getTitle());
        holder.description.setText(video.getDescription());

        // Set thumbnail image of the video
        Bitmap thumbnail = video.getThumbnail();
        if (thumbnail != null) {
            holder.thumbnail.setImageBitmap(thumbnail);
        } else {
            // Set a placeholder image if the thumbnail is not available
            holder.thumbnail.setImageResource(R.drawable.ic_default_profile);
        }

        // Set onClickListener for the itemView (whole video item)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log the click event
                Log.d("VideoAdapter", "Item clicked: " + video.getTitle());

                // Create an intent to start Watch activity
                Intent intent = new Intent(context, Watch.class);
                intent.putExtra("user", user); // Pass user object to Watch activity
                intent.putExtra("videoUrl", video.getVideoUrl()); // Pass video URL to Watch activity
                intent.putExtra("video", video); // Pass video object itself to Watch activity

                // Start activity with startActivityForResult if context is an Activity
                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, WATCH_REQUEST_CODE);
                } else {
                    context.startActivity(intent); // Start activity normally if context is not an Activity
                }
            }
        });
    }

    // Return the total number of videos in the list
    @Override
    public int getItemCount() {
        return videoList.getVideoList().size();
    }

    // ViewHolder class for video item views
    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail; // ImageView for video thumbnail
        TextView title; // TextView for video title
        TextView description; // TextView for video description

        // Constructor to initialize views
        public VideoViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail); // Initialize thumbnail ImageView
            title = itemView.findViewById(R.id.title); // Initialize title TextView
            description = itemView.findViewById(R.id.description); // Initialize description TextView
        }
    }
}
