package com.example.advanced_system_programing;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class user implements Parcelable {
    private String username;        // Username of the user
    private String displayName;     // Display name of the user
    private String password;        // Password of the user
    private String profilePicUri;   // URI to the profile picture of the user

    // Constructor to initialize a user object with basic details
    public user(String username, String name, String password, String profilePicUri, Context context) {
        this.username = username;
        this.displayName = name;
        this.password = password;
        this.profilePicUri = profilePicUri;
    }

    // Parcelable constructor: retrieves data from Parcel
    protected user(Parcel in) {
        username = in.readString();
        displayName = in.readString();
        password = in.readString();
        profilePicUri = in.readString();
    }

    // Parcelable CREATOR instance to create user objects from Parcel
    public static final Creator<user> CREATOR = new Creator<user>() {
        @Override
        public user createFromParcel(Parcel in) {
            return new user(in);
        }

        @Override
        public user[] newArray(int size) {
            return new user[size];
        }
    };

    // Parcelable method: describes contents of the object
    @Override
    public int describeContents() {
        return 0;
    }

    // Parcelable method: writes object data to the Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(displayName);
        dest.writeString(password);
        dest.writeString(profilePicUri);
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for display name
    public String getDisplayName() {
        return displayName;
    }

    // Setter for display name
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }

    // Getter for profile picture URI
    public String getProfilePic() {
        return profilePicUri;
    }

    // Setter for profile picture URI
    public void setProfilePic(String profilePicUri) {
        this.profilePicUri = profilePicUri;
    }
}
