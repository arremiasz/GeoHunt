/**
 * Object to handle location submission to the server
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.submissions;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.jubair5.geohunt.network.ApiConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class LocationSubmission {
    private static final String TAG = "LocationSubmission";
    private Location location;

    /**
     * Submits the location to the server
     * @return {@code true} if submission was successful, {@code false} otherwise}
     */
    public boolean performSubmit() {
        final JSONObject requestBody = toJSON();
        String url = ApiConstants.BASE_URL + ApiConstants.SUBMIT_LOCATIONS_ENDPOINT;
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                response -> {
                    // TODO: Handle successful response
                    Log.d(TAG, "Successfully submitted location: " + response);
                },
                error -> {
                    // TODO: Handle error
                    Log.d(TAG, "Error when trying to submit location: " + error);
                }
        ) {
            @Override
            public byte[] getBody() {
                return requestBody.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        return false;
    }

    /**
     * Converts the Location object to a JSON object
     * @return JSON object
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("latitude", location.latitude);
            json.put("longitude", location.longitude);
            json.put("photourl", location.image);
            json.put("creator", location.creator);
        } catch (JSONException e) {
            Log.e(TAG, "Error converting location to JSON", e);
        }
        return json;
    }

    /**
     * Inner class to represent a location
     */
    private class Location {
        protected double latitude;
        protected double longitude;
        protected String image;
        protected int creator;

        /**
         * Constructor for Location class
         * @param lat Latitude
         * @param lon Longitude
         * @param image Image URL
         * @param userId ID of the creator
         */
        protected Location(double lat, double lon, String image, int userId) {
            this.latitude = lat;
            this.longitude = lon;
            this.image = image;
            this.creator = userId;
        }
    }
}
