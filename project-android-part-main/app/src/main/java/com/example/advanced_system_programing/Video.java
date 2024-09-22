package com.example.advanced_system_programing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Video implements Parcelable {

    // Properties of the video
    private String title;
    private String description;
    private String videoUrl;
    private int likes;
    private int dislikes;
    private int views;
    private Bitmap thumbnail;  // Thumbnail image of the video
    private List<Comment> comments;  // List of comments associated with the video
    private Context context;  // Context reference for operations

    // Constructor with video URL for extracting thumbnail
    public Video(String title, String description, String videoUrl, int likes, int dislikes, int views, Context context) throws IOException {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.likes = likes;
        this.dislikes = dislikes;
        this.views = views;
        this.context = context;
        this.thumbnail = extractThumbnail(videoUrl);  // Extract thumbnail from video URL
        this.comments = new ArrayList<>();  // Initialize empty list of comments
    }

    // Constructor with pre-existing thumbnail as Bitmap
    public Video(String title, String description, String videoUrl, int likes, int dislikes, int views, Bitmap picture, Context context) throws IOException {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.likes = likes;
        this.dislikes = dislikes;
        this.views = views;
        this.context = context;
        this.thumbnail = picture;  // Use provided thumbnail Bitmap
        this.comments = new ArrayList<>();  // Initialize empty list of comments
    }

    // Parcelable constructor
    protected Video(Parcel in) {
        title = in.readString();
        description = in.readString();
        videoUrl = in.readString();
        likes = in.readInt();
        dislikes = in.readInt();
        views = in.readInt();
        byte[] thumbnailBytes = in.createByteArray();
        if (thumbnailBytes != null) {
            thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
        }
        comments = in.createTypedArrayList(Comment.CREATOR);
    }

    // Parcelable CREATOR instance for deserialization
    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    // Getters and setters for all properties
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void editComment(int index, String newText) {
        if (index >= 0 && index < comments.size()) {
            comments.get(index).setText(newText);
        }
    }

    public void setComments(List<Comment> comments){
        this.comments=comments;
    }

    public void deleteComment(int index) {
        if (index >= 0 && index < comments.size()) {
            comments.remove(index);
        }
    }

    // Equals method to compare two Video objects based on videoUrl
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return videoUrl.equals(video.videoUrl);
    }

    // Parcelable methods implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(videoUrl);
        parcel.writeInt(likes);
        parcel.writeInt(dislikes);
        parcel.writeInt(views);
        if (thumbnail != null) {
            byte[] thumbnailBytes = getBytesFromBitmap(thumbnail);
            parcel.writeByteArray(thumbnailBytes);
        }
        parcel.writeTypedList(comments);
    }

    // Method to extract the first frame of the video as a thumbnail
    private Bitmap extractThumbnail(String videoUrl) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoUrl);
            return retriever.getFrameAtTime();
        } finally {
            retriever.release();
        }
    }

    // Convert Bitmap to byte array
    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
