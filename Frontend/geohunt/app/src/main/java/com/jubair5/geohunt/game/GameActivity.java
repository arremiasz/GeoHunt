/**
 * Activity responsible for handling the main game function
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.game;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.transition.TransitionManager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
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
import com.google.android.material.slider.Slider;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;

import org.json.JSONException;

import java.util.Locale;

public class GameActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "GameActivity";
    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Circle radiusCircle;
    private LinearLayout settingsContainer;
    private FrameLayout countdownOverlay;
    private TextView countdownText;
    private CardView stopwatchContainer;
    private TextView stopwatchText;
    private CardView hintContainer;
    private ImageView hintImage;
    private ImageView fullscreenPreview;
    private Button guessButton;
    private double radius = 1.0; // Default radius in miles
    private double currentLat;
    private double currentLng;
    private boolean gameStarted = false;
    private boolean userHasPanned = false;

    private Handler stopwatchHandler = new Handler(Looper.getMainLooper());
    private long startTime = 0L;

    // For dragging the hint box
    private float dX, dY;

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
                    Toast.makeText(this, "Location permission is required for the game.", Toast.LENGTH_LONG).show();
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mapView = findViewById(R.id.game_map_view);
        Slider radiusSlider = findViewById(R.id.radius_slider);
        Button readyButton = findViewById(R.id.ready_button);
        settingsContainer = findViewById(R.id.settings_container);
        countdownOverlay = findViewById(R.id.countdown_overlay);
        countdownText = findViewById(R.id.countdown_text);
        stopwatchContainer = findViewById(R.id.stopwatch_container);
        stopwatchText = findViewById(R.id.stopwatch_text);
        hintContainer = findViewById(R.id.hint_container);
        hintImage = findViewById(R.id.hint_image);
        fullscreenPreview = findViewById(R.id.fullscreen_preview);
        guessButton = findViewById(R.id.guess_button);
        guessButton.setVisibility(View.GONE);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        radiusSlider.addOnChangeListener((slider, value, fromUser) -> {
            radius = value;
            updateCircleRadius();
        });

        readyButton.setOnClickListener(v -> readyUp());
        guessButton.setOnClickListener(v -> startGuess());
        setupHintBoxListeners();
    }

    /**
     * This method is called when the user clicks the ready button. It stops location updates
     * and sends the game parameters to the server.
     */
    private void readyUp() {
        Log.d(TAG, "Ready button clicked. Beginning ready sequence.");


        // Get the most current location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission is required to start the game.", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLat = location.getLatitude();
                currentLng = location.getLongitude();
                Log.d(TAG, "Final location for game start: Lat: " + currentLat + ", Lng: " + currentLng);

                if (googleMap != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat, currentLng), 18f));
                }

                sendGameStartRequest();
            } else {
                Log.e(TAG, "Could not get final location to start game.");
                Toast.makeText(this, "Could not get current location. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Builds and sends the GET request to the server to get a generated location for the game.
     */
    private void sendGameStartRequest() {
        String url = Uri.parse(ApiConstants.BASE_URL + ApiConstants.GET_GENERATED_LOCATIONS_ENDPOINT)
                .buildUpon()
                .appendQueryParameter("lat", String.valueOf(currentLat))
                .appendQueryParameter("lng", String.valueOf(currentLng))
                .appendQueryParameter("radius", String.valueOf(radius))
                .build().toString();

        Log.d(TAG, "Sending game start request to: " + url);

        JsonObjectRequest gameStartRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "Successfully received game location: " + response.toString());
                    try {
                        int id = response.getInt("id");
                        String imageUrl = response.getString("streetviewurl");
                        startGame(id, imageUrl);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing game start response", e);
                        Toast.makeText(this, "Invalid response from server.", Toast.LENGTH_LONG).show();
                    }

                },
                error -> {
                    Log.e(TAG, "Failed to get game locations.", error);
                    Toast.makeText(this, "Error starting game. Please try again.", Toast.LENGTH_LONG).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(gameStartRequest);
    }

    /**
     * Starts the main game loop with the data received from the server. Makes the map full screen.
     * @param id The ID of the target location.
     * @param imageUrl The URL of the Street View image for the target.
     */
    private void startGame(int id, String imageUrl) {
        settingsContainer.setVisibility(View.GONE);
        gameStarted = true;

        if (radiusCircle != null) {
            radiusCircle.remove();
        }

        TransitionManager.beginDelayedTransition((ViewGroup) mapView.getParent());
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mapView.getLayoutParams();
        params.matchConstraintPercentHeight = 1.0f;
        mapView.setLayoutParams(params);

        startCountdown(id, imageUrl);
    }

    /**
     * Displays a 3-second countdown overlay and then proceeds to the main game logic.
     * @param id The ID of the target location.
     * @param imageUrl The URL of the Street View image for the target.
     */
    private void startCountdown(int id, String imageUrl) {
        countdownOverlay.setVisibility(View.VISIBLE);
        countdownText.setText(String.valueOf(3));
        new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                if (seconds > 0) {
                    countdownText.setText(String.valueOf(seconds));
                } else {
                    countdownText.setText(R.string.go);
                }
            }

            public void onFinish() {
                countdownOverlay.setVisibility(View.GONE);
                startStopwatch();
                showHint(imageUrl);
                guessButton.setVisibility(View.VISIBLE);
                Log.d(TAG, "Countdown finished. Starting game with challenge ID: " + id);
                // TODO: Main game logic starts here
            }
        }.start();
    }

    /**
     * Starts the stopwatch and updates the text view.
     */
    private void startStopwatch() {
        stopwatchContainer.setVisibility(View.VISIBLE);
        startTime = System.currentTimeMillis();
        stopwatchHandler.post(new Runnable() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                stopwatchText.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));

                stopwatchHandler.postDelayed(this, 500);
            }
        });
    }

    /**
     * Stops the stopwatch and returns the elapsed time.
     * @return The elapsed time in seconds.
     */
    private int stopStopwatch() {
        stopwatchHandler.removeCallbacksAndMessages(null);
        long millis = System.currentTimeMillis() - startTime;
        return (int) (millis / 1000);
    }

    /**
     * This method is called when the user clicks the guess button.
     */
    private void startGuess() {
        Log.d(TAG, "Guess button clicked.");
        // TODO: Implement guess logic
    }

    /**
     * Makes the hint container visible and loads the image.
     * @param imageUrl The URL of the hint image.
     */
    private void showHint(String imageUrl) {
        fullscreenPreview.setVisibility(View.VISIBLE);
        hintContainer.post(() -> snapToCorner(hintContainer));
        Glide.with(this).load(imageUrl).into(hintImage);
        Glide.with(this).load(imageUrl).into(fullscreenPreview);
    }

    /**
     * Sets up the listeners for the hint box to allow dragging and clicking.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setupHintBoxListeners() {
        fullscreenPreview.setOnClickListener(v -> {
            fullscreenPreview.setVisibility(View.GONE);
            hintContainer.setVisibility(View.VISIBLE);
        });

        hintContainer.setOnClickListener(v -> {
            fullscreenPreview.setVisibility(View.VISIBLE);
            hintContainer.setVisibility(View.GONE);
        });

        hintContainer.setOnTouchListener(new View.OnTouchListener() {
            private long startClickTime;
            private static final int MAX_CLICK_DURATION = 200;
            private float pressedX, pressedY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startClickTime = System.currentTimeMillis();
                        pressedX = event.getRawX();
                        pressedY = event.getRawY();
                        dX = view.getX() - pressedX;
                        dY = view.getY() - pressedY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        view.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;
                    case MotionEvent.ACTION_UP:
                        long clickDuration = System.currentTimeMillis() - startClickTime;
                        float distance = (float) Math.hypot(event.getRawX() - pressedX, event.getRawY() - pressedY);

                        if (clickDuration < MAX_CLICK_DURATION && distance < 10) {
                            // Click
                            view.performClick();
                        } else {
                            // Drag
                            snapToCorner(view);
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    /**
     * Snaps the hint box to the nearest corner of the screen.
     * @param view The hint box view.
     */
    private void snapToCorner(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        int parentWidth = parent.getWidth();
        int parentHeight = parent.getHeight();
        int margin = (int) (16 * getResources().getDisplayMetrics().density);

        float viewX = view.getX();
        float viewY = view.getY();

        float endX, endY;

        // Snap to left or right corner
        if (viewX + view.getWidth() / 2f < parentWidth / 2f) {
            endX = margin;
        } else {
            endX = parentWidth - view.getWidth() - margin;
        }

        // Snap to top or bottom corner
        if (viewY + view.getHeight() / 2f < parentHeight / 2f) {
            endY = margin + 150;
        } else {
            endY = parentHeight - view.getHeight() - margin - 200;
        }

        view.animate()
                .x(endX)
                .y(endY)
                .setDuration(200)
                .start();
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        enableMyLocation();

        googleMap.setOnCameraMoveStartedListener(reason -> {
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                Log.d(TAG, "User panned the map.");
                userHasPanned = true;
            }
        });

        googleMap.setOnMyLocationButtonClickListener(() -> {
            Log.d(TAG, "My Location button clicked.");
            userHasPanned = false;
            LatLng currentLatLng = new LatLng(currentLat, currentLng);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f));
            return true;
        });
    }

    /**
     * Checks for location permission, and if granted, enables the 'My Location' layer and
     * moves the camera to the user's current position.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (googleMap != null) {
                googleMap.setMyLocationEnabled(true);
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
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
     * Starts requesting location updates to keep the user's position centered.
     */
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    currentLat = location.getLatitude();
                    currentLng = location.getLongitude();

                    if (gameStarted && !userHasPanned) {
                        LatLng currentLatLng = new LatLng(currentLat, currentLng);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                    } else if (!gameStarted) {
                        updateCircleRadius();
                    }
                }
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    /**
     * Updates the radius circle on the map to reflect the current search radius.
     */
    private void updateCircleRadius() {
        if (googleMap != null) {
            if (radiusCircle != null) {
                radiusCircle.remove();
            }
            LatLng currentLatLng = new LatLng(currentLat, currentLng);
            radiusCircle = googleMap.addCircle(new CircleOptions()
                    .center(currentLatLng)
                    .radius(radius * 1609.34) // Convert miles to meters
                    .strokeColor(Color.BLUE)
                    .strokeWidth(2f)
                    .fillColor(0x220000FF));

            // Adjust camera to fit the circle
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(getOffsetLatLng(currentLatLng, radius * 1609.34, 0))
                    .include(getOffsetLatLng(currentLatLng, radius * 1609.34, 90))
                    .include(getOffsetLatLng(currentLatLng, radius * 1609.34, 180))
                    .include(getOffsetLatLng(currentLatLng, radius * 1609.34, 270))
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
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
        mapView.onPause();
        super.onPause();
        if (fusedLocationProviderClient != null && locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
        stopwatchHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
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
}
