/**
 * Activity that handles displaying results for multiplayer matches
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiplayerResultsActivity extends ResultsActivity {

    private static final String TAG = "MultiplayerResults";
    private RecyclerView leaderboardRecyclerView;
    private LeaderboardAdapter leaderboardAdapter;
    private List<LeaderboardItem> leaderboardItems = new ArrayList<>();

    private WebSocketClient webSocketClient;
    private String lobbyId;
    private String username;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplayer_results_activity);

        prefs = getSharedPreferences("GeoHuntPrefs", Context.MODE_PRIVATE);
        username = prefs.getString("userName", "User");
        lobbyId = getIntent().getStringExtra("lobbyId");

        // Manually call the setup methods from the parent class.
        setupViews();
        displayResults();
        setupButtons();
        startConfetti();

        // Setup the leaderboard
        leaderboardRecyclerView = findViewById(R.id.leaderboard_recycler_view);
        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        leaderboardAdapter = new LeaderboardAdapter(leaderboardItems);
        leaderboardRecyclerView.setAdapter(leaderboardAdapter);

        if (lobbyId == null) {
            Toast.makeText(this, "Error: Lobby ID not found.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        createWebSocketClient();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            Log.d(TAG, "Activity is finishing, sending 'leave' message and closing WebSocket.");
            sendLobbyMessage("leave");
            if (webSocketClient != null) {
                webSocketClient.close();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null && !webSocketClient.isClosed()) {
            webSocketClient.close();
        }
    }

    /**
     * Establishes a connection to the WebSocket server for the current lobby.
     */
    private void createWebSocketClient() {
        if (webSocketClient != null && !webSocketClient.isClosed()) {
            Log.d(TAG, "WebSocket client already connected or connecting.");
            return;
        }

        URI uri;
        try {
            String url = ApiConstants.WEBSOCKET_URL + "/multiplayer/" + username;
            Log.d(TAG, "Connecting to WebSocket: " + url);
            uri = new URI(url);
        } catch (URISyntaxException e) {
            Log.e(TAG, "URISyntaxException: " + e.getMessage());
            Toast.makeText(this, "Error connecting to the server.", Toast.LENGTH_SHORT).show();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i(TAG, "WebSocket connection opened.");
                sendLobbyMessage("join " + lobbyId);
                sendLobbyMessage("get_results");
            }

            @Override
            public void onMessage(String message) {
                Log.i(TAG, "Received message: " + message);
                runOnUiThread(() -> handleWebSocketMessage(message));
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i(TAG, "WebSocket connection closed: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "WebSocket error: " + ex.getMessage());
            }
        };
        webSocketClient.setConnectionLostTimeout(60);
        webSocketClient.connect();
    }

    /**
     * Handles incoming WebSocket messages, specifically for parsing leaderboard results.
     * @param message The message from the server.
     */
    private void handleWebSocketMessage(String message) {
        if (message.startsWith("LEADERBOARD_UPDATE ")) {
            try {
                String jsonString = message.substring("LEADERBOARD_UPDATE ".length());
                JSONObject resultData = new JSONObject(jsonString);
                String stringAsJson = message.substring(19);
                String updatedUser = resultData.getString("username");
                double distance = Double.parseDouble(resultData.getString("result"));

                boolean userExists = false;
                for (LeaderboardItem item : leaderboardItems) {
                    if (item.getUsername().equals(updatedUser)) {
                        userExists = true;
                        break;
                    }
                }

                if (!userExists) {
                    leaderboardItems.add(new LeaderboardItem(updatedUser, distance));
                    Collections.sort(leaderboardItems, (o1, o2) -> Double.compare(o1.getDistance(), o2.getDistance()));
                    leaderboardAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Leaderboard updated for user: " + updatedUser);
                }
            } catch (JSONException | NumberFormatException e) {
                Log.e(TAG, "Failed to parse LEADERBOARD_UPDATE message: " + message, e);
            }
        } else {
            // Assume this is the initial full list of results
            try {
                JSONArray resultsArray = new JSONArray(message);
                leaderboardItems.clear();
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject result = resultsArray.getJSONObject(i);
                    String user = result.getString("username");
                    double distance = result.getDouble("distance");
                    leaderboardItems.add(new LeaderboardItem(user, distance));
                }
                Collections.sort(leaderboardItems, (o1, o2) -> Double.compare(o1.getDistance(), o2.getDistance()));
                leaderboardAdapter.notifyDataSetChanged();
                Log.d(TAG, "Leaderboard updated with full list.");
            } catch (JSONException e) {
                Log.w(TAG, "Received unhandled WebSocket message: " + message);
            }
        }
    }

    /**
     * Sends a message to the lobby's WebSocket connection.
     * @param message The message to send.
     */
    private void sendLobbyMessage(String message) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            Log.d(TAG, "Sending message: " + message);
            webSocketClient.send(message);
        } else {
            Log.d(TAG, "Cannot send message, WebSocket is not open.");
        }
    }

    @Override
    protected void onPlayAgainClicked() {
        // For multiplayer, "Play Again" should return to the lobby.
        finish();
    }
}
