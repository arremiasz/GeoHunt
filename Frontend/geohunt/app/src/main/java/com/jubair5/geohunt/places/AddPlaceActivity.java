/**
 * Activity for adding places
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.places;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jubair5.geohunt.R;

import java.io.File;

public class AddPlaceActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "AddPlaceActivity";

    private ImageView capturedImage;
    private MapView mapView;
    private Button submitButton;
    private Uri imageUri;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap googleMap;
    private double latitude;
    private double longitude;

    /**
     * This method is called when the user has taken a picture.
     */
    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            success -> {
                if (success) {
                    Log.d(TAG, "Image capture successful.");
                    capturedImage.setImageURI(imageUri);
                    fetchLocation();
                } else {
                    Log.w(TAG, "Image capture cancelled or failed.");
                    Toast.makeText(this, "Cancelled.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    /**
     * This method handles getting the camera permission.
     */
    private final ActivityResultLauncher<String> requestCameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    Log.d(TAG, "Camera permission granted.");
                    launchCamera();
                } else {
                    Log.w(TAG, "Camera permission denied.");
                    Toast.makeText(this, "Camera permission is required to add a place.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    /**
     * This method handles getting the location permission.
     */
    private final ActivityResultLauncher<String> requestLocationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    Log.d(TAG, "Location permission granted.");
                    getCurrentLocation();
                } else {
                    Log.w(TAG, "Location permission denied.");
                    Toast.makeText(this, "Location permission is required to add a place.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_place_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Upload Place");
        }

        capturedImage = findViewById(R.id.captured_image);
        mapView = findViewById(R.id.map_view);
        submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(v -> submitPlace());

        mapView.onCreate(savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            launchCamera();
        }
    }

    /**
     * Checks for location permissions and then gets the current location.
     */
    private void fetchLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            getCurrentLocation();
        }
    }

    /**
     * Handles the submission of the new place.
     */
    private void submitPlace() {
        // TODO: Implement the POST request logic here.
        // For now, we'll just simulate a successful submission
        Toast.makeText(this, "Submit button clicked!", Toast.LENGTH_SHORT).show();
        setResult(Activity.RESULT_OK);
        finish();
    }

    /**
     * Uses the FusedLocationProviderClient to get the last known location.
     */
    private void getCurrentLocation() {
        // Suppressing the permission check here because this method is only called after the permission has been granted.
        // noinspection MissingPermission
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d(TAG, "Location found: Lat: " + latitude + ", Lon: " + longitude);

                        mapView.setVisibility(View.VISIBLE);
                        submitButton.setVisibility(View.VISIBLE);
                        mapView.getMapAsync(this);
                    } else {
                        Log.w(TAG, "FusedLocationProvider returned null location.");
                        Toast.makeText(this, "Could not get location. Please ensure GPS is enabled.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    /**
     * Creates a temporary file URI and launches the camera intent.
     */
    private void launchCamera() {
        imageUri = createImageUri();
        if (imageUri != null) {
            takePictureLauncher.launch(imageUri);
        } else {
            Log.e(TAG, "Failed to create image URI.");
            Toast.makeText(this, "Error preparing for camera.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Creates a content Uri for a temporary file in the app's cache directory.
     * @return The Uri for the temporary image file.
     */
    private Uri createImageUri() {
        File imageFile = new File(getCacheDir(), "camera_photo.jpg");
        return FileProvider.getUriForFile(this, getPackageName() + ".provider", imageFile);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng location = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(location).title("Your Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
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
