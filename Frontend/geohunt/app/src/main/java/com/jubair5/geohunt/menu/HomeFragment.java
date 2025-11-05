/**
 * Fragment containing the home screen, with single and multiplayer options
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.menu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.game.GameActivity;
import com.jubair5.geohunt.game.LobbyActivity;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "HomeFragment";
    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

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
                    Toast.makeText(getContext(), "Location permission is required to show your position on the map.", Toast.LENGTH_LONG).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        mapView = view.findViewById(R.id.map_view_home);
        Button playButton = view.findViewById(R.id.play_button);
        Button multiplayerButton = view.findViewById(R.id.multiplayer_button);
        Button dummyButton2 = view.findViewById(R.id.dummy_button_2);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GameActivity.class);
            startActivity(intent);
        });

        multiplayerButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LobbyActivity.class);
            startActivity(intent);
        });

        dummyButton2.setOnClickListener(v -> {
            // prompt the user for a lobby ID in an alert and store in a string
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Join Lobby by ID");

            final EditText input = new EditText(requireContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Join", (dialog, which) -> {
                String lobbyId = input.getText().toString();
                if (!lobbyId.isEmpty()) {
                    Intent intent = new Intent(getActivity(), LobbyActivity.class);
                    intent.putExtra("lobbyId", lobbyId);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Lobby ID cannot be empty.", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });


        return view;
    }

    /**
     * This method is called when the map is ready to be used.
     * @param googleMap The GoogleMap object.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        enableMyLocation();
        setMapStyle();
    }

    /**
     * Sets the map style based on the current system theme (dark or light).
     */
    private void setMapStyle() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            Log.d(TAG, "Setting dark mode map style.");
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style_dark));
        }
    }

    /**
     * Checks for location permission, and if granted, enables the 'My Location' layer and
     * moves the camera to the user's current position.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (googleMap != null) {
                googleMap.setMyLocationEnabled(true);
                // Get initial location to center the map
                fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f));
                    } else {
                        Log.w(TAG, "FusedLocationProvider returned null location for initial position.");
                    }
                });
                // Start continuous location updates
                startLocationUpdates();
            }
        } else {
            // Request the permission.
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Configures and starts continuous location updates.
     */
    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 10 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (googleMap != null) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                    }
                }
            }
        };

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; // Should not happen as this is called after permission check
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        // Stop location updates to save battery
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
