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
    private boolean isMultiplayerReady = false;

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

        // Set a flag that we are ready to start. The game will be started from our
        // overridden enableMyLocation() method, which is called by the parent's onMapReady().
        isMultiplayerReady = true;
    }

    /**
     * Intercepts the parent's setup flow. This method is called by onMapReady() in the parent class.
     * Instead of the default behavior, we use the challenge data we received from the intent
     * to start the game immediately after centering the camera on the user.
     */
    @Override
    protected void enableMyLocation() {
        if (isMultiplayerReady) {
            Log.d(TAG, "Map is ready, starting multiplayer game setup.");

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // If permission is not granted, we launch the permission request. The parent activity's
                // launcher will handle the result and call this method again.
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                return;
            }

            // Set the blue dot on the map
            googleMap.setMyLocationEnabled(true);

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    currentLat = location.getLatitude();
                    currentLng = location.getLongitude();
                    Log.d(TAG, "Initial location for multiplayer game: Lat: " + currentLat + ", Lng: " + currentLng);

                    // Animate camera to the user's location, now that the map is guaranteed to be ready.
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat, currentLng), 18f));

                    // Immediately start the game
                    startGame(multiplayerChallengeId, streetViewUrl);
                } else {
                    Log.e(TAG, "Could not get current location to start game.");
                    Toast.makeText(this, "Could not get current location. Please try again.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            // This case should not be hit in the multiplayer flow, but as a fallback, we call the parent.
            super.enableMyLocation();
        }
    }
}
