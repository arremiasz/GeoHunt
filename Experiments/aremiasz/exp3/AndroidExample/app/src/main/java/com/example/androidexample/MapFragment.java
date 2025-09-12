package com.example.androidexample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import android.annotation.SuppressLint;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MapFragment extends Fragment {

    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.map_fragment, container, false);

        // Initialize osmdroid configuration
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        mapView = root.findViewById(R.id.osm_map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Set default location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        centerOnUser();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume(); // needed for osmdroid
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();  // needed for osmdroid
    }

    @SuppressLint("MissingPermission")
    private void centerOnUser() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    IMapController mapController = mapView.getController();
                    mapController.setZoom(15.0);
                    GeoPoint userPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mapController.setCenter(userPoint);
                }
            });
        }
    }
}