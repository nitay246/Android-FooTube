package com.example.advanced_system_programing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<Video> videoList; // List to hold videos // Reference to the singleton video list
    private Context context; // Context reference for starting activities

    private static final int WATCH_REQUEST_CODE = 1; // Request code for watching a video

    // Constructor for VideoAdapter
    public VideoAdapter(List<Video> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    public void setVideos(List<Video> videoList) {
        this.videoList = videoList;
        notifyDataSetChanged(); // Notify adapter that data has changed
    }

    // ViewHolder for holding video item views
    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the video item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }


    private Bitmap extractThumbnail(String videoUrl) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoUrl, new HashMap<String, String>());
            return retriever.getFrameAtTime();
        } finally {
            retriever.release();
        }
    }


    // Bind data to views in ViewHolder
    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        // Get the video object at the given position
        Video video = videoList.get(position);
        Log.d("VideoAdapter", "video:" + video.getId());
        // Set title and description of the video
        holder.title.setText(video.getTitle());
        holder.description.setText(video.getDescription());

        // Set thumbnail image of the video
        Bitmap thumbnail = null;
        try {
            String videoUrl = "http://10.0.2.2:8000/" + video.getSource(); // Construct the full URL of the video
            Log.d("VideoAdapter", "videoUrl:" + videoUrl);
            thumbnail = extractThumbnail(videoUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (thumbnail != null) {
            holder.thumbnail.setImageBitmap(thumbnail);
        } else {
            // Set a placeholder image if the thumbnail is not available
            holder.thumbnail.setImageResource(R.drawable.ic_default_profile);
            Log.d("VideoAdapter", "image is null");
        }




        // Set onClickListener for the itemView (whole video item)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log the click event
                Log.d("VideoAdapter", "Item clicked: " + video);
                Log.d("VideoAdapter", "comments of item: " + video.getComments());
                // Create an intent to start Watch activity
                Intent intent = new Intent(context, Watch.class);
                Log.d("VideoAdapter", "Item clicked, video creator: " + video.getCreator());
                Log.d("VideoAdapter", "Item clicked, video id: " + video.getId());

                intent.putExtra("userId", video.getCreator()); // Pass user ID to Watch activity
                intent.putExtra("videoId", video.getId()); // Pass video ID to Watch activity
                Log.d("VideoAdapter", "Item clicked, video source: " + video.getSource());

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
        return videoList.size();
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
