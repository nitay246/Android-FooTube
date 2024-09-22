package repository;



import static com.example.advanced_system_programing.MyApplication.context;

import android.app.Application;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import androidx.room.Room;

import com.auth0.android.jwt.JWT;
import com.example.advanced_system_programing.AppDatabase;
import com.example.advanced_system_programing.FileUtils;

import com.example.advanced_system_programing.Video;
import com.example.advanced_system_programing.VideoDao;

import com.google.gson.JsonObject;



import java.io.File;
import java.util.List;

import api.UserAPI;
import api.VideoAPI;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoRepository extends AppCompatActivity {

    private VideoDao videoDao;
    private LiveData<List<Video>> allVideos;
    private VideoAPI videoAPI;
    private UserAPI userAPI;


    public VideoRepository(Application application) {
        AppDatabase db = Room.databaseBuilder(application, AppDatabase.class, "app_database")
                .build();
        videoDao = db.videoDao();
        allVideos = videoDao.getAllVideos();
        videoAPI = new VideoAPI();
        userAPI = new UserAPI();
    }


    public LiveData<List<Video>> getAllVideos() {
        return allVideos;
    }

    public LiveData<List<Video>> getVideosByUserId(String userId) {

        return videoDao.getVideosByUserId(userId);
    }

    public void insert(Video video) {
        new InsertAsyncTask(videoDao).execute(video);
    }


    public void fetchVideosFromServer() {
        Log.d("VideoRepository", "Fetching videos from server");
        Call<List<Video>> call = videoAPI.getVideos();
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(@NonNull Call<List<Video>> call, @NonNull Response<List<Video>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("VideoRepository", response.message());
                    new InsertVideosAsyncTask(videoDao).execute(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Video>> call, @NonNull Throwable t) {
                // Handle failure
            }
        });
    }

    public void clearAllVideos() {
        new ClearVideosAsyncTask(videoDao).execute();
    }

    private static class ClearVideosAsyncTask extends AsyncTask<Void, Void, Void> {
        private VideoDao videoDao;

        ClearVideosAsyncTask(VideoDao dao) {
            videoDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            videoDao.clearAllVideos();
            return null;
        }
    }

    public void fetchVideosForUser(String userId) {
        Call<List<Video>> call = userAPI.getUserVideos(userId);
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, @NonNull Response<List<Video>> response) {
                Log.d("VideoRepository", "request to the server has been made");
                if (response.isSuccessful() && response.body() != null) {
                    new InsertVideosAsyncTask(videoDao).execute(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                // Handle failure
            }
        });
    }


    public void deleteVideoFromServer(String token,String userId,String videoId){
        videoAPI.deleteVideo(token,userId,videoId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("Edit_activity","Video deleted successfully");
                    deleteVideo(videoId);
                }else{
                    Log.d("Edit_activity","Failed to delete video");
                    Log.d("Edit_activity",response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Edit_activity","onFailure: "+t.getMessage());
            }
        });

    }
    public void updateVideoOnServer(String token, String userId, String videoId, JsonObject editedVideo){
        videoAPI.updateVideo(token,userId,videoId,editedVideo).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){
                    Log.d("Edit_activity","Video updated successfully");
                    updateVideo(videoId,editedVideo.get("title").getAsString(),editedVideo.get("description").getAsString());
                }else{

                    Log.d("Edit_activity","Failed to update video");
                    Log.d("Edit_activity",response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Edit_activity","onFailure: "+t.getMessage());
            }
        });

    }
    // Method to upload video to server
    public void uploadVideoToServer(String title, String description, Uri videoUri, String jwtToken) {
        File filePath = FileUtils.copyFileFromUri(context, videoUri);
        if (filePath == null) {
            Log.d("upload_activity", "File path is null");
            Toast.makeText(this, "Failed to get file path from URI", Toast.LENGTH_SHORT).show();
            return;
        }


        RequestBody requestBody = RequestBody.create(MediaType.parse("video/*"),filePath);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", filePath.getName(), requestBody);

        // Create RequestBody for title and description
        RequestBody titlePart = RequestBody.create(MultipartBody.FORM, title);
        RequestBody descriptionPart = RequestBody.create(MultipartBody.FORM, description);


        Call<JsonObject> call = videoAPI.uploadVideo(jwtToken, decodeTokenAndGetUserId(jwtToken), body, titlePart,descriptionPart);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("upload_activity", "Response: " + response.message());
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject videoJson = response.body();

                    // Parse the JsonObject to extract video details
                    String id = videoJson.get("_id").getAsString();
                    String title = videoJson.get("title").getAsString();
                    String description = videoJson.get("description").getAsString();
                    String source = videoJson.get("source").getAsString();

                    // Create a Video object from the JSON response
                    Video videoFromServer = new Video();
                    videoFromServer.setId(id);
                    videoFromServer.setTitle(title);
                    videoFromServer.setDescription(description);
                    videoFromServer.setSource(source);
                    // Insert the video into the Room database
                    insert(videoFromServer);

                } else {
                    Log.e("VideosViewModel", "Failed to create video on server");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("VideosViewModel", "Failed to create video on server: " + t.getMessage());
            }
        });

    }
    // Method to decode the token and get the user ID
    // Method to decode the token and get the user ID
    private String decodeTokenAndGetUserId(String token) {
        JWT jwt = new JWT(token);
        return jwt.getClaim("id").asString();
    }

















    private static class InsertAsyncTask extends AsyncTask<Video, Void, Void> {
        private VideoDao videoDao;

        InsertAsyncTask(VideoDao dao) {
            videoDao = dao;
        }

        @Override
        protected Void doInBackground(Video... videos) {
            videoDao.insertVideo(videos[0]);
            Log.d("InsertAsyncTask", "Inserted video into Room");
            Log.d("InsertAsyncTask", videos[0].toString());
            return null;
        }
    }

    private static class InsertVideosAsyncTask extends AsyncTask<List<Video>, Void, Void> {
        private VideoDao videoDao;

        InsertVideosAsyncTask(VideoDao dao) {
            videoDao = dao;
        }

        @Override
        protected Void doInBackground(List<Video>... videos) {
            videoDao.insertAll(videos[0]);
            Log.d("InsertVideosAsyncTask", "Inserted " + videos[0].size() + " videos into Room");
            return null;
        }
    }
    public void updateVideo(String id, String title, String description) {
        new UpdateVideoByIdAsyncTask(videoDao, id, title, description).execute();
    }

    public void deleteVideo(String id) {
        new DeleteVideoByIdAsyncTask(videoDao, id).execute();
    }

    private static class UpdateVideoByIdAsyncTask extends AsyncTask<Void, Void, Void> {
        private VideoDao videoDao;
        private String id;
        private String title;
        private String description;

        UpdateVideoByIdAsyncTask(VideoDao dao, String id, String title, String description) {
            videoDao = dao;
            this.id = id;
            this.title = title;
            this.description = description;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            videoDao.updateVideo(id, title, description);
            return null;
        }
    }

    private static class DeleteVideoByIdAsyncTask extends AsyncTask<Void, Void, Void> {
        private VideoDao videoDao;
        private String id;

        DeleteVideoByIdAsyncTask(VideoDao dao, String id) {
            videoDao = dao;
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            videoDao.deleteVideo(id);
            return null;
        }
    }
}
