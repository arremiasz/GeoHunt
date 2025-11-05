/**
 * Activity to handle creating a lobby using websockets to setup a multiplayer match
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.Slider;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends AppCompatActivity {

    private static final String TAG = "LobbyActivity";
    // Set to true to simulate server responses and test UI
    private static final boolean MOCK_MODE = true;

    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_ID = "userId";
    private WebSocketClient webSocketClient;
    private PlayerAdapter playerAdapter;
    private List<Player> playerList = new ArrayList<>();
    private Button startGameButton;
    private Slider radiusSlider;

    private String lobbyId;
    private int userId;
    private String username;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_activity);

        RecyclerView playerRecyclerView = findViewById(R.id.player_recycler_view);
        playerRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        playerAdapter = new PlayerAdapter(playerList);
        playerRecyclerView.setAdapter(playerAdapter);
        prefs = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        // Get lobbyId from intent (if joining) and user info from SharedPreferences
        lobbyId = getIntent().getStringExtra("lobbyId");
        userId = prefs.getInt(KEY_USER_ID, -1);
        username = prefs.getString(KEY_USER_NAME, "User");

        radiusSlider = findViewById(R.id.radius_slider);
        startGameButton = findViewById(R.id.start_game_button);

        Button inviteButton = findViewById(R.id.invite_button);
        inviteButton.setOnClickListener(v -> inviteFriends());

        startGameButton.setOnClickListener(v -> sendLobbyMessage("start")); // Updated to match server

        if (MOCK_MODE) {
            setupMockMode();
        } else {
            createWebSocketClient();
        }
    }

    private void createWebSocketClient() {
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
            }

            @Override
            public void onMessage(String message) {
                Log.i(TAG, "Received message: " + message);
                runOnUiThread(() -> handleWebSocketMessage(message));
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i(TAG, "WebSocket connection closed: " + reason);
                runOnUiThread(() -> Toast.makeText(LobbyActivity.this, "Disconnected: " + reason, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "WebSocket error: " + ex.getMessage());
                runOnUiThread(() -> Toast.makeText(LobbyActivity.this, "WebSocket error. See logs.", Toast.LENGTH_SHORT).show());
            }
        };

        webSocketClient.connect();
    }

    private void handleWebSocketMessage(String message) {
        if (message.equals("Connection Success")) {
            Toast.makeText(this, "Connected to server!", Toast.LENGTH_SHORT).show();
            // If lobbyId is null from the intent, create a new lobby. Otherwise, join.
            if (lobbyId == null) {
                sendLobbyMessage("create");
            } else {
                sendLobbyMessage("join " + lobbyId);
            }
        } else if (message.startsWith("Successfully created lobby")) {
            String[] parts = message.split("\\s+");
            this.lobbyId = parts[parts.length - 1];
            Toast.makeText(this, "Lobby created! ID: " + this.lobbyId, Toast.LENGTH_SHORT).show();
            setLobbyLeader(true);
            // Add self to player list
            playerList.clear();
            playerList.add(new Player(String.valueOf(userId), username));
            playerAdapter.notifyDataSetChanged();
        } else if (message.startsWith("User ") && message.endsWith(" joined the lobby")) {
            // e.g., "User Player2 joined the lobby"
            String joinedUser = message.substring(5, message.indexOf(" joined the lobby"));
            Toast.makeText(this, joinedUser + " has joined!", Toast.LENGTH_SHORT).show();
            // We need a way to get the user ID for the new player, for now, we use a placeholder
            playerList.add(new Player("-1", joinedUser));
            playerAdapter.notifyDataSetChanged();
        } else if (message.equals("cannot join lobby")){
            Toast.makeText(this, "Failed to join lobby. It might be full or invalid.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void sendLobbyMessage(String message) {
        if (MOCK_MODE) {
            Log.d(TAG, "Mock mode: Pretending to send message: " + message);
            return; // In mock mode, we don't send real messages
        }

        if (webSocketClient != null && webSocketClient.isOpen()) {
            Log.d(TAG, "Sending message: " + message);
            webSocketClient.send(message);
        } else {
            Toast.makeText(this, "Not connected to the server.", Toast.LENGTH_SHORT).show();
        }
    }

    private void inviteFriends() {
        if (lobbyId != null) {
            // TODO: Implement a way for the user to share the lobbyId
            Toast.makeText(this, "Share this lobby ID with your friends: " + lobbyId, Toast.LENGTH_LONG).show();
        } else if (MOCK_MODE) {
            Toast.makeText(this, "Share this lobby ID with your friends: mockLobby123", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "You must be in a lobby to invite friends.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    private void setLobbyLeader(boolean isLeader) {
        startGameButton.setEnabled(isLeader);
        radiusSlider.setEnabled(isLeader);
    }
    // --- Mock Mode Methods (kept for reference) ---

    private void setupMockMode() {
        Log.d(TAG, "Running in Mock Mode");
        playerList.clear();
        playerList.add(new Player(String.valueOf(userId), username));
        playerList.add(new Player("2", "Player 2"));
        playerList.add(new Player("3", "Player 3"));
        playerList.add(new Player("4", "Player 4"));
        playerList.add(new Player("5", "Player 5"));
        playerList.add(new Player("6", "Player 6"));
        playerList.add(new Player("7", "Player 7"));
        playerList.add(new Player("8", "Player 8"));
        playerAdapter.notifyDataSetChanged();
        setLobbyLeader(true);
    }

    // Not used, but kept for future implementation
    private void launchGame(JSONObject gameState) throws JSONException {}

    private void updateLobbyState(JSONObject lobbyState) throws JSONException {}
}
