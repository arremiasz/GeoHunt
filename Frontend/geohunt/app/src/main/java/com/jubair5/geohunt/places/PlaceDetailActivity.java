package com.jubair5.geohunt.places;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jubair5.geohunt.R;

public class PlaceDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_detail_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("NAME"));
        }

        ImageView imageView = findViewById(R.id.place_image_detail);
        TextView latitudeText = findViewById(R.id.latitude_text);
        TextView longitudeText = findViewById(R.id.longitude_text);
        Button deleteButton = findViewById(R.id.delete_button);
        mapView = findViewById(R.id.map_view);

        String imageUrl = getIntent().getStringExtra("IMAGE_URL");
        double latitude = getIntent().getDoubleExtra("LATITUDE", 0);
        double longitude = getIntent().getDoubleExtra("LONGITUDE", 0);

        Glide.with(this).load(imageUrl).into(imageView);
        latitudeText.setText("Latitude: " + latitude);
        longitudeText.setText("Longitude: " + longitude);

        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Place")
                .setMessage("Are you sure you want to delete this place?")
                .setPositiveButton("Delete", (dialog, which) -> deletePlace())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePlace() {
        int id = getIntent().getIntExtra("ID", -1);
        if (id == -1) {
            Toast.makeText(this, "Place ID not found!", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO: Implement the DELETE request logic here.
        Toast.makeText(this, "Place deleted!", Toast.LENGTH_SHORT).show();
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        double latitude = getIntent().getDoubleExtra("LATITUDE", 0);
        double longitude = getIntent().getDoubleExtra("LONGITUDE", 0);
        LatLng location = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(location).title(getIntent().getStringExtra("NAME")));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
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
