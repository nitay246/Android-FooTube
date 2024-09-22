package com.example.advanced_system_programing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class intro_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro); // Set the layout for the intro activity

        // Find the login button by its ID
        Button btnLogin = findViewById(R.id.go_to_login);
        // Set an onClickListener for the login button
        btnLogin.setOnClickListener(v -> {
            // Create an Intent to start the Signin activity
            Intent intent = new Intent(intro_activity.this, Signin.class);
            // Start the Signin activity
            startActivity(intent);
        });

        // Find the signup button by its ID
        Button btnSignup = findViewById(R.id.go_to_signup); // Assuming you have a signup button
        // Set an onClickListener for the signup button
        btnSignup.setOnClickListener(v -> {
            // Create an Intent to start the signup activity
            Intent intent = new Intent(intro_activity.this, signup.class);
            // Start the signup activity
            startActivity(intent);
        });
    }
}
