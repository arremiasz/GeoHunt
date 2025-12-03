package com.jubair5.geohunt.game;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
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
public class ResultsActivity extends AppCompatActivity {

    private static final String TAG = "ResultsActivity";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_ID = "userId";

    protected TextView distanceText;
    protected TextView distanceUnitLabel;
    protected Button playAgainButton;
    protected Button goHomeButton;
    protected KonfettiView konfettiView;
    protected ImageView userPhotoView;
    protected RecyclerView galleryRecyclerView;
    protected PhotoAdapter photoAdapter;
    protected List<Photo> photosList;
    protected int challengeId;
    protected String userPhotoString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Results");
        }

        setupViews();
        displayResults();
        setupButtons();
        startConfetti();
        fetchGalleryPhotos();
    }

    /**
     * Initializes the views from the layout file.
     */
    protected void setupViews() {
        distanceText = findViewById(R.id.distance_text);
        distanceUnitLabel = findViewById(R.id.distance_unit_label);
        playAgainButton = findViewById(R.id.play_again_button);
        goHomeButton = findViewById(R.id.go_home_button);
        konfettiView = findViewById(R.id.konfetti_view);
        userPhotoView = findViewById(R.id.user_photo_view);
        galleryRecyclerView = findViewById(R.id.gallery_recycler_view);

        // Setup RecyclerView for gallery
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

        // Display user's photo
        challengeId = getIntent().getIntExtra("challengeId", -1);
        userPhotoString = getIntent().getStringExtra("userPhoto");
        if (userPhotoString != null && !userPhotoString.isEmpty()) {
            try {
                byte[] imageData = Base64.decode(userPhotoString, Base64.DEFAULT);
                Glide.with(this).load(imageData).into(userPhotoView);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Failed to decode user photo from Base64", e);
            }
        }
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

    /**
     * Fetches the gallery photos from the server for the current challenge.
     */
    protected void fetchGalleryPhotos() {
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
                        Log.e(TAG, "Error parsing gallery photos JSON", e);
                        Toast.makeText(this, "Failed to load gallery photos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching gallery photos", error);
                    Toast.makeText(this, "Failed to load gallery photos", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
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
