package com.jubair5.geohunt.game;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.jubair5.geohunt.R;

/**
 * Activity for the multiplayer game screen. This activity extends the base GameActivity
 * but modifies the startup flow to work with multiplayer lobbies. It receives the challenge
 * details from the LobbyActivity and immediately starts the game countdown.
 */
public class MultiplayerGameActivity extends GameActivity {

    private static final String TAG = "MultiplayerGameActivity";
    private int multiplayerChallengeId;
    private String streetViewUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Use the custom onCreate from GameActivity to set the correct layout and run setup
        super.onCreate(savedInstanceState, R.layout.multiplayer_game_activity);

        // Get challenge info from LobbyActivity's intent
        Intent intent = getIntent();
        multiplayerChallengeId = intent.getIntExtra("challengeId", -1);
        streetViewUrl = intent.getStringExtra("imageUrl");

        // Validate that we have the necessary data to start
        if (multiplayerChallengeId == -1 || streetViewUrl == null) {
            Toast.makeText(this, "Error: Missing multiplayer game data.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Missing challengeId or imageUrl in intent.");
            finish();
            return;
        }

        // This will call our overridden readyUp() method to start the game flow.
        readyUp();
    }

    /**
     * Overrides the default ready-up behavior for multiplayer.
     * Instead of fetching a new location from the server, it immediately starts the game
     * using the challenge details passed from the lobby.
     */
    @Override
    protected void readyUp() {
        Log.d(TAG, "Bypassing single-player ready-up. Starting game with provided challenge.");
        // We already have the challenge details, so we can skip the server request
        // and go straight to the startGame method.

        // Get the most current location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission is required to start the game.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat, currentLng), 18f));
        }

        startGame(multiplayerChallengeId, streetViewUrl);
    }
}
