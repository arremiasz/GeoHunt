package com.jubair5.geohunt.game;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
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
import androidx.core.content.FileProvider;
import androidx.transition.TransitionManager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.slider.Slider;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Activity responsible for handling the main game function
 * 
 * @author Alex Remiasz
 */
public class GameActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "GameActivity";
    protected MapView mapView;
    protected GoogleMap googleMap;
    protected FusedLocationProviderClient fusedLocationProviderClient;
    protected LocationCallback locationCallback;
    protected Circle radiusCircle;
    protected LinearLayout settingsContainer;
    protected FrameLayout countdownOverlay;
    protected TextView countdownText;
    protected CardView stopwatchContainer;
    protected TextView stopwatchText;
    protected CardView hintContainer;
    protected ImageView hintImage;
    protected ImageView fullscreenPreview;
    protected Button guessButton;
    protected double radius = 1.0; // Default radius in miles
    protected double currentLat;
    protected double currentLng;
    protected boolean gameStarted = false;
    protected boolean userHasPanned = false;
    protected Uri guessImageUri;
    protected int challengeId;
    protected int strokeColor = Color.BLUE;
    protected int fillColor = 0x220000FF;

    protected Handler stopwatchHandler = new Handler(Looper.getMainLooper());
    protected long startTime = 0L;

    // For dragging the hint box
    protected float dX, dY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        setup(savedInstanceState);
    }

    protected void onCreate(Bundle savedInstanceState, int activity) {
        super.onCreate(savedInstanceState);
        setContentView(activity);
        setup(savedInstanceState);
    }

    protected void setup(Bundle savedInstanceState) {
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
     * This method is called when the user clicks the ready button. It stops
     * location updates
     * and sends the game parameters to the server.
     */
    protected void readyUp() {
        Log.d(TAG, "Ready button clicked. Beginning ready sequence.");

        // Get the most current location
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
     * Builds and sends the GET request to the server to get a generated location
     * for the game.
     */
    protected void sendGameStartRequest() {
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
                });

        VolleySingleton.getInstance(this).addToRequestQueue(gameStartRequest);
    }

    /**
     * Starts the main game loop with the data received from the server. Makes the
     * map full screen.
     * 
     * @param id       The ID of the target location.
     * @param imageUrl The URL of the Street View image for the target.
     */
    protected void startGame(int id, String imageUrl) {
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
     * Displays a 3-second countdown overlay and then proceeds to the main game
     * logic.
     * 
     * @param id       The ID of the target location.
     * @param imageUrl The URL of the Street View image for the target.
     */
    protected void startCountdown(int id, String imageUrl) {
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
                challengeId = id;
            }
        }.start();
    }

    /**
     * Starts the stopwatch and updates the text view.
     */
    protected void startStopwatch() {
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
     * 
     * @return The elapsed time in seconds.
     */
    protected int stopStopwatch() {
        stopwatchHandler.removeCallbacksAndMessages(null);
        long millis = System.currentTimeMillis() - startTime;
        return (int) (millis / 1000);
    }

    /**
     * This method is called when the user clicks the guess button.
     * It initiates the process of taking a picture for the guess.
     */
    protected void startGuess() {
        Log.d(TAG, "Guess button clicked.");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissionForGuessLauncher.launch(Manifest.permission.CAMERA);
        } else {
            launchCameraForGuess();
        }
    }

    /**
     * Creates a temporary file URI and launches the camera intent for the guess.
     */
    protected void launchCameraForGuess() {
        guessImageUri = createImageUri("guess_photo.jpg");
        if (guessImageUri != null) {
            takePictureForGuessLauncher.launch(guessImageUri);
        } else {
            Log.e(TAG, "Failed to create image URI for guess.");
            Toast.makeText(this, "Error preparing camera for guess.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void submitGuess(String imageString) {
        SharedPreferences prefs = getSharedPreferences("GeoHuntPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "User ID not found in shared preferences for submission.");
            return;
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("latitude", currentLat);
            requestBody.put("longitude", currentLng);
            requestBody.put("photourl", imageString);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create JSON object for submission.", e);
        }

        String url = ApiConstants.BASE_URL + ApiConstants.POST_SUBMISSION_ENDPOINT + "?uid=" + userId + "&cid="
                + challengeId;

        StringRequest submissionRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Submission successful: " + response);
                    Intent intent = new Intent(GameActivity.this, ResultsActivity.class);
                    intent.putExtra("results", Double.parseDouble(response));
                    intent.putExtra("challengeId", challengeId);
                    intent.putExtra("guessLat", currentLat);
                    intent.putExtra("guessLng", currentLng);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    Log.e(TAG, "Submission failed.", error);
                    Toast.makeText(this, "Error submitting guess. Please try again.", Toast.LENGTH_LONG).show();
                }) {
            @Override
            public byte[] getBody() {
                return requestBody.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(submissionRequest);

    }

    /**
     * Creates a content Uri for a temporary file in the app's cache directory.
     * 
     * @param fileName The name of the file to create.
     * @return The Uri for the temporary image file.
     */
    protected Uri createImageUri(String fileName) {
        File imageFile = new File(getCacheDir(), fileName);
        return FileProvider.getUriForFile(this, getPackageName() + ".provider", imageFile);
    }

    /**
     * Converts a Bitmap image to a Base64 encoded string.
     * 
     * @param bitmap The bitmap to be converted.
     * @return The Base64 encoded string representation of the bitmap.
     */
    protected String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * This method handles the result of the location permission request.
     */
    protected final ActivityResultLauncher<String> requestLocationPermissionLauncher = registerForActivityResult(
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

    /**
     * This method handles getting the camera permission for guessing.
     */
    protected final ActivityResultLauncher<String> requestCameraPermissionForGuessLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    Log.d(TAG, "Camera permission for guess granted.");
                    launchCameraForGuess();
                } else {
                    Log.w(TAG, "Camera permission for guess denied.");
                    Toast.makeText(this, "Camera permission is required to make a guess.", Toast.LENGTH_SHORT).show();
                }
            });

    /**
     * This method is called when the user has taken a picture for their guess.
     */
    protected final ActivityResultLauncher<Uri> takePictureForGuessLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            success -> {
                if (success) {
                    Log.d(TAG, "Guess image capture successful.");
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(guessImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        String imageString = bitmapToString(bitmap);

                        Log.d(TAG, "Guess image as Base64 string is ready.");

                        submitGuess(imageString);

                    } catch (IOException e) {
                        Log.e(TAG, "Failed to read guess image file.", e);
                        Toast.makeText(this, "Error processing guess image.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.w(TAG, "Guess image capture cancelled or failed.");
                    Toast.makeText(this, "Guess cancelled.", Toast.LENGTH_SHORT).show();
                }
            });

    /**
     * Makes the hint container visible and loads the image.
     * 
     * @param imageUrl The URL of the hint image.
     */
    protected void showHint(String imageUrl) {
        fullscreenPreview.setVisibility(View.VISIBLE);
        hintContainer.post(() -> snapToCorner(hintContainer));
        if (imageUrl != null) {
            if (imageUrl.startsWith("http")) {
                Glide.with(this).load(imageUrl).into(hintImage);
                Glide.with(this).load(imageUrl).into(fullscreenPreview);
            } else {
                try {
                    byte[] imageData = Base64.decode(imageUrl, Base64.DEFAULT);
                    Glide.with(this).load(imageData).into(hintImage);
                    Glide.with(this).load(imageData).into(fullscreenPreview);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Bad Base64 string for hint image.", e);
                    Toast.makeText(this, "Failed to load hint image.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Sets up the listeners for the hint box to allow dragging and clicking.
     */
    @SuppressLint("ClickableViewAccessibility")
    protected void setupHintBoxListeners() {
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
     * 
     * @param view The hint box view.
     */
    protected void snapToCorner(View view) {
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
        setMapStyle();

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
     * Checks for location permission, and if granted, enables the 'My Location'
     * layer and
     * moves the camera to the user's current position.
     */
    protected void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
    protected void startLocationUpdates() {
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
     * Sets the map style based on the current system theme (dark or light).
     */
    protected void setMapStyle() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            Log.d(TAG, "Setting dark mode map style.");
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark));
            strokeColor = Color.rgb(0, 100, 255);
            fillColor = Color.argb(34, 0, 150, 255);
        }
    }

    /**
     * Updates the radius circle on the map to reflect the current search radius.
     */
    protected void updateCircleRadius() {
        if (googleMap != null) {
            if (radiusCircle != null) {
                radiusCircle.remove();
            }
            LatLng currentLatLng = new LatLng(currentLat, currentLng);
            radiusCircle = googleMap.addCircle(new CircleOptions()
                    .center(currentLatLng)
                    .radius(radius * 1609.34) // Convert miles to meters
                    .strokeColor(strokeColor)
                    .strokeWidth(2f)
                    .fillColor(fillColor));

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
     * Calculates a new LatLng based on a starting point, a distance in meters, and
     * a bearing.
     * 
     * @param latLng   The starting LatLng.
     * @param distance The distance in meters.
     * @param bearing  The bearing in degrees.
     * @return The new LatLng.
     */
    protected LatLng getOffsetLatLng(LatLng latLng, double distance, double bearing) {
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
