package com.example.advanced_system_programing;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

//@Entity(tableName = "comments")
public class Comment implements Parcelable {
    private String user;
    private String userId;
    private String text;
    // private long timestamp; // Commented out timestamp field

    public Comment(String userId, String text,String user) {
        this.user = user;
        this.userId = userId;
        this.text = text;
    }


    // Constructor to create Comment object from Parcel
    protected Comment(Parcel in) {
        userId = in.readString();
        text = in.readString();
    }

    // Parcelable.Creator implementation to create array and Comment from Parcel
    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    // Method to write Comment object to Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(userId); // Write userId to parcel
        dest.writeString(text); // Write text to parcel
    }

    // Describe contents for Parcelable (default implementation)
    @Override
    public int describeContents() {
        return 0;
    }

    // Override toString to return the text of the comment
    @NonNull
    @Override
    public String toString() {
        return text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
