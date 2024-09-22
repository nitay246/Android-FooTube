package com.example.advanced_system_programing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import api.UserAPI;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signup extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText usernameField;
    private EditText passwordField;
    private EditText nameField;
    private EditText confirmPasswordField;
    private ImageView profilePicField;
    private Uri profilePicUri;
    private String profilePicBase64;
    private UserAPI userAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        Button SignupButton = findViewById(R.id.login_button);
        usernameField = findViewById(R.id.etChooseuser);
        passwordField = findViewById(R.id.editTextTextPassword);
        nameField = findViewById(R.id.editTextDisplayName);
        confirmPasswordField = findViewById(R.id.editTextTextPasswordConfirm);
        profilePicField = findViewById(R.id.ivProfilePic);
        Button homeButton = findViewById(R.id.home_btn);

        userAPI = new UserAPI();

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Signup.this, HomepageActivity.class);
            startActivity(intent);
        });

        SignupButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String name = nameField.getText().toString().trim();
            String confirmPassword = confirmPasswordField.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(Signup.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(Signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{5,}$";
            if (!password.matches(passwordPattern)) {
                Toast.makeText(Signup.this, "Password must contain letters and numbers", Toast.LENGTH_LONG).show();
                return;
            }

            JsonObject newUser = new JsonObject();
            newUser.addProperty("username", username);
            newUser.addProperty("displayname", name);
            newUser.addProperty("password", password);
            newUser.addProperty("img", profilePicBase64);

            registerUserOnServer(newUser);
        });
    }

    private void registerUserOnServer(JsonObject newUser) {
        System.out.println("Registering user with data: " + newUser.toString());

        userAPI.signup(newUser).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Signup.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Signup.this, HomepageActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Signup.this, "Sign up failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    System.out.println("Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(Signup.this, "Sign up failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void selectProfilePicture(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profilePicUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profilePicUri);
                profilePicField.setImageBitmap(bitmap);

                // Convert the image to Base64
                profilePicBase64 = encodeImageToBase64(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
