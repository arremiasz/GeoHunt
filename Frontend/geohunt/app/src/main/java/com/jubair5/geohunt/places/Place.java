/**
 * Place Class
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.places;

import android.util.Base64;
import android.util.Log;
import org.json.JSONObject;

public class Place {
    private int id;
    private byte[] photoData;
    private double latitude;
    private double longitude;

    public Place(JSONObject json) {
        id = json.optInt("id");
        String base64Photo = json.optString("streetviewurl");
        if (!base64Photo.isEmpty()) {
            try {
                this.photoData = Base64.decode(base64Photo, Base64.DEFAULT);
            } catch (IllegalArgumentException e) {
                Log.e("Place", "Failed to decode Base64 string from server.", e);
                this.photoData = null; // Gracefully handle error by setting data to null
            }
        }
        latitude = json.optDouble("latitude");
        longitude = json.optDouble("longitude");
    }

    public int getId() {
        return id;
    }

    /**
     * Gets the decoded image data as a byte array.
     * @return The photo data, ready to be loaded by Glide.
     */
    public byte[] getPhotoData() {
        return photoData;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
