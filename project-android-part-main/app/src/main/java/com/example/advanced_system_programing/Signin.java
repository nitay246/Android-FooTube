package com.example.advanced_system_programing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Signin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in); // Set the layout for the sign-in activity

        // Find the login button, username and password EditTexts, and home button by their IDs
        Button loginButton = findViewById(R.id.login_button);
        EditText usernameEditText = findViewById(R.id.editTextTextPassword4);
        EditText passwordEditText = findViewById(R.id.editTextTextPassword);
        Button homeButton = findViewById(R.id.home_btn);

        // Handle home button click
        homeButton.setOnClickListener(v -> {
            // Create an Intent to start the HomepageActivity
            Intent intent = new Intent(Signin.this, HomepageActivity.class);
            startActivity(intent); // Start the HomepageActivity
        });

        // Handle login button click
        loginButton.setOnClickListener(v -> {
            // Get the entered username and password, and trim any leading/trailing spaces
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Get the instance of UserListSingleton
            UserListSingleton userListSingleton = UserListSingleton.getInstance();
            if (userListSingleton == null) {
                // If the singleton instance is null, show an invalid credentials message
                Toast.makeText(Signin.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrieve the user object from the singleton using the provided username and password
            user authenticatedUser = userListSingleton.getUser(username, password);
            if (authenticatedUser != null) {
                // If the user is authenticated, show a success message
                Toast.makeText(Signin.this, "Success", Toast.LENGTH_SHORT).show();

                // Create an Intent to return to HomepageActivity with the authenticated user object
                Intent intent = new Intent(Signin.this, HomepageActivity.class);
                intent.putExtra("user", authenticatedUser);
                startActivity(intent); // Start the HomepageActivity
                finish(); // Close the Signin activity
            } else {
                // If the user is not found, show a user not found message
                Toast.makeText(Signin.this, "User not found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
