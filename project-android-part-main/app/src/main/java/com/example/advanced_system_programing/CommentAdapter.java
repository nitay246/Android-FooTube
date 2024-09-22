package com.example.advanced_system_programing;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> comments; // List of comments
    private String currentUserId; // ID of the current user

    // Constructor for CommentAdapter
    public CommentAdapter(List<Comment> comments, String currentUserId) {
        this.comments = comments;
        this.currentUserId = currentUserId;
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
        Comment comment = comments.get(position); // Get comment at current position
        holder.userTextView.setText(comment.getUserId()); // Set user ID

        // Check if the comment is new and has the placeholder text
        boolean isPlaceholder = comment.getText().isEmpty() || comment.getText().equals("press the edit and write your comment...");
        holder.commentTextView.setText(isPlaceholder ? "press the edit and write your comment..." : comment.getText());
        holder.commentTextView.setTextColor(isPlaceholder ? Color.parseColor("#A9A9A9") : Color.BLACK);

        // Check if the comment is from the current user
        boolean isCurrentUser = comment.getUserId().equals(currentUserId);
        holder.editCommentButton.setVisibility(isCurrentUser ? View.VISIBLE : View.GONE);
        holder.deleteCommentButton.setVisibility(isCurrentUser ? View.VISIBLE : View.GONE);

        // Set OnClickListener for edit button
        holder.editCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show EditText and Save button, hide TextView, Edit and Delete buttons
                holder.commentTextView.setVisibility(View.GONE);
                holder.commentEditText.setVisibility(View.VISIBLE);
                holder.saveCommentButton.setVisibility(View.VISIBLE);
                holder.editCommentButton.setVisibility(View.GONE);
                holder.deleteCommentButton.setVisibility(View.GONE);
                holder.commentEditText.setText(comment.getText().equals("press the edit and write your comment...") ? "" : comment.getText());
            }
        });

        // Set OnClickListener for delete button
        holder.deleteCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle delete comment
                comments.remove(position); // Remove comment from list
                notifyItemRemoved(position); // Notify adapter about item removed
                notifyItemRangeChanged(position, comments.size()); // Notify adapter about range change
            }
        });

        // Set OnClickListener for save button
        holder.saveCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the edited comment
                String updatedComment = holder.commentEditText.getText().toString();
                comment.setText(updatedComment); // Update comment text
                holder.commentTextView.setText(updatedComment); // Set updated text

                // Hide EditText and Save button, show TextView, Edit and Delete buttons
                holder.commentTextView.setVisibility(View.VISIBLE);
                holder.commentEditText.setVisibility(View.GONE);
                holder.saveCommentButton.setVisibility(View.GONE);
                holder.editCommentButton.setVisibility(View.VISIBLE);
                holder.deleteCommentButton.setVisibility(View.VISIBLE);

                // Remove the placeholder text color if the comment is not empty
                holder.commentTextView.setTextColor(updatedComment.isEmpty() ? Color.parseColor("#A9A9A9") : Color.BLACK);
            }
        });
    }

    // Return the total number of comments
    @Override
    public int getItemCount() {
        return comments.size();
    }

    // Notify adapter about item change
    public void editComment(int position) {
        notifyItemChanged(position);
    }

    // ViewHolder class for comment item
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView userTextView; // TextView for user ID
        TextView commentTextView; // TextView for comment text
        EditText commentEditText; // EditText for editing comment
        Button editCommentButton; // Button for editing comment
        Button deleteCommentButton; // Button for deleting comment
        Button saveCommentButton; // Button for saving edited comment

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userTextView = itemView.findViewById(R.id.userTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            commentEditText = itemView.findViewById(R.id.commentEditText);
            editCommentButton = itemView.findViewById(R.id.editCommentButton);
            deleteCommentButton = itemView.findViewById(R.id.deleteCommentButton);
            saveCommentButton = itemView.findViewById(R.id.saveCommentButton);
        }
    }
}
