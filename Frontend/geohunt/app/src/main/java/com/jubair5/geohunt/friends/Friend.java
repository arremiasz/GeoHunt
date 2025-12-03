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


    /**
     * Creates a friend and relationship based on json
     * @param json
     */
    public Friend(JSONObject json) {
        id = json.optInt("id");
        username = json.optString("username");
        photoUrl = json.optString("pfp");
        state = json.optInt("state");
    }

    /**
     * Gets the decoded image data as a byte array.
     * @return The photo data, ready to be loaded by Glide.
     */
    private String getPhotoUrl(){return photoUrl;}

    /**
     * returns the id of the account
     * @return id of the account
     */
    public int getId() {
        return id;
    }

    /**
     * returns the username of the account
     * @return username of the account
     */
    public String getUsername() {
        return username;
    }

    /**
     * returns the state of relationship
     * @return state of relationship
     */
    public int getState() {
        return state;
    }

    /**
     * sets the state of the relationship
     * @param state
     */
    public void setState(int state) {
        this.state = state;
    }
}
