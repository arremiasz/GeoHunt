/**
 * Activity responsible for handling the main game function
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.game;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
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
import org.json.JSONObject;

import java.util.Locale;

public class GameActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "GameActivity";
    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Circle radiusCircle;
    private LinearLayout settingsContainer;
    private FrameLayout countdownOverlay;
    private TextView countdownText;
    private CardView stopwatchContainer;
    private TextView stopwatchText;
    private double radius = 1.0; // Default radius in miles
    private double currentLat;
    private double currentLng;

    private Handler stopwatchHandler = new Handler(Looper.getMainLooper());
    private long startTime = 0L;

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

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        radiusSlider.addOnChangeListener((slider, value, fromUser) -> {
            radius = value;
            updateCircleRadius();
        });

        readyButton.setOnClickListener(v -> readyUp());
    }

    /**
     * This method is called when the user clicks the ready button. It stops location updates
     * and sends the game parameters to the server.
     */
    private void readyUp() {
        Log.d(TAG, "Ready button clicked. Stopping location updates.");
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }

        // Get the most current location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission is required to start the game.", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
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
                    countdownText.setText("GO!");
                }
            }

            public void onFinish() {
                countdownOverlay.setVisibility(View.GONE);
                startStopwatch();
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        enableMyLocation();
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
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f));
                        updateCircle(currentLatLng);
                        checkZoomAndAdjust(); // Check zoom after first location
                    } else {
                        Log.w(TAG, "FusedLocationProvider returned null location for initial position.");
                    }
                });
                startLocationUpdates();
            }
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Updates the circle on the map with a new center and radius.
     * @param center The new center for the circle.
     */
    private void updateCircle(LatLng center) {
        if (googleMap == null) return;

        if (radiusCircle == null) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(center != null ? center : new LatLng(0, 0))
                    .radius(radius * 1609.34) // Convert miles to meters
                    .strokeColor(Color.BLUE)
                    .fillColor(0x220000FF);
            radiusCircle = googleMap.addCircle(circleOptions);
        } else {
            if (center != null) {
                radiusCircle.setCenter(center);
            }
        }
    }

    /**
     * Updates the radius of the circle on the map and checks if a zoom adjustment is needed.
     */
    private void updateCircleRadius() {
        if (radiusCircle != null) {
            radiusCircle.setRadius(radius * 1609.34);
            checkZoomAndAdjust();
        }
    }

    /**
     * Checks if the radius circle fits well within the map view, zooming out if it's too large
     * and zooming in if it's too small.
     */
    private void checkZoomAndAdjust() {
        if (googleMap == null || radiusCircle == null) return;

        LatLngBounds mapBounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
        LatLngBounds circleBounds = getCircleBounds(radiusCircle.getCenter(), radiusCircle.getRadius());

        // Zoom out if the circle is not fully contained within the map's visible bounds
        if (!mapBounds.contains(circleBounds.northeast) || !mapBounds.contains(circleBounds.southwest)) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(circleBounds, 150));
        } else {
            // Zoom in if the circle takes up less than a third of the map's height
            double circleVisibleSpan = Math.abs(circleBounds.northeast.latitude - circleBounds.southwest.latitude);
            double mapVisibleSpan = Math.abs(mapBounds.northeast.latitude - mapBounds.southwest.latitude);

            if (circleVisibleSpan < (mapVisibleSpan / 3.0)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(circleBounds, 150));
            }
        }
    }

    /**
     * Calculates the bounding box for a circle on the map.
     * @param center The center of the circle.
     * @param radiusInMeters The radius of the circle in meters.
     * @return A LatLngBounds object representing the circle's bounding box.
     */
    private LatLngBounds getCircleBounds(LatLng center, double radiusInMeters) {
        double R2D = 180 / Math.PI;
        double EarthRadius = 6378137; // meters
        double lat = center.latitude;
        double lon = center.longitude;

        double rlat = (radiusInMeters / EarthRadius) * R2D;
        double rlon = rlat / Math.cos(Math.toRadians(lat));

        return new LatLngBounds(
                new LatLng(lat - rlat, lon - rlon),
                new LatLng(lat + rlat, lon + rlon)
        );
    }

    /**
     * Configures and starts continuous location updates.
     */
    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 5 seconds
        locationRequest.setFastestInterval(2500); // 2.5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (googleMap != null) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                        updateCircle(currentLatLng);
                    }
                }
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
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
        stopwatchHandler.removeCallbacksAndMessages(null);
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
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
