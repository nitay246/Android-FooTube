package com.example.advanced_system_programing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class signup extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText usernameField;
    private EditText passwordField;
    private EditText nameField;
    private EditText confirmPasswordField;
    private ImageView profilePicField;
    private Uri profilePicUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up); // Set the layout for the sign-up activity

        // Initialize UI elements
        Button signUpButton = findViewById(R.id.login_button);
        usernameField = findViewById(R.id.etChooseuser);
        passwordField = findViewById(R.id.editTextTextPassword);
        nameField = findViewById(R.id.editTextDisplayName);
        confirmPasswordField = findViewById(R.id.editTextTextPasswordConfirm);
        profilePicField = findViewById(R.id.ivProfilePic);
        Button homeButton = findViewById(R.id.home_btn);

        // Set up the home button click listener
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(signup.this, HomepageActivity.class);
            startActivity(intent); // Start the HomepageActivity
        });

        // Set up the sign-up button click listener
        signUpButton.setOnClickListener(v -> {
            // Get the entered username, password, name, and confirm password
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String name = nameField.getText().toString().trim();
            String confirmPassword = confirmPasswordField.getText().toString().trim();

            // Check if any field is empty
            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(signup.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                Toast.makeText(signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the instance of UserListSingleton
            UserListSingleton userListSingleton = UserListSingleton.getInstance();

            // Check if the username is already taken
            if (userListSingleton.isUsernameTaken(username)) {
                Toast.makeText(signup.this, "Username is already taken", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the password meets the required pattern
            String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{5,}$";
            if (!password.matches(passwordPattern)) {
                Toast.makeText(signup.this, "Password must contain letters and numbers", Toast.LENGTH_LONG).show();
                return;
            }

            // Get the profile picture URI as a string
            String profilePicUriString = profilePicUri != null ? profilePicUri.toString() : null;

            // Create a new user object
            user newUser = new user(username, name, password, profilePicUriString, this);
            userListSingleton.addUser(newUser); // Add the new user to the singleton

            Toast.makeText(signup.this, "Sign up successful", Toast.LENGTH_SHORT).show();
        });
    }

    // Method to handle profile picture selection
    public void selectProfilePicture(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST); // Start the activity to pick an image
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profilePicUri = data.getData(); // Get the selected image URI

            try {
                // Get the bitmap from the selected image URI and set it to the ImageView
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profilePicUri);
                profilePicField.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
