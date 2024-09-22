package com.example.advanced_system_programing;

import static api.TokenValidator.decodeTokenAndGetUserDisplayname;
import static api.TokenValidator.decodeTokenAndGetUserId;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.jwt.JWT;
import com.google.gson.JsonObject;

import java.util.List;

import api.VideoAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> comments; // List of comments
    private String currentUserjwt; // ID of the current user
    private String videoId; // ID of the video
    private VideoAPI videoAPI;
    private boolean userConnected;

    // Constructor for CommentAdapter
    public CommentAdapter(List<Comment> comments, String currentUserjwt, String videoId) {
        this.comments = comments;
        this.currentUserjwt = currentUserjwt;
        this.videoId = videoId;
        this.videoAPI = new VideoAPI();

        if (currentUserjwt != null) {
            addEmptyCommentAtBeginning();
        }
    }

    // Add an empty comment at the beginning of the list
    private void addEmptyCommentAtBeginning() {
        this.comments.add(0, new Comment(decodeTokenAndGetUserId(currentUserjwt), "press the edit and write your comment...", decodeTokenAndGetUserDisplayname(currentUserjwt)));
    }

    // Create ViewHolder for each comment item
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    // Bind data to the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Log.d("CommentsAdapter", comments.toString());
        Log.d("CommentsAdapter", "user id " + comments.get(position).getUserId());

        Comment comment = comments.get(holder.getAdapterPosition()); // Get comment at current position
        if (comment != null) {
            holder.userTextView.setText(comment.getUser()); // Set user displayname
            holder.userTextView.setOnClickListener(v -> {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(context, MyVideos.class);
                intent.putExtra("userId", comment.getUserId()); // Pass the user ID to the My Videos activity
                context.startActivity(intent);
            });
            // Check if the comment text is null or empty and handle it
            String commentText = comment.getText();
            boolean isPlaceholder = (commentText == null || commentText.isEmpty() || commentText.equals("press the edit and write your comment..."));
            holder.commentTextView.setText(isPlaceholder ? "press the edit and write your comment..." : commentText);
            holder.commentTextView.setTextColor(isPlaceholder ? Color.parseColor("#A9A9A9") : Color.BLACK);

            if (currentUserjwt != null) {

                Log.d("CommentAdapter", "currentUserjwt " + currentUserjwt);
                Log.d("CommentAdapter", "comment.userID " + comment.getUserId());
                // Check if the comment is from the current user
                boolean isCurrentUser = comment.getUserId().equals(decodeTokenAndGetUserId(currentUserjwt));
                Log.d("CommentAdapter", "isCurrentuser " + isCurrentUser);
                Log.d("CommentAdapter", "isCurrentuser " + comment.getUserId());
                Log.d("CommentAdapter", "isCurrentuser " + decodeTokenAndGetUserId(currentUserjwt));
                holder.editCommentButton.setVisibility(isCurrentUser && !isPlaceholder ? View.VISIBLE : View.GONE);
                holder.deleteCommentButton.setVisibility(isCurrentUser && !isPlaceholder ? View.VISIBLE : View.GONE);
            } else {
                holder.editCommentButton.setVisibility(View.GONE);
                holder.deleteCommentButton.setVisibility(View.GONE);
            }

            // Show the Submit button and EditText for the new comment
            if (isPlaceholder && currentUserjwt != null) {
                holder.commentTextView.setVisibility(View.GONE);
                holder.commentEditText.setVisibility(View.VISIBLE);
                holder.saveCommentButton.setVisibility(View.GONE);
                holder.submitCommentButton.setVisibility(View.VISIBLE);
            } else {
                holder.submitCommentButton.setVisibility(View.GONE);
            }
        }

        // Set OnClickListener for edit button
        holder.editCommentButton.setOnClickListener(v -> {
            // Show EditText and Save button, hide TextView, Edit and Delete buttons
            holder.commentTextView.setVisibility(View.GONE);
            holder.commentEditText.setVisibility(View.VISIBLE);
            holder.saveCommentButton.setVisibility(View.VISIBLE);
            holder.editCommentButton.setVisibility(View.GONE);
            holder.deleteCommentButton.setVisibility(View.GONE);
            assert comment != null;
            holder.commentEditText.setText(comment.getText().equals("press the edit and write your comment...") ? "" : comment.getText());
        });

        // Set OnClickListener for delete button
        holder.deleteCommentButton.setOnClickListener(v -> {
            Log.d("CommentAdapter", "position of deleting" + position);
            deleteCommentFromServer(holder.getAdapterPosition());
        });

        // Set OnClickListener for save button
        holder.saveCommentButton.setOnClickListener(v -> {
            // Save the edited comment
            String updatedComment = holder.commentEditText.getText().toString();

            String jwtToken = "Bearer " + currentUserjwt;
            JsonObject commentJson = new JsonObject();
            commentJson.addProperty("text", updatedComment);

            int place;
            if(holder.getAdapterPosition()==0) {
                 place=0;
            }
            else{
                place=holder.getAdapterPosition()-1;
            }

            Log.d("CommentAdapter", "place " + place);
            Call<JsonObject> call = videoAPI.editComment(jwtToken, videoId, place , commentJson);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        Log.d("CommentAdapter", "Comment edited successfully");
                        // Handle the successful response
                        JsonObject responseBody = response.body();
                        // Process the response
                        assert comment != null;
                        assert responseBody != null;
                        comment.setText(responseBody.get("text").getAsString()); // Update comment text
                        holder.commentTextView.setText(responseBody.get("text").getAsString()); // Set updated text

                        // Hide EditText and Save button, show TextView, Edit and Delete buttons
                        holder.commentTextView.setVisibility(View.VISIBLE);
                        holder.commentEditText.setVisibility(View.GONE);
                        holder.saveCommentButton.setVisibility(View.GONE);
                        holder.editCommentButton.setVisibility(View.VISIBLE);
                        holder.deleteCommentButton.setVisibility(View.VISIBLE);
                    } else {
                        // Handle the error
                        Log.d("CommentAdapter", "Comment unsuccessfully edited");
                        Log.d("CommentAdapter", response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    // Handle the failure
                }
            });

            // Remove the placeholder text color if the comment is not empty
            holder.commentTextView.setTextColor(updatedComment.isEmpty() ? Color.parseColor("#A9A9A9") : Color.BLACK);
        });

        // Set OnClickListener for submit button
        holder.submitCommentButton.setOnClickListener(v -> {
            // Save the new comment
            String newCommentText = holder.commentEditText.getText().toString();

            String jwtToken = "Bearer " + currentUserjwt;
            JsonObject commentJson = new JsonObject();
            commentJson.addProperty("text", newCommentText);

            Call<JsonObject> call = videoAPI.createComment(jwtToken, commentJson, videoId);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        Log.d("CommentAdapter", "Comment created successfully");
                        // Handle the successful response
                        JsonObject responseBody = response.body();
                        String userDisplayname = responseBody.get("user").getAsString();
                        // Process the response
                        assert comment != null;
                        assert responseBody != null;
                        comment.setText(responseBody.get("text").getAsString()); // Update comment text
                        holder.commentTextView.setText(responseBody.get("text").getAsString()); // Set updated text

                        Log.d("CommentAdapter", "comment user" + decodeTokenAndGetUserDisplayname(currentUserjwt));
                        holder.userTextView.setText(userDisplayname); // Set user displayname
                        holder.commentTextView.setTextColor(Color.BLACK);

                        // Hide EditText and Submit button, show TextView, Edit and Delete buttons
                        holder.commentTextView.setVisibility(View.VISIBLE);
                        holder.commentEditText.setVisibility(View.GONE);
                        holder.submitCommentButton.setVisibility(View.GONE);
                        holder.editCommentButton.setVisibility(View.VISIBLE);
                        holder.deleteCommentButton.setVisibility(View.VISIBLE);
                        addEmptyCommentAtBeginning();
                        notifyItemInserted(0);



                    } else {
                        // Handle the error
                        Log.d("CommentAdapter", "Comment unsuccessfully created");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    // Handle the failure
                }
            });


        });
        // Inside onBindViewHolder method
        holder.userTextView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, MyVideos.class);
            intent.putExtra("userID", comments.get(position).getUserId()); // Pass the user ID to the My Videos activity
            context.startActivity(intent);
        });

    }


    private void deleteCommentFromServer(int position) {
        Call<List<Comment>> call = videoAPI.deleteComment(currentUserjwt, videoId, position - 1);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Comment>> call, @NonNull Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    comments.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, comments.size());
                } else {
                    Log.e("CommentAdapter", "Delete request failed: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Comment>> call, @NonNull Throwable t) {
                Log.e("CommentAdapter", "Delete request failed: " + t.getMessage());
            }
        });
    }

    // Return the total number of comments
    @Override
    public int getItemCount() {
        return comments.size();
    }

    // ViewHolder class for comment item
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView userTextView; // TextView for user ID
        TextView commentTextView; // TextView for comment text
        EditText commentEditText; // EditText for editing comment
        Button editCommentButton; // Button for editing comment
        Button deleteCommentButton; // Button for deleting comment
        Button saveCommentButton; // Button for saving edited comment
        Button submitCommentButton; // Button for submitting new comment

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userTextView = itemView.findViewById(R.id.userTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            commentEditText = itemView.findViewById(R.id.commentEditText);
            editCommentButton = itemView.findViewById(R.id.editCommentButton);
            deleteCommentButton = itemView.findViewById(R.id.deleteCommentButton);
            saveCommentButton = itemView.findViewById(R.id.saveCommentButton);
            submitCommentButton = itemView.findViewById(R.id.submitCommentButton);
        }
    }
}


