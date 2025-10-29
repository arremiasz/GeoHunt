/**
 * Place Class
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.places;

import org.json.JSONObject;

public class Place {
    private int id;
    private String photoUrl;
    private double latitude;
    private double longitude;

    public Place(JSONObject json) {
        id = json.optInt("id");
        photoUrl = json.optString("photourl");
        latitude = json.optDouble("latitude");
        longitude = json.optDouble("longitude");
    }

    public int getId() {
        return id;
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
