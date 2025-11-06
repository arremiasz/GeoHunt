/**
 * Activity that handles displaying results for multiplayer matches
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.game;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jubair5.geohunt.R;
import java.util.ArrayList;
import java.util.List;

public class MultiplayerResultsActivity extends ResultsActivity {

    private RecyclerView leaderboardRecyclerView;
    private LeaderboardAdapter leaderboardAdapter;
    private List<LeaderboardItem> leaderboardItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplayer_results_activity);

        // The parent class's `onCreate` is not called, so we must call its setup methods manually.
        setupViews();
        displayResults();
        setupButtons();
        startConfetti();

        // Setup the leaderboard
        leaderboardRecyclerView = findViewById(R.id.leaderboard_recycler_view);
        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        leaderboardAdapter = new LeaderboardAdapter(leaderboardItems);
        leaderboardRecyclerView.setAdapter(leaderboardAdapter);

        // Load mock data for the leaderboard
        loadLeaderboardData();
    }

    private void loadLeaderboardData() {
        // In the future, this data will come from a WebSocket message or API call
        leaderboardItems.add(new LeaderboardItem("Player 1", 0.25));
        leaderboardItems.add(new LeaderboardItem("Player 2", 0.50));
        leaderboardItems.add(new LeaderboardItem("Player 3", 1.75));
        leaderboardAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPlayAgainClicked() {
        // For multiplayer, "Play Again" should return to the lobby, not start a new game.
        // We will implement this logic later.
    }
}
