/**
 * Activity to display the details of a place.
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.submissions;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.jubair5.geohunt.R;

public class PlaceDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        getSupportActionBar().setTitle("My Place");

        ImageView imageView = findViewById(R.id.place_image_detail);
        TextView latitudeText = findViewById(R.id.latitude_text);
        TextView longitudeText = findViewById(R.id.longitude_text);

        String imageUrl = getIntent().getStringExtra("IMAGE_URL");
        double latitude = getIntent().getDoubleExtra("LATITUDE", 0);
        double longitude = getIntent().getDoubleExtra("LONGITUDE", 0);

        Glide.with(this).load(imageUrl).into(imageView);
        latitudeText.setText("Latitude: " + latitude);
        longitudeText.setText("Longitude: " + longitude);
    }
}
