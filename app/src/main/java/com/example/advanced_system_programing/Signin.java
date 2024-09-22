package com.example.advanced_system_programing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import api.TokenValidator;
import api.UserAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signin extends AppCompatActivity {

    private UserAPI userAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in); // Set the layout for the sign-in activity

        // Initialize UserAPI
        userAPI = new UserAPI();

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

            // Create a JSON object with the username and password
            JsonObject loginRequest = new JsonObject();
            loginRequest.addProperty("username", username);
            loginRequest.addProperty("password", password);

            // Make the network request to the login endpoint
            userAPI.tokenize(loginRequest).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    System.out.println("Response code: " + response.code()); // Log response code
                    if (response.isSuccessful() && response.body() != null) {
                        String token = response.body().get("token").getAsString();
                        saveTokenToLocalStorage(token);
                        Log.d("Sign in",TokenValidator.decodeTokenAndGetUserId(token));
                        Toast.makeText(Signin.this, "Login successful", Toast.LENGTH_SHORT).show();

                        // Create an Intent to return to HomepageActivity
                        Intent intent = new Intent(Signin.this, HomepageActivity.class);
                        startActivity(intent); // Start the HomepageActivity
                        finish(); // Close the Signin activity
                    } else {
                        Toast.makeText(Signin.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    System.out.println("Network error: " + t.getMessage()); // Log network error
                    Toast.makeText(Signin.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void saveTokenToLocalStorage(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }
}

