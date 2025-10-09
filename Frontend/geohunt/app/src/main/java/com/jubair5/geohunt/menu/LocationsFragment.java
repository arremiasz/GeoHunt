package com.jubair5.geohunt.menu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationsFragment extends Fragment {

    private static final String TAG = "LocationFragment";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";

    private String currentLocation;
    private ListView locations;
    private Button location;
    private ImageButton addLocalButton;
    private ImageButton backButton;
    private View root;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.locations_fragment, container, false);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Locations");
        }

        // Layout
        //locations = root.findViewById(R.id.locations);
        //locations.setOnClickListener(v -> getLocations());



        // Buttons
        location = root.findViewById(R.id.location_button);
        location.setOnClickListener(v-> goToSingleLocal(new SingleLocationFragment()));

        addLocalButton = root.findViewById(R.id.addLocal);
        addLocalButton.setOnClickListener(v -> gotoAddLocal(new AddLocationFragment()));

        backButton = root.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> goBack(new MapFragment()));

        getLocations();

        return root;
    }

    private boolean goToSingleLocal(Fragment fragment) {
        if (fragment != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void getLocations(){
        String locationsURL = ApiConstants.BASE_URL + ApiConstants.LOCATIONS_ENDPOINT;
        StringRequest locationRequest = new StringRequest(Request.Method.GET, locationsURL,
                locationResponse -> {
                    try {
                        JSONObject userJson = new JSONObject(locationResponse);
                        String name = userJson.getString("name");
                        location.setText(name);
                        currentLocation = name;

                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing locations details from GET request", e);
                        Toast.makeText(getContext(), "Getting successful, but failed to parse user details.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching location details", error);
                });



    }

    private boolean goBack(Fragment fragment) {
        if (fragment != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private boolean gotoAddLocal(Fragment fragment) {
        if (fragment != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}