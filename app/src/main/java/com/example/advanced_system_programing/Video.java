package com.example.advanced_system_programing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.bson.types.ObjectId;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "videos")
@TypeConverters({Video.BitmapConverter.class, Video.CommentListConverter.class})
public class Video implements Parcelable {

    @PrimaryKey
    @NonNull
    private String _id; // Primary key field
    private String creator;
    private String title;
    private String description;
    private String source;


    private int likes;
    private int dislikes;
    private int views;
    private Bitmap thumbnail;  // Thumbnail image of the video
    private List<Comment> comments;  // List of comments associated with the video

    @Ignore
    private Context context;  // Context reference for operations

    // Constructor with video URL for extracting thumbnail
    public Video(String title, String description, String creator, String source, int likes, int dislikes, int views, Bitmap picture, Context context) throws IOException {
        this._id = new ObjectId().toString(); // Generate a new ObjectId if not provided
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.source = source;
        this.likes = likes;
        this.dislikes = dislikes;
        this.views = views;
        this.context = context;
        this.thumbnail = picture;  // Extract thumbnail from video URL
        this.comments = new ArrayList<>();  // Initialize empty list of comments
    }

    // Empty constructor for Room
    public Video() {
        this._id = new ObjectId().toString();
    }

    // Constructor with pre-existing thumbnail as Bitmap
    @Ignore
    public Video(String _id, String title, String description, String creator, String source, int likes, int dislikes, int views, Bitmap picture, Context context) throws IOException {
        this._id = _id;
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.source = source;
        this.likes = likes;
        this.dislikes = dislikes;
        this.views = views;
        this.context = context;
        this.thumbnail = picture;  // Use provided thumbnail Bitmap
        this.comments = new ArrayList<>();  // Initialize empty list of comments
    }

    // Parcelable constructor
    protected Video(Parcel in) {
        _id = in.readString(); // Read id from parcel
        title = in.readString();
        description = in.readString();
        creator = in.readString();
        source = in.readString();
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
    @NonNull
    public String getId() {
        return _id;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + _id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", source='" + source + '\'' +
                ", creator='" + creator + '\'' +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", views=" + views +
                ", comments=" + comments +
                '}';
    }

    public void setId(@NonNull String _id) {
        this._id = _id;
    }

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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }


    // Equals method to compare two Video objects based on source
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return source.equals(video.source);
    }

    // Parcelable methods implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(_id); // Write id to parcel
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(creator);
        parcel.writeString(source);
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
    private Bitmap extractThumbnail(String source) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            String resourcePath = "android.resource://" + context.getPackageName() + "/raw/" + source.replace(".mp4", "");
            retriever.setDataSource(resourcePath);
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    // Converters for Bitmap and List<Comment> for Room
    public static class BitmapConverter {
        @TypeConverter
        public static byte[] fromBitmap(Bitmap bitmap) {
            if (bitmap == null) {
                return null;
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            return outputStream.toByteArray();
        }

        @TypeConverter
        public static Bitmap toBitmap(byte[] bytes) {
            return bytes == null ? null : BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
    }

    public static class CommentListConverter {
        private static final Gson gson = new Gson();
        private static final Type type = new TypeToken<List<Comment>>() {}.getType();

        @TypeConverter
        public static String fromComments(List<Comment> comments) {
            return comments == null ? null : gson.toJson(comments, type);
        }

        @TypeConverter
        public static List<Comment> toComments(String commentsString) {
            return commentsString == null ? null : gson.fromJson(commentsString, type);
        }
    }
}
