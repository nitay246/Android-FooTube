package api;

import androidx.lifecycle.MutableLiveData;

import com.example.advanced_system_programing.Comment;
import com.example.advanced_system_programing.MyApplication;
import com.example.advanced_system_programing.R;
import com.example.advanced_system_programing.Video;

import com.google.gson.JsonObject;


import java.util.List;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoAPI {


    Retrofit retrofit;
    WebServiceAPI webServiceAPI;

    public VideoAPI() {



        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public Call<List<Video>> getVideos() {
        return webServiceAPI.getVideosForHomePage();
    }


    public Call<Video> getVideo(String userId, String videoId) {
        return webServiceAPI.getVideo(userId, videoId);
    }

    public Call<JsonObject> createComment(String token, JsonObject text, String videoId) {
        return webServiceAPI.createComment(token, text, videoId);
    }

    public Call<List<Comment>> deleteComment(String token, String videoId, int index) {
        return webServiceAPI.deleteComment(token, videoId, index);
    }

    public Call<JsonObject> editComment(String token, String videoId, int index, JsonObject comment) {
        return webServiceAPI.editComment(token, videoId, index, comment);
    }

    public Call<JsonObject> addLike(String token,  String videoId) {
        return webServiceAPI.addLike(token,  videoId);
    }

    public Call<JsonObject> addDislike(String token,  String videoId) {
        return webServiceAPI.addDislike(token,  videoId);
    }

    public Call<ResponseBody> deleteVideo(String token, String userId, String videoId) {
        return webServiceAPI.deleteVideo(token, userId, videoId);
    }
    public Call<JsonObject> updateVideo(String token, String userId, String videoId, JsonObject editedVideo) {
        return webServiceAPI.updateVideo(token, userId, videoId, editedVideo);
    }
    public Call<JsonObject> uploadVideo(String token, String userId, MultipartBody.Part video, RequestBody title, RequestBody description) {
        return webServiceAPI.uploadVideo(token, userId, video, title, description);

    }
}


