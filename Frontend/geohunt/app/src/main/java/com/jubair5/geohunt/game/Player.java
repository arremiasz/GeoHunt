/**
 * Player class to handle websocket players
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.game;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;

import org.json.JSONException;

public class Player {
    private String userId;
    private String username;

    /**
     * Constructs a new Player and fetches their ID from the server.
     * @param username The username of the player.
     * @param context The application context for making network requests.
     */
    public Player(String username, Context context) {
        this.username = username;
        fetchUserId(context);
    }

    /**
     * Fetches the user ID from the server based on the username.
     * @param context The application context for making network requests.
     */
    private void fetchUserId(Context context) {
        String url = Uri.parse(ApiConstants.BASE_URL + ApiConstants.GET_ACCOUNT_BY_USERNAME_ENDPOINT)
                .buildUpon()
                .appendQueryParameter("name", this.username)
                .build().toString();

        Log.d("Player", "Fetching user ID from: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Assuming the server returns a JSON object with an "id" field
                        this.userId = String.valueOf(response.getInt("id"));
                        Log.d("Player", "Successfully fetched userId: " + this.userId + " for user: " + this.username);
                    } catch (JSONException e) {
                        Log.e("Player", "Error parsing userId from response for user: " + this.username, e);
                    }
                },
                error -> {
                    Log.e("Player", "Error fetching userId for user: " + this.username, error);
                }
        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }


    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
