package com.jubair5.geohunt.game;

import android.util.Log;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Model class for a comment on a challenge.
 *
 * @author Alex Remiasz
 */
public class Comment {

    private int uid;
    private String comment;
    private long timestamp;
    private String username;
    private String profilePhotoUrl;

    public Comment(JSONObject json) {
        this.uid = json.optInt("uid");
        this.comment = json.optString("comment");
        this.timestamp = json.optLong("timestamp");
        this.username = "Loading...";
        this.profilePhotoUrl = "";
    }

    public int getUid() {
        return uid;
    }

    public String getComment() {
        return comment;
    }

    public String getFormattedTimestamp() {
        // Assuming timestamp is in milliseconds
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }
}
