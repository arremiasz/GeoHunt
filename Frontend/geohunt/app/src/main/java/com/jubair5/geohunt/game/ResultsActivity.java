package com.jubair5.geohunt.game;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jubair5.geohunt.R;
import com.jubair5.geohunt.reward.powerups.PowerUp;
import com.jubair5.geohunt.reward.powerups.TimeReductionPU;
import com.jubair5.geohunt.reward.powerups.hintPu;

import java.util.Locale;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

/**
 * Page for displaying the results from a game.
 * @author Alex Remiasz, Nathan Imig
 */
public class ResultsActivity extends AppCompatActivity {

    protected TextView distanceText, distanceUnitLabel;
    protected TextView timeText, timeUnitLabel;
    protected TextView pointsText, pointsUnitsLabel;
    protected TextView powerUpUnlock;
    protected ImageView newPowerUp;
    protected Button playAgainButton;
    protected Button goHomeButton;
    protected KonfettiView konfettiView;

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
    }

    /**
     * Initializes the views from the layout file.
     */
    protected void setupViews() {
        distanceText = findViewById(R.id.distance_text);
        distanceUnitLabel = findViewById(R.id.distance_unit_label);

        timeText = findViewById(R.id.time_text);
        timeUnitLabel = findViewById(R.id.time_unit_label);

        pointsText = findViewById(R.id.currency_text);
        pointsUnitsLabel = findViewById(R.id.currency_unit_text);


        powerUpUnlock = findViewById(R.id.unlock_text);
        newPowerUp = findViewById(R.id.new_PowerUp);

        playAgainButton = findViewById(R.id.play_again_button);
        goHomeButton = findViewById(R.id.go_home_button);
        konfettiView = findViewById(R.id.konfetti_view);
    }


    /**
     * Parses the results from the intent and updates the UI.
     */
    protected void displayResults() {
        double results = getIntent().getDoubleExtra("results", -1);
        int time = getIntent().getIntExtra("time", -1);
        getRewards(results,time);


        if (results != -1) {
            if (results <= 0.1) {
                double distanceInFeet = results * 5280;
                distanceText.setText(String.format(Locale.getDefault(), "%.0f", distanceInFeet));
                distanceUnitLabel.setText("feet");
            } else {
                distanceText.setText(String.format(Locale.getDefault(), "%.2f", results));
                distanceUnitLabel.setText("miles");
            }

            distanceUnitLabel.append(" away");
        } else {
            distanceText.setText("?");
            distanceUnitLabel.setText("Error");
        }

        // Time
        if (time != -1) {
            int minuets = time/60;
            int seconds = time % 60;

            timeText.setText(minuets + ":" + seconds);
            timeUnitLabel.setText("seconds");
        } else {
            timeText.setText("?");
            timeUnitLabel.setText("Error");
        }

        

    }

    /**
     * Returns an amount of currency based off of results and time
     * @param results
     * @param time
     * @return amount of currency
     */
    private int getPoints(double results, int time) {
        return 500;
    }

    private void getRewards(double results, int time){
        pointsText.setText(getPoints(results,time)+ "");
        pointsUnitsLabel.setText("points");
        int type = (int) (Math.random()*2);
        PowerUp powerUp;

        if(type == 0){
            powerUp = new TimeReductionPU();
        }
        else{
            powerUp = new hintPu();
        }
        newPowerUp.setOnClickListener(v -> showDescription(powerUp));
        
        
        
    }

    /**
     * Sets up the click listeners for the buttons.
     */
    protected void setupButtons() {
        playAgainButton.setOnClickListener(v -> onPlayAgainClicked());
        goHomeButton.setOnClickListener(v -> onGoHomeClicked());
    }

    private void showDescription(PowerUp powerUp) {
        Dialog powerUpDescription = new Dialog(this, R.style.DialogStyle);
        powerUpDescription.setContentView(R.layout.layout_power_up_description);

        TextView title = powerUpDescription.findViewById(R.id.powerUp_title);
        title.setText(powerUp.getTitle());

        TextView description = powerUpDescription.findViewById(R.id.powerUp_description);
        description.setText(powerUp.getDescription());

        ImageView imageView = powerUpDescription.findViewById(R.id.powerUp_icon);
        imageView.setImageResource(powerUp.getImage());

        ImageView btnClose = powerUpDescription.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                powerUpDescription.dismiss();
            }
        });

        powerUpDescription.show();
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
                .setPosition(getResources().getDisplayMetrics().widthPixels / 2f, 20)
                .streamFor(150, 1600L);
    }
}
