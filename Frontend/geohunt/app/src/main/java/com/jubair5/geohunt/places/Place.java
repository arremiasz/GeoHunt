/**
 * Place Class
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.places;

import org.json.JSONObject;

public class Place {
    private String photoUrl;
    private double latitude;
    private double longitude;

    public Place(JSONObject json) {
        photoUrl = json.optString("photourl");
        latitude = json.optDouble("latitude");
        longitude = json.optDouble("longitude");
    }

    public String getImageUrl() {
        return photoUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
