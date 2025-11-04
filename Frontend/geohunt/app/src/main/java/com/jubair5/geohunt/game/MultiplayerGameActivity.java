package com.jubair5.geohunt.game;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.R;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class MultiplayerGameActivity extends GameActivity {

    private static final String TAG = "MultiplayerGameActivity";
    private WebSocketClient webSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.multiplayer_game_activity);
//        setContentView(R.layout.multiplayer_game_activity);
    }

    @Override
    protected void readyUp() {
        Log.d(TAG, "Ready button clicked. Beginning ready sequence for multiplayer.");
        super.readyUp(); // Sets currentLat and currentLng
        createWebSocketClient();
    }

    private void createWebSocketClient() {
        URI uri;
        try {
            // Construct the WebSocket URI
            uri = new URI(ApiConstants.WEBSOCKET_URL + "?lat=" + currentLat + "&lng=" + currentLng + "&radius=" + radius);
        } catch (URISyntaxException e) {
            Log.e(TAG, "URISyntaxException: " + e.getMessage());
            Toast.makeText(this, "Error connecting to the server.", Toast.LENGTH_SHORT).show();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i(TAG, "WebSocket connection opened.");
                runOnUiThread(() -> Toast.makeText(MultiplayerGameActivity.this, "Connected to multiplayer server!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onMessage(String message) {
                Log.i(TAG, "Received message: " + message);
                runOnUiThread(() -> {
                    try {
                        JSONObject response = new JSONObject(message);
                        int id = response.getInt("id");
                        String imageUrl = response.getString("streetviewurl");
                        startGame(id, imageUrl);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing game start response from WebSocket", e);
                        Toast.makeText(MultiplayerGameActivity.this, "Invalid response from server.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i(TAG, "WebSocket connection closed: " + reason);
                runOnUiThread(() -> Toast.makeText(MultiplayerGameActivity.this, "Disconnected from multiplayer server.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "WebSocket error: " + ex.getMessage());
                runOnUiThread(() -> Toast.makeText(MultiplayerGameActivity.this, "WebSocket error. See logs.", Toast.LENGTH_SHORT).show());
            }
        };

        webSocketClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}
