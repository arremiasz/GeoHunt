/**
 * Object to handle location submission to the server
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.menu;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.jubair5.geohunt.network.ApiConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class LocationSubmission {
    private class Location {
        private static final String TAG = "LocationSubmission";
        private double latitude;
        private double longitude;
        private String image;
        private int creator;

        /**
         * Constructor for Location class
         * @param lat Latitude
         * @param lon Longitude
         * @param image Image URL
         * @param userId ID of the creator
         */
        public Location(double lat, double lon, String image, int userId) {
            this.latitude = lat;
            this.longitude = lon;
            this.image = image;
            this.creator = userId;
        }

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
                json.put("latitude", latitude);
                json.put("longitude", longitude);
                json.put("image", image);
                json.put("creator", creator);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }
    }
}
