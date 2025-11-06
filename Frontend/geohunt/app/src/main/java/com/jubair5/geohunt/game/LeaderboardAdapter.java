/**
 * Handles displaying players on the leaderboard
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.game;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jubair5.geohunt.R;

import java.util.List;
import java.util.Locale;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private List<LeaderboardItem> leaderboardItems;

    public LeaderboardAdapter(List<LeaderboardItem> leaderboardItems) {
        this.leaderboardItems = leaderboardItems;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        LeaderboardItem item = leaderboardItems.get(position);
        holder.usernameTextView.setText(item.getUsername());
        holder.distanceTextView.setText(String.format(Locale.getDefault(), "%.2f miles", item.getDistance()));
    }

    @Override
    public int getItemCount() {
        return leaderboardItems.size();
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView distanceTextView;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username_text_view);
            distanceTextView = itemView.findViewById(R.id.distance_text_view);
        }
    }
}
