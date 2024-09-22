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

import com.auth0.android.jwt.JWT;
import com.google.gson.JsonObject;

import api.UserAPI;
import api.WebServiceAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Edit_user extends AppCompatActivity {
    private static final String TAG = "Edit_user";
    private User user; // Ensure 'user' class is defined properly
    private EditText usernameField;
    private EditText passwordField;
    private EditText displayNameField;
    private WebServiceAPI webServiceAPI;
    private SharedPreferences sharedPreferences;
    private String userId;
    private UserAPI userAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user);

        userAPI = new UserAPI();

        sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        webServiceAPI = retrofit.create(WebServiceAPI.class);

        passwordField = findViewById(R.id.editTextTextPassword);
        displayNameField = findViewById(R.id.editTextDisplayName);

        Button updateButton = findViewById(R.id.Edit);
        Button homeButton = findViewById(R.id.home_btn);

        // Check and validate JWT token
        checkAndValidateToken();

        updateButton.setOnClickListener(v -> updateUser());
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Edit_user.this, HomepageActivity.class);
            startActivity(intent); // Start the HomepageActivity
            finish();
        });
    }

    private void checkAndValidateToken() {
        String token = sharedPreferences.getString("token", null);
        if (token == null) {
            Log.e(TAG, "No valid JWT");
            throw new RuntimeException("No valid JWT found: 999999");
        }

        JWT jwt = new JWT(token);
        userId = jwt.getClaim("id").asString();
        String username = jwt.getClaim("username").asString();

        if (userId == null || jwt.isExpired(10)) {
            Log.e(TAG, "No valid JWT");
            throw new RuntimeException("No valid JWT found: 999999");
        }

        // Populate username field from JWT
        usernameField.setText(username);

        fetchUserData();
    }

    private void fetchUserData() {
        userAPI.getUserProfile(userId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject userData = response.body();
                    if (userData.has("username") && userData.has("nick") && userData.has("password") && userData.has("img")) {
                        user = new User(
                                userData.get("username").getAsString(),
                                userData.get("nick").getAsString(),
                                userData.get("password").getAsString(),
                                userData.get("img").getAsString(), // Pass profile pic URI to user constructor
                                getApplicationContext()
                        );
                        usernameField.setText(user.getUsername());
                        displayNameField.setText(user.getDisplayName());
                        passwordField.setText(user.getPassword());
                    } else {
                        Toast.makeText(Edit_user.this, "Incomplete user data received", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(Edit_user.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUser() {
        String token = sharedPreferences.getString("token", null);
        if (token == null) {
            Toast.makeText(this, "Token not found", Toast.LENGTH_SHORT).show();
            return;
        }

        JWT jwt = new JWT(token);
        String userId = jwt.getClaim("id").asString();
        if (userId == null) {
            Toast.makeText(this, "Invalid token", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = usernameField.getText().toString().trim();
        String displayName = displayNameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        JsonObject updatedUser = new JsonObject();
        updatedUser.addProperty("username", username);
        updatedUser.addProperty("nick", displayName);
        if (!password.isEmpty()) {
            updatedUser.addProperty("password", password);
        }

        webServiceAPI.updateUserProfile("Bearer " + token, userId, updatedUser).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Edit_user.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    // Update the JWT token with the new username and password
                    if (!password.isEmpty()) {
                        updateToken(username, password);
                    }
                } else {
                    Toast.makeText(Edit_user.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(Edit_user.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateToken(String username, String password) {
        JsonObject loginRequest = new JsonObject();
        loginRequest.addProperty("username", username);
        loginRequest.addProperty("password", password);

        userAPI.tokenize(loginRequest).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String newToken = response.body().get("token").getAsString();
                    saveTokenToLocalStorage(newToken);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(Edit_user.this, "Failed to update token", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTokenToLocalStorage(String token) {
        sharedPreferences.edit().putString("token", token).apply();
    }
}

