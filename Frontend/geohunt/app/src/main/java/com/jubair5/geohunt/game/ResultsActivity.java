package com.jubair5.geohunt.game;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

/**
 * Page for displaying the results from a game.
 * 
 * @author Alex Remiasz
 */
public class ResultsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "ResultsActivity";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_ID = "userId";

    protected TextView distanceText;
    protected TextView distanceUnitLabel;
    protected Button playAgainButton;
    protected Button goHomeButton;
    protected KonfettiView konfettiView;
    protected MapView mapView;
    protected GoogleMap googleMap;
    protected RatingBar ratingBar;
    protected RecyclerView galleryRecyclerView;
    protected PhotoAdapter photoAdapter;
    protected List<Photo> photosList;
    protected int challengeId;
    protected double guessLat;
    protected double guessLng;
    protected double targetLat;
    protected double targetLng;
    protected boolean targetLocationLoaded = false;
    protected boolean userHasRated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Results");
        }

        setupViews(savedInstanceState);
        displayResults();
        setupButtons();
        startConfetti();
        fetchChallengeDetails();
    }

    /**
     * Initializes the views from the layout file.
     */
    protected void setupViews(Bundle savedInstanceState) {
        distanceText = findViewById(R.id.distance_text);
        distanceUnitLabel = findViewById(R.id.distance_unit_label);
        playAgainButton = findViewById(R.id.play_again_button);
        goHomeButton = findViewById(R.id.go_home_button);
        konfettiView = findViewById(R.id.konfetti_view);
        mapView = findViewById(R.id.results_map_view);
        ratingBar = findViewById(R.id.rating_bar);
        galleryRecyclerView = findViewById(R.id.gallery_recycler_view);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                userHasRated = true;
                ratingBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FFD700"))); // Gold
                submitRating(rating);
            }
        });

        photosList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(this, photosList);
        galleryRecyclerView.setAdapter(photoAdapter);
    }

    /**
     * Parses the results from the intent and updates the UI.
     */
    protected void displayResults() {
        double results = getIntent().getDoubleExtra("results", -1);
        if (results != -1) {
            if (results <= 0.1) {
                double distanceInFeet = results * 5280;
                distanceText.setText(String.format(Locale.getDefault(), "%.0f", distanceInFeet));
                distanceUnitLabel.setText("feet");
            } else {
                distanceText.setText(String.format(Locale.getDefault(), "%.2f", results));
                distanceUnitLabel.setText("miles");
            }

            distanceUnitLabel.append(" from target");
        } else {
            distanceText.setText("?");
            distanceUnitLabel.setText("Error");
        }

        challengeId = getIntent().getIntExtra("challengeId", -1);
        guessLat = getIntent().getDoubleExtra("guessLat", 0.0);
        guessLng = getIntent().getDoubleExtra("guessLng", 0.0);
    }

    /**
     * Sets up the click listeners for the buttons.
     */
    protected void setupButtons() {
        playAgainButton.setOnClickListener(v -> onPlayAgainClicked());
        goHomeButton.setOnClickListener(v -> onGoHomeClicked());
    }

    /**
     * Navigates to the GameActivity when the "Play Again" button is clicked.
     */
    protected void onPlayAgainClicked() {
        Intent intent = new Intent(ResultsActivity.this, GameActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Finishes the activity when the "Go Home" button is clicked.
     */
    protected void onGoHomeClicked() {
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        updateMap();
    }

    /**
     * Updates the map with markers for the target and guess locations, and a line
     * connecting them.
     */
    protected void updateMap() {
        if (googleMap == null || !targetLocationLoaded) {
            return;
        }

        LatLng targetLatLng = new LatLng(targetLat, targetLng);
        LatLng guessLatLng = new LatLng(guessLat, guessLng);

        googleMap.clear();

        googleMap.addMarker(new MarkerOptions()
                .position(targetLatLng)
                .title("Target Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        googleMap.addMarker(new MarkerOptions()
                .position(guessLatLng)
                .title("Your Guess")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        googleMap.addPolyline(new PolylineOptions()
                .add(targetLatLng, guessLatLng)
                .width(5)
                .color(Color.RED));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(targetLatLng);
        builder.include(guessLatLng);
        LatLngBounds bounds = builder.build();

        try {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLatLng, 10f));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null)
            mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null)
            mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mapView != null)
            mapView.onStop();
    }

    @Override
    protected void onPause() {
        if (mapView != null)
            mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mapView != null)
            mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null)
            mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null)
            mapView.onSaveInstanceState(outState);
    }

    /**
     * Fetches the challenge details (target location) and gallery photos.
     */
    protected void fetchChallengeDetails() {
        if (challengeId == -1) {
            Log.e(TAG, "Challenge ID not found in intent.");
            return;
        }

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        int userId = prefs.getInt(KEY_USER_ID, -1);

        String url = ApiConstants.BASE_URL + ApiConstants.GET_CHALLENGE_BY_ID_ENDPOINT + "?cid=" + challengeId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "Response: " + response.toString());
                    try {
                        targetLat = response.getDouble("latitude");
                        targetLng = response.getDouble("longitude");
                        targetLocationLoaded = true;
                        updateMap();

                        if (!userHasRated) {
                            double rating = response.optDouble("rating", 0.0);
                            ratingBar.setRating((float) rating);
                            ratingBar.setProgressTintList(ColorStateList.valueOf(Color.DKGRAY));
                        }

                        JSONArray submissions = response.getJSONArray("submissions");
                        photosList.clear();
                        for (int i = 0; i < submissions.length(); i++) {
                            JSONObject submission = submissions.getJSONObject(i);
                            int submissionUserId = submission.optInt("uid", -1);
                            if (submissionUserId != userId) {
                                Photo photo = new Photo(submission);
                                photosList.add(photo);
                            }
                        }
                        photoAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Loaded " + photosList.size() + " gallery photos");
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing challenge details JSON", e);
                        Toast.makeText(this, "Failed to load results details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching challenge details", error);
                    Toast.makeText(this, "Failed to load results details", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Submits the user's rating for the challenge.
     * 
     * @param rating The rating value (1-5).
     */
    protected void submitRating(float rating) {
        if (challengeId == -1)
            return;

        String url = ApiConstants.BASE_URL + ApiConstants.RATE_CHALLENGE_ENDPOINT + "?cid=" + challengeId + "&rating="
                + (int) rating;
        Log.d(TAG, "Submitting rating: " + url);

        StringRequest ratingRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Rating submitted successfully: " + response);
                    Toast.makeText(this, "Rating submitted!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e(TAG, "Error submitting rating", error);
                    Toast.makeText(this, "Failed to submit rating", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(ratingRequest);
    }

    /**
     * Configures and starts the confetti animation.
     */
    protected void startConfetti() {
        konfettiView.build()
                .addColors(Color.rgb(168, 100, 253),
                        Color.rgb(41, 205, 255),
                        Color.rgb(120, 255, 68),
                        Color.rgb(255, 113, 141),
                        Color.rgb(253, 255, 106))
                .setDirection(0.0, 180.0)
                .setSpeed(1f, 4f)
                .setFadeOutEnabled(true)
                .setTimeToLive(3000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(12, 5f))
                .setPosition(getResources().getDisplayMetrics().widthPixels / 2f, -100)
                .streamFor(150, 1600L);
    }
}
