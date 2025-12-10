/**
 * Photo Class - Represents a submission photo from the gallery
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.game;

import android.util.Base64;
import android.util.Log;
import org.json.JSONObject;

public class Photo {
    private String photoUrl;
    private byte[] photoData;

    public Photo(JSONObject json) {
        photoUrl = json.optString("photourl");
        if (!photoUrl.isEmpty()) {
            try {
                this.photoData = Base64.decode(photoUrl, Base64.DEFAULT);
            } catch (IllegalArgumentException e) {
                Log.e("Photo", "Failed to decode Base64 string from server.", e);
                this.photoData = null; // Gracefully handle error by setting data to null
            }
        }
    }

    /**
     * Gets the decoded image data as a byte array.
     * 
     * @return The photo data, ready to be loaded by Glide.
     */
    public byte[] getPhotoData() {
        return photoData;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
