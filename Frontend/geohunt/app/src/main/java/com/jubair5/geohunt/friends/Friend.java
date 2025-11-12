/**
 * Friend Class
 * @author Nathan Imig
 */
package com.jubair5.geohunt.friends;

import org.json.JSONObject;

public class Friend {
    private int id;
    private String username;
    private String state;

    // Future Use
    private String photoUrl;


    public Friend(JSONObject json) {
        id = json.optInt("id");
        username = json.optString("username");
        photoUrl = json.optString("pfp");

        // Future Use
        //photoUrl = json.optString("photourl");
    }

    //Future Use
    private String getPhotoUrl(){return photoUrl;}

    public int getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getState() {
        return state;
    }
}
