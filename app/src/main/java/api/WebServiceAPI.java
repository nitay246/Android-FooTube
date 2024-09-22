package api;

import com.example.advanced_system_programing.Comment;
import com.example.advanced_system_programing.Video;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface WebServiceAPI {


    @GET("users/{id}")
    Call<JsonObject> getUserProfile(@Path("id") String userId);

    @PATCH("users/{id}")
    Call<JsonObject> updateUserProfile(@Header("Authorization") String token, @Path("id") String userId, @Body JsonObject body);

    @GET("videos/")
    Call<List<Video>> getVideosForHomePage();
    @GET("users/{id}/videos/")
    Call<List<Video>> getUserVideos(@Path("id") String userId);

    @DELETE("users/{id}")
    Call<ResponseBody> deleteUser(@Header("Authorization") String token, @Path("id") String userId);

    @POST("videos/{videoId}/comments")
    Call<JsonObject> createComment(@Header("Authorization") String token, @Body JsonObject comment, @Path("videoId") String videoId);

    @DELETE("videos/{videoId}/comments/{index}")
    Call<List<Comment>> deleteComment(@Header("Authorization") String token, @Path("videoId") String videoId, @Path("index") int index);

    @PATCH("videos/{videoId}/comments/{index}")
    Call<JsonObject> editComment(@Header("Authorization") String token, @Path("videoId") String videoId, @Path("index") int index, @Body JsonObject comment);

    @POST("videos/{id}/like")
    Call<JsonObject> addLike(@Header("Authorization") String token, @Path("id") String videoId);

    @POST("videos/{id}/dislike")
    Call<JsonObject> addDislike(@Header("Authorization") String token, @Path("id") String videoId);

    @Headers("Content-Type: application/json")
    @POST("users")
    Call<ResponseBody> signup(@Body JsonObject user);

    @GET("users/{creator}/videos/{id}")
    Call<Video> getVideo(@Path("creator") String userId, @Path("id") String videoId);

    @DELETE("users/{creator}/videos/{id}")
    Call<ResponseBody> deleteVideo(@Header("Authorization") String token, @Path("creator") String userId, @Path("id") String videoId);

    @PATCH("users/{creator}/videos/{id}")
    Call<JsonObject> updateVideo(@Header("Authorization") String token, @Path("creator") String userId, @Path("id") String videoId,@Body JsonObject body);

    @Multipart
    @POST("users/{id}/videos")
    Call<JsonObject> uploadVideo(
            @Header("Authorization") String token,
            @Path("id") String userId,
            @Part MultipartBody.Part video,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description
    );


    @Headers("Content-Type: application/json")
    @POST("tokens")
    Call<JsonObject> tokenize(@Body JsonObject body);

    @POST("users/{creator}/videos/{id}/views")
    Call<JsonObject> addView(@Path("creator") String userID, @Path("id") String videoID);

    @POST("users/{userId}/updateRecommend/{videoId}")
    Call<JsonObject> updateRecommend(@Path("userId") String userId, @Path("videoId") String videoId);

    @POST("users/{userId}/recommendedVideo/{videoId}")
    Call<JsonObject> recommendedVideo(@Path("userId") String userId, @Path("videoId") String videoId);

    @POST("users/{userId}/disconnectuser")
    Call<JsonObject> disconnectUser(@Header("Authorization") String token, @Path("userId") String userId);
}
