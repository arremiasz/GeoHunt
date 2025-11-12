/**
 * Friend Class
 * @author Nathan Imig
 */
package com.jubair5.geohunt.friends;

import org.json.JSONObject;

public class Friend {
    private int id;
    private String username;
    private int state;

    // Future Use
    private String photoUrl;


    public Friend(JSONObject json) {
        id = json.optInt("id");
        username = json.optString("username");
        photoUrl = json.optString("pfp");
        state = json.optInt("state");
    }

    private String getPhotoUrl(){return photoUrl;}
    public int getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public int getState() {
        return state;
    }
}
