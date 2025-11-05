/**
 * Activity to handle creating a lobby using websockets to setup a multiplayer match
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.game;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.slider.Slider;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LobbyActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "LobbyActivity";
    // Set to true to simulate server responses and test UI
    private static final boolean MOCK_MODE = false;

    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_NAME = "userName";
    private WebSocketClient webSocketClient;
    private PlayerAdapter playerAdapter;
    private List<Player> playerList = new ArrayList<>();
    private Button startGameButton;
    private Slider radiusSlider;
    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Circle radiusCircle;
    private double radius = 1.0; // Default radius in miles
    private int strokeColor = Color.BLUE;
    private int fillColor = 0x220000FF;
    private LatLng lobbyCenter;
    private boolean userHasPanned = false;

    private String lobbyId;
    private String username;
    private SharedPreferences prefs;

    /**
     * This method handles the result of the location permission request.
     */
    private final ActivityResultLauncher<String> requestLocationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    Log.d(TAG, "Location permission granted.");
                    enableMyLocation();
                } else {
                    Log.w(TAG, "Location permission denied.");
                    Toast.makeText(this, "Location permission is required to show your position on the map.", Toast.LENGTH_LONG).show();
                }
            });

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
        username = prefs.getString(KEY_USER_NAME, "User");

        radiusSlider = findViewById(R.id.radius_slider);
        startGameButton = findViewById(R.id.start_game_button);
        mapView = findViewById(R.id.map_view_lobby);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        radiusSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                radius = value;
                updateCircleRadius(true);
            }
        });

        radiusSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {}

            @SuppressLint("RestrictedApi")
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                // Only send the update if the user is the lobby leader
                if (startGameButton.isEnabled()) {
                    sendLobbyMessage(String.format(Locale.US, "radius %.2f", slider.getValue()));
                }
            }
        });

        Button inviteButton = findViewById(R.id.invite_button);
        inviteButton.setOnClickListener(v -> inviteFriends());

        startGameButton.setOnClickListener(v -> {
            if (startGameButton.isEnabled()) {
                fetchChallengeAndBroadcast();
            }
        });

        if (MOCK_MODE) {
            setupMockMode();
        } else {
            createWebSocketClient();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        setMapStyle();
        enableMyLocation();

        googleMap.setOnCameraMoveListener(() -> {
            if (!startGameButton.isEnabled()) return;
            lobbyCenter = googleMap.getCameraPosition().target;
            updateCircleRadius(false);
        });

        googleMap.setOnCameraMoveStartedListener(reason -> {
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                userHasPanned = true;
            }
        });

        googleMap.setOnCameraIdleListener(() -> {
            if (!startGameButton.isEnabled()) return;
            lobbyCenter = googleMap.getCameraPosition().target;
            updateCircleRadius(false);
            sendLobbyMessage(String.format(Locale.US, "center %f %f", lobbyCenter.latitude, lobbyCenter.longitude));
        });
    }

    /**
     * Fetches a new challenge from the server and broadcasts its ID to the lobby.
     * This method should only be called by the lobby leader.
     */
    private void fetchChallengeAndBroadcast() {
        if (lobbyCenter == null) {
            Toast.makeText(this, "Please set a center for the game area.", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = Uri.parse(ApiConstants.BASE_URL + ApiConstants.GET_GENERATED_LOCATIONS_ENDPOINT)
                .buildUpon()
                .appendQueryParameter("lat", String.valueOf(lobbyCenter.latitude))
                .appendQueryParameter("lng", String.valueOf(lobbyCenter.longitude))
                .appendQueryParameter("radius", String.valueOf(radius))
                .build().toString();

        Log.d(TAG, "Requesting challenge from: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        int challengeId = response.getInt("id");
                        Log.d(TAG, "Fetched challenge ID: " + challengeId);
                        // Now broadcast this to all users via WebSocket
                        sendLobbyMessage("start " + challengeId);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing challenge response", e);
                        Toast.makeText(LobbyActivity.this, "Failed to parse challenge from server.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Failed to fetch challenge", error);
                    Toast.makeText(LobbyActivity.this, "Error fetching challenge from server.", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    /**
     * Fetches the full details of a challenge using its ID and launches the game activity.
     * @param challengeId The ID of the challenge to fetch.
     */
    private void fetchChallengeDetailsAndLaunch(String challengeId) {
        String url = Uri.parse(ApiConstants.BASE_URL + ApiConstants.GET_CHALLENGE_BY_ID_ENDPOINT)
                .buildUpon()
                .appendQueryParameter("id", challengeId)
                .build().toString();

        Log.d(TAG, "Requesting challenge details from: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        int id = response.getInt("id");
                        String imageUrl = response.getString("streetviewurl");
                        Log.d(TAG, "Successfully fetched challenge details. Launching game.");

                        // Launch the game activity
                        Intent intent = new Intent(LobbyActivity.this, MultiplayerGameActivity.class);
                        intent.putExtra("challengeId", id);
                        intent.putExtra("imageUrl", imageUrl);
                        startActivity(intent);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing challenge details response", e);
                        Toast.makeText(LobbyActivity.this, "Failed to parse game details.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Failed to fetch challenge details", error);
                    Toast.makeText(LobbyActivity.this, "Error fetching game details.", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    /**
     * Sets the map style based on the current system theme.
     */
    private void setMapStyle() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            Log.d(TAG, "Setting dark mode map style.");
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark));
            strokeColor = Color.rgb(0, 100, 255);
            fillColor = Color.argb(34, 0, 150, 255);
        }
    }

    /**
     * Checks for location permission, and if granted, enables the 'My Location' layer and
     * moves the camera to the user's current position.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (googleMap != null) {
                googleMap.setMyLocationEnabled(true);
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        lobbyCenter = currentLatLng;
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                        startLocationUpdates();
                    } else {
                        Log.w(TAG, "Could not get current location on map ready.");
                        Toast.makeText(this, "Could not get current location.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Starts requesting location updates.
     */
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(10000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (!userHasPanned) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                        updateCircleRadius(true);
                    }
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    /**
     * Updates the radius circle on the map to reflect the current search radius.
     * @param adjustCamera If true, the camera will be adjusted to fit the circle.
     */
    private void updateCircleRadius(boolean adjustCamera) {
        if (googleMap != null) {
            if (lobbyCenter == null) return;

            if (radiusCircle == null) {
                radiusCircle = googleMap.addCircle(new CircleOptions()
                        .center(lobbyCenter)
                        .radius(radius * 1609.34) // Convert miles to meters
                        .strokeColor(strokeColor)
                        .strokeWidth(2f)
                        .fillColor(fillColor));
            } else {
                radiusCircle.setCenter(lobbyCenter);
                radiusCircle.setRadius(radius * 1609.34);
            }

            if (adjustCamera) {
                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(getOffsetLatLng(lobbyCenter, radius * 1609.34, 0))
                        .include(getOffsetLatLng(lobbyCenter, radius * 1609.34, 90))
                        .include(getOffsetLatLng(lobbyCenter, radius * 1609.34, 180))
                        .include(getOffsetLatLng(lobbyCenter, radius * 1609.34, 270))
                        .build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }
    }

    /**
     * Calculates a new LatLng based on a starting point, a distance in meters, and a bearing.
     * @param latLng The starting LatLng.
     * @param distance The distance in meters.
     * @param bearing The bearing in degrees.
     * @return The new LatLng.
     */
    private LatLng getOffsetLatLng(LatLng latLng, double distance, double bearing) {
        double lat1 = Math.toRadians(latLng.latitude);
        double lon1 = Math.toRadians(latLng.longitude);
        double brng = Math.toRadians(bearing);
        double dR = distance / 6378137; // Earth's radius in meters

        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dR) + Math.cos(lat1) * Math.sin(dR) * Math.cos(brng));
        double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(dR) * Math.cos(lat1),
                Math.cos(dR) - Math.sin(lat1) * Math.sin(lat2));

        return new LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2));
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
                finish();
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "WebSocket error: " + ex.getMessage());
                runOnUiThread(() -> Toast.makeText(LobbyActivity.this, "WebSocket error. See logs.", Toast.LENGTH_SHORT).show());
                finish();
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
            playerList.add(new Player(username, getApplicationContext()));
            playerAdapter.notifyDataSetChanged();
        } else if (message.startsWith("Successfully joined lobby")) {
            Toast.makeText(this, "Successfully joined lobby!", Toast.LENGTH_SHORT).show();
            setLobbyLeader(false);
        } else if (message.startsWith("User ") && message.endsWith(" joined the lobby")) {
            // e.g., "User Player2 joined the lobby"
            String joinedUser = message.substring(5, message.indexOf(" joined the lobby"));
            Toast.makeText(this, joinedUser + " has joined!", Toast.LENGTH_SHORT).show();
            playerList.add(new Player(joinedUser, getApplicationContext()));
            playerAdapter.notifyDataSetChanged();

            boolean userExists = false;
            for (Player p : playerList) {
                if (p.getUsername().equals(joinedUser)) {
                    userExists = true;
                    break;
                }
            }
            if (!userExists) {
                playerList.add(new Player(joinedUser, getApplicationContext()));
                playerAdapter.notifyDataSetChanged();
            }

            if (startGameButton.isEnabled()) { // User is the lobby leader
                sendLobbyMessage(String.format(Locale.US, "radius %.2f", radius));

                if (lobbyCenter != null) {
                    sendLobbyMessage(String.format(Locale.US, "center %f %f", lobbyCenter.latitude, lobbyCenter.longitude));
                }

                StringBuilder userListMessage = new StringBuilder("users");
                for (Player p : playerList) {
                    userListMessage.append(" ").append(p.getUsername());
                }
                sendLobbyMessage(userListMessage.toString());
            }
        } else if (message.startsWith("User ") && message.endsWith(" disconnected")) {
            String disconnectedUser = message.substring(5, message.indexOf(" disconnected"));
            Toast.makeText(this, disconnectedUser + " has disconnected.", Toast.LENGTH_SHORT).show();
            playerList.removeIf(player -> player.getUsername().equals(disconnectedUser));
            playerAdapter.notifyDataSetChanged();
            if (playerList.get(0).getUsername().equals(username)) {
                setLobbyLeader(true);
            }
        } else if (message.startsWith("radius ")) {
            if (!startGameButton.isEnabled()) { // Only non-leaders should react to this message
                try {
                    float newRadius = Float.parseFloat(message.substring(7));
                    radius = newRadius;
                    radiusSlider.setValue(newRadius);
                    updateCircleRadius(true);
                    Log.d(TAG, "Lobby radius updated to: " + newRadius);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Could not parse radius from message: " + message, e);
                }
            }
        } else if (message.startsWith("center ")) {
            if (!startGameButton.isEnabled()) { // Only non-leaders should react to this message
                try {
                    String[] parts = message.substring(7).split(" ");
                    double lat = Double.parseDouble(parts[0]);
                    double lng = Double.parseDouble(parts[1]);
                    lobbyCenter = new LatLng(lat, lng);
                    updateCircleRadius(true);
                    Log.d(TAG, "Lobby center updated to: " + lobbyCenter.toString());
                } catch (Exception e) {
                    Log.e(TAG, "Could not parse center from message: " + message, e);
                }
            }
        } else if (message.startsWith("users ")) {
            String[] users = message.substring(6).split(" ");
            playerList.clear();
            for (String user : users) {
                if (!user.trim().isEmpty()) {
                    playerList.add(new Player(user.trim(), getApplicationContext()));
                }
            }
            playerAdapter.notifyDataSetChanged();
            Log.d(TAG, "Lobby user list updated.");
        } else if (message.startsWith("start ")) {
            try {
                String challengeId = message.substring(6);
                Log.d(TAG, "Received start game command with challenge ID: " + challengeId);
                fetchChallengeDetailsAndLaunch(challengeId);
            } catch (Exception e) {
                Log.e(TAG, "Could not parse challenge ID from start message", e);
            }
        } else if (message.equals("cannot join lobby")){
            Toast.makeText(this, "Failed to join lobby. It might be full or invalid.", Toast.LENGTH_LONG).show();
            finish(); // Close activity if join fails
        }
    }

    private void sendLobbyMessage(String message) {
        if (MOCK_MODE) {
            Log.d(TAG, "Mock mode: Pretending to send message: " + message);
            if (message.startsWith("start ")) {
                handleWebSocketMessage(message);
            }
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
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    private void setLobbyLeader(boolean isLeader) {
        startGameButton.setEnabled(isLeader);
        radiusSlider.setEnabled(isLeader);
    }
    // --- Mock Mode Methods (kept for reference) ---

    private void setupMockMode() {
        Log.d(TAG, "Running in Mock Mode");
        playerList.clear();
        playerList.add(new Player(username, getApplicationContext()));
        playerList.add(new Player("Player 2", getApplicationContext()));
        playerList.add(new Player("Player 3", getApplicationContext()));
        playerList.add(new Player("Player 4", getApplicationContext()));
        playerAdapter.notifyDataSetChanged();
        setLobbyLeader(true);
    }

    // Not used, but kept for future implementation
    private void launchGame(JSONObject gameState) throws JSONException {}

    private void updateLobbyState(JSONObject lobbyState) throws JSONException {}
}
