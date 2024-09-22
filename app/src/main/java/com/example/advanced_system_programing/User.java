package com.example.advanced_system_programing;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {

    private String _id;


    private String username;        // Username of the user
    private String displayname;     // Display name of the user
    private String password;        // Password of the user
    private String profilePicBase64;   // Base64 encoded profile picture of the user

    // Constructor to initialize a user object with basic details and context
    public User(String username, String name, String password, String profilePicBase64, Context context) {
        this.username = username;
        this.displayname = name;
        this.password = password;
        this.profilePicBase64 = profilePicBase64;
    }

    // Another constructor without context
    public User(String _id, String username, String name, String password, String profilePicBase64) {
        this._id = _id;
        this.username = username;
        this.displayname = name;
        this.password = password;
        this.profilePicBase64 = profilePicBase64;
    }

    // Parcelable constructor: retrieves data from Parcel
    protected User(Parcel in) {
        _id = in.readString();
        username = in.readString();
        displayname = in.readString();
        password = in.readString();
        profilePicBase64 = in.readString();
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + _id + '\'' +
                ", username='" + username + '\'' +
                ", displayName='" + displayname + '\'' +
                ", password='" + password + '\'' +
                ", img='" + profilePicBase64 + '\'' +
                '}';
    }

    // Parcelable CREATOR instance to create user objects from Parcel
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
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
        dest.writeString(_id);
        dest.writeString(username);
        dest.writeString(displayname);
        dest.writeString(password);
        dest.writeString(profilePicBase64);
    }

    // Getter for username
    @NonNull
    public String getUsername() {
        return username;
    }

    // Getter for display name
    public String getDisplayName() {
        return displayname;
    }

    // Setter for display name
    public void setDisplayName(String displayName) {
        this.displayname = displayName;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }

    // Getter for Base64 encoded profile picture (handles both PNG and JPEG prefixes)
    public String getProfilePicBase64() {
        if (profilePicBase64 != null) {
            if (profilePicBase64.startsWith("data:image/png;base64,")) {
                return profilePicBase64.substring("data:image/png;base64,".length());
            } else if (profilePicBase64.startsWith("data:image/jpeg;base64,")) {
                return profilePicBase64.substring("data:image/jpeg;base64,".length());
            }
        }
        return profilePicBase64;
    }


    // Setter for Base64 encoded profile picture
    public void setProfilePicBase64(String profilePicBase64) {
        this.profilePicBase64 = profilePicBase64;
    }

    // Getter for _id
    public String get_id() {
        return _id;
    }

    // Setter for _id
    public void set_id(String _id) {
        this._id = _id;
    }
}
