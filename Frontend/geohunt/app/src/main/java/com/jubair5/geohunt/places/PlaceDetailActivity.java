/**
 * Activity to display the details of a place.
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.places;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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

        getSupportActionBar().setTitle("My Place");

        ImageView imageView = findViewById(R.id.place_image_detail);
        TextView latitudeText = findViewById(R.id.latitude_text);
        TextView longitudeText = findViewById(R.id.longitude_text);
        mapView = findViewById(R.id.map_view);

        String imageUrl = getIntent().getStringExtra("IMAGE_URL");
        double latitude = getIntent().getDoubleExtra("LATITUDE", 0);
        double longitude = getIntent().getDoubleExtra("LONGITUDE", 0);

        Glide.with(this).load(imageUrl).into(imageView);
        latitudeText.setText("Latitude: " + latitude);
        longitudeText.setText("Longitude: " + longitude);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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
