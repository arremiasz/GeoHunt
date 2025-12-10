package com.jubair5.geohunt.game;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jubair5.geohunt.R;

import java.util.List;

/**
 * Adapter for displaying comments in a RecyclerView.
 *
 * @author Alex Remiasz
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private final Context context;
    private final List<Comment> comments;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.username.setText(comment.getUsername());
        holder.commentText.setText(comment.getComment());
        holder.timestamp.setText(comment.getFormattedTimestamp());

        String photoUrl = comment.getProfilePhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            try {
                byte[] imageData = Base64.decode(photoUrl, Base64.DEFAULT);
                Glide.with(context)
                        .load(imageData)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(holder.profileImage);
            } catch (IllegalArgumentException e) {
                holder.profileImage.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            holder.profileImage.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView username;
        TextView timestamp;
        TextView commentText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.comment_profile_image);
            username = itemView.findViewById(R.id.comment_username);
            timestamp = itemView.findViewById(R.id.comment_timestamp);
            commentText = itemView.findViewById(R.id.comment_text);
        }
    }
}
