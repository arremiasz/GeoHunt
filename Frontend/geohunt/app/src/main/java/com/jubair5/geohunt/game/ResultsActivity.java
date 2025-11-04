/**
 * Page for displaying the results from a game.
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.game;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jubair5.geohunt.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Results");
        }

        TextView distanceText = findViewById(R.id.distance_text);
        TextView distanceUnitLabel = findViewById(R.id.distance_unit_label);
        Button playAgainButton = findViewById(R.id.play_again_button);
        Button goHomeButton = findViewById(R.id.go_home_button);
        KonfettiView konfettiView = findViewById(R.id.konfetti_view);

        String results = getIntent().getStringExtra("results");
        if (results != null) {
            try {
                JSONObject json = new JSONObject(results);
                double distance = json.getDouble("distance");

                if (distance <= 0.1) {
                    double distanceInFeet = distance * 5280;
                    distanceText.setText(String.format(Locale.getDefault(), "%.0f", distanceInFeet));
                    distanceUnitLabel.setText("feet");
                } else {
                    distanceText.setText(String.format(Locale.getDefault(), "%.2f", distance));
                    distanceUnitLabel.setText("miles");
                }

                distanceUnitLabel.append(" from target");
            } catch (JSONException e) {
                distanceText.setText("?");
                distanceUnitLabel.setText("Error");
            }
        }

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


        playAgainButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultsActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        });

        goHomeButton.setOnClickListener(v -> {
            finish();
        });
    }
}
