package api;

import com.example.advanced_system_programing.MyApplication;
import com.example.advanced_system_programing.R;
import com.example.advanced_system_programing.Video;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserAPI {
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;

    public UserAPI() {
        String baseUrl = MyApplication.context.getString(R.string.BaseUrl);
        System.out.println("Base URL: " + baseUrl);

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public Call<JsonObject> getUserProfile(String userId) {
        return webServiceAPI.getUserProfile(userId);
    }

    public Call<JsonObject> updateUserProfile(String token, String userId, JsonObject updatedUser) {
        return webServiceAPI.updateUserProfile(token, userId, updatedUser);
    }

    public Call<JsonObject> tokenize(JsonObject loginRequest) {
        System.out.println("Tokenize request body: " + loginRequest.toString());
        return webServiceAPI.tokenize(loginRequest);
    }

    public Call<ResponseBody> signup(JsonObject newUser) {
        System.out.println("Signup request body: " + newUser.toString());
        return webServiceAPI.signup(newUser);
    }
    public Call<List<Video>> getUserVideos(String userId) {
        return webServiceAPI.getUserVideos(userId);
    }
    public Call<ResponseBody> deleteUser(String token, String userId) {
        return webServiceAPI.deleteUser(token, userId);
    }
    public Call<JsonObject> addView(String userID, String videoId) {
        return webServiceAPI.addView(userID, videoId);
    }
    public Call<JsonObject> updateRecommend(String userID, String videoId) {
        return webServiceAPI.updateRecommend(userID, videoId);
    }
    public Call<JsonObject> recommendedVideo(String userID, String videoId) {
        return webServiceAPI.recommendedVideo(userID, videoId);
    }
    public Call<JsonObject> disconnectUser(String token, String userID) {
        return webServiceAPI.disconnectUser(token, userID);
    }
}