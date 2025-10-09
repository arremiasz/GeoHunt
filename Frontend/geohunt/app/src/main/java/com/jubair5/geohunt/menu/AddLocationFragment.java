/**
 * Home Page Fragment
 * Displays user information, statistics and account settings.
 * @author Nathan Imig
 */
package com.jubair5.geohunt.menu;

//import android.app.AlertDialog;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;

import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AddLocationFragment extends Fragment {

    private static final String TAG = "LocationsFragment";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";

    private Button location_button;
    private TextInputLayout localNameLayout;
    private TextInputLayout localLatLayout;
    private TextInputLayout localLongLayout;
    private TextInputLayout localRadiusLayout;
    private EditText nameEditText;
    private EditText latEditText;
    private EditText longEditText;
    private EditText radiusEditText;
    private Button submitbutton;
    private ImageButton backButton;
    private SharedPreferences prefs;
    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.add_location_fragment, container, false);

        prefs = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Add a Locations");
        }

        //Layouts
        localNameLayout = root.findViewById(R.id.localNameLayout);
        localLatLayout = root.findViewById(R.id.localLatLayout);
        localLongLayout = root.findViewById(R.id.localLongLayout);
        localRadiusLayout = root.findViewById(R.id.localRadiusLayout);

        // Text fields
        nameEditText = root.findViewById(R.id.localName);
        latEditText = root.findViewById(R.id.localLat);
        longEditText = root.findViewById(R.id.localLong);
        radiusEditText = root.findViewById(R.id.localRadius);

        // Buttons
        submitbutton= root.findViewById(R.id.localSubmit);
        submitbutton.setOnClickListener(v -> submitLocation());

        backButton = root.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> goBack(new LocationsFragment()));

        return root;
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

    private void submitLocation() {
        localNameLayout.setError(null);
        localLatLayout.setError(null);
        localNameLayout.setError(null);
        localRadiusLayout.setError(null);

        String name = nameEditText.getText().toString().trim();
        String latitude = latEditText.getText().toString().trim();
        String longitude = longEditText.getText().toString().trim();
        String radius = radiusEditText.getText().toString().trim();

        if (!validateName(name) || !validateLatitude(latitude) || !validateLongitude(longitude) || !validateRadius(radius)) {
            return;
        }

        Log.d(TAG, "All validations passed. Proceeding with uploading Location.");

        final JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", name);
            requestBody.put("latitude", latitude);
            requestBody.put("longitude", longitude);
            requestBody.put("radius", radius);
            requestBody.put("creator", prefs.getString("userName", "User"));
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON request body", e);
            Toast.makeText(getContext(), "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
            return;
        }

        String localSubmit = ApiConstants.BASE_URL + ApiConstants.ADD_LOCAL_ENDPOINT;
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, localSubmit,
                response -> {
                    Log.d(TAG, "Adding Location successful response: " + response);


                        Toast.makeText(getContext(), "Location Added created successfully!", Toast.LENGTH_LONG).show();

                        goBack(new LocationsFragment());

                    },
                error -> {
                    Log.e(TAG, "Add Location error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Add Location error status code: " + error.networkResponse.statusCode);
                        String responseBody = "";
                        if(error.networkResponse.data != null) {
                            responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        }
                        Log.e(TAG, "Add Location error response body: " + responseBody);

                        if (error.networkResponse.statusCode == 400) {
                            Toast.makeText(getContext(), "Account already exists or invalid input.", Toast.LENGTH_LONG).show();
                            localNameLayout.setError("Location already made");
                            nameEditText.requestFocus();
                        } else {
                            Toast.makeText(getContext(), "Adding Location failed. Server error: " + error.networkResponse.statusCode, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Adding Location failed failed. Check network connection.", Toast.LENGTH_LONG).show();
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

    /**
     * Validates the provided name.
     * A valid email must not be empty
     *
     * @param name The email string to validate.
     * @return {@code true} if the name is valid, {@code false} otherwise.
     */
    private boolean validateName(String name) {
        if (TextUtils.isEmpty(name)) {
            localNameLayout.setError("Name cannot be empty");
            nameEditText.requestFocus();
            return false;
        }

        localNameLayout.setError(null);
        return true;
    }

    /**
     * Validates the provided latitude if it is range:
     * -
     *
     * @param latitude The latitude string to validate.
     * @return {@code true} if the latitude meets all criteria, {@code false} otherwise.
     */
    private boolean validateLatitude(String latitude) {
        if (Double.parseDouble(latitude) < -90 || Double.parseDouble(latitude) > 90) {
            localLatLayout.setError("Latitude not in range");
            latEditText.requestFocus();
            return false;
        }

        localLatLayout.setError(null);
        return true;
    }

    /**
     * Validates the provided longitude if it is range:
     * -
     *
     * @param longitude The longitude string to validate.
     * @return {@code true} if the longitude meets all criteria, {@code false} otherwise.
     */
    private boolean validateLongitude(String longitude) {
        if (Double.parseDouble(longitude) < -180 || Double.parseDouble(longitude) > 180) {
            localLongLayout.setError("longitude not in range");
            longEditText.requestFocus();
            return false;
        }

        localLongLayout.setError(null);
        return true;
    }

    /**
     * Validates the provided Radius if it is range:
     * -
     *
     * @param Radius The Radius string to validate.
     * @return {@code true} if the Radius meets all criteria, {@code false} otherwise.
     */
    private boolean validateRadius(String Radius) {
        if (!(Double.parseDouble(Radius) > 0)) {
            localRadiusLayout.setError("Radius needs to be positive");
            radiusEditText.requestFocus();
            return false;
        }

        localRadiusLayout.setError(null);
        return true;
    }


}
