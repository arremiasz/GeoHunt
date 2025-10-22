/**
 * Home Page Fragment
 * Displays user information, statistics and account settings.
 * @author Nathan Imig
 */
package com.jubair5.geohunt.menu;

//import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.menu.map.AddLocationFragment;
import com.jubair5.geohunt.menu.map.LocationsFragment;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MapFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private Button play_button;
    private ImageButton getLocation_Ibutton;
    private ImageButton addLocation_Ibutton;
    private SharedPreferences prefs;
    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.map_fragment, container, false);

        prefs = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);


        getLocation_Ibutton= root.findViewById(R.id.location_button);
        getLocation_Ibutton.setOnClickListener(v -> changeFragment(new LocationsFragment()));

        addLocation_Ibutton= root.findViewById(R.id.addLocal);
        addLocation_Ibutton.setOnClickListener(v -> changeFragment(new AddLocationFragment()));

        play_button= root.findViewById(R.id.play_button);
        play_button.setOnClickListener(v -> generateLocation());




        return root;
    }

    // Take in location
    private void generateLocation() {



        int radius = getRadius();

        Log.d(TAG, "All validations passed. Proceeding with uploading Play.");

        final JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("latitude", 0);
            requestBody.put("longitude", 0);
            requestBody.put("radius", radius);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON request body", e);
            Toast.makeText(getContext(), "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentLocal = ApiConstants.BASE_URL + "/GenerateLocation";
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, currentLocal,
                response -> {
                    Log.d(TAG, "Adding Location successful response: " + response);


                    Toast.makeText(getContext(), "Location Added created successfully!", Toast.LENGTH_LONG).show();
                },
                error -> {
                    Log.e(TAG, "Generating Location error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Generating Location error status code: " + error.networkResponse.statusCode);
                        String responseBody = "";
                        if(error.networkResponse.data != null) {
                            responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        }
                        Log.e(TAG, "Generating Location error response body: " + responseBody);

                    } else {
                        Toast.makeText(getContext(), "Generating Location failed failed. Check network connection.", Toast.LENGTH_LONG).show();
                    }
                }
            ) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return requestBody.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders() != null ? super.getHeaders() : new HashMap<>();
            }
        };

        if (getContext() != null) {
            VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
        }
    }

    private int getRadius() {
        return 0;
    }


    private boolean changeFragment(Fragment fragment) {
            if (fragment != null) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                return true;
            }
            return false;
    }

    /**
     * Validates the provided Radius if it is range:
     * -
     *
     * @param Radius The Radius string to validate.
     * @return {@code true} if the Radius meets all criteria, {@code false} otherwise.
     */
    private boolean validateLocation(String Radius) {
        if (!(Double.parseDouble(Radius) > 0)) {

            return true;
        }
        return false;
    }

}

