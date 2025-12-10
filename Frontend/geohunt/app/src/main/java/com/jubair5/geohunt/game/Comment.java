package com.jubair5.geohunt.game;

import android.util.Log;

import org.json.JSONObject;

import java.text.ParseException;
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
    private String timestamp;
    private String username;
    private String profilePhotoUrl;

    public Comment(JSONObject json) {
        this.uid = json.optInt("id");
        this.comment = json.optString("comment");
        this.timestamp = json.optString("timeStamp");

        JSONObject author = json.optJSONObject("author");
        if (author != null) {
            this.username = author.optString("username", "Unknown");
            this.profilePhotoUrl = author.optString("pfp", "");
        } else {
            this.username = "Unknown";
            this.profilePhotoUrl = "";
        }
    }

    public int getUid() {
        return uid;
    }

    public String getComment() {
        return comment;
    }

    public String getFormattedTimestamp() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(timestamp);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            Log.e("Comment", "Error parsing timestamp: " + timestamp, e);
        }
        return timestamp;
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
