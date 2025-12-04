/**
 * Activity used to display another account so that you can view stats or friend them
 * @author Nathan Imig
 */
package com.jubair5.geohunt.friends;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;

import java.nio.charset.StandardCharsets;

public class SingleFriendActivity extends AppCompatActivity {

    private static final String TAG = "SingleFriendActivity";
    public static final int NOT_FRIENDS_STATE = 0;
    public static final int RECEIVED_REQUEST_STATE = 1;
    public static final int  SENT_REQUEST_STATE = 2;
    public static final int ARE_FRIENDS_STATE = 3;
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_ID = "userId";


    private int userId;
    private int friendID;
    private String friendUsername;
    private SharedPreferences prefs;
    private TextView nameText;
    private ImageView profilePic;
    private Button friendRequestButton;
    private Button rejectRemoveButton;
    private int state;

    public void onCreate(Bundle savedInstancesState){
        super.onCreate(savedInstancesState);
        setContentView(R.layout.single_friend);
        prefs = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        friendUsername = getIntent().getStringExtra("USERNAME");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(friendUsername);
        }


        nameText = findViewById(R.id.friendName);
        profilePic = findViewById(R.id.friendProfileImage);
        friendRequestButton = findViewById(R.id.friendRequestButton);
        rejectRemoveButton = findViewById(R.id.rejectRemoveButton);

        userId = prefs.getInt(KEY_USER_ID, -1);
        if (userId == -1) {
            Log.e(TAG, "User ID not found in shared preferences.");
            return;
        }

        friendID = getIntent().getIntExtra("FID", 0);
        state = getIntent().getIntExtra("STATE", 0);

        getFriendProfile();
        setState();

        friendRequestButton.setOnClickListener(V-> mainButtonClicked());
        rejectRemoveButton.setOnClickListener(V-> secondaryButtonClicked());


    }


    /**
     * Sets up main button based on relationship state
     */
    private void mainButtonClicked() {
        if(state == NOT_FRIENDS_STATE){
            sendFriendRequest();
        }
        else if (state == RECEIVED_REQUEST_STATE) {
            accpetFriendRequest();;
        }
        else if (state == ARE_FRIENDS_STATE) {
            challengeFriend();
        }
        setState();
    }

    /**
     * Sets up second button based on relationship if needed
     */
    private void secondaryButtonClicked() {
        if (state == RECEIVED_REQUEST_STATE) {
            rejectFriend();
        }
        else if (state == ARE_FRIENDS_STATE) {
            removeFriend();;
        }
        setState();
    }


    /**
     * Sends a Friend Request to from your account to the currently viewed one
     */
    private void sendFriendRequest() {
        String friendsURL = ApiConstants.BASE_URL + ApiConstants.Send_Friend_Request_ENDPOINT + "?primaryId=" + userId + "&targetId=" + friendID;

        StringRequest sendRequest = new StringRequest(
                Request.Method.POST,
                friendsURL,
                response -> {
                    Log.d(TAG, "Request Sent");

                    state = SENT_REQUEST_STATE;

                },
                volleyError -> {
                    Log.e(TAG, "Error Sending Request" + volleyError.toString());
                    String responseBody = "";
                    if(volleyError.networkResponse.data != null) {
                        responseBody = new String(volleyError.networkResponse.data, StandardCharsets.UTF_8);
                    }
                    Log.e(TAG, "Accepting friends error response body: " + responseBody);
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(sendRequest);
    }

    /**
     * Accepts their friend request if they sent on to you
     */
    private void accpetFriendRequest() {
        String friendsURL = ApiConstants.BASE_URL + ApiConstants.Accept_Friend_Request_ENDPOINT + "?primaryId=" + userId + "&targetId=" + friendID ;

        StringRequest acceptRequest = new StringRequest(
                Request.Method.PUT,
                friendsURL,
                response -> {
                    Log.d(TAG, "Response: "+ response.toString());
                    state = ARE_FRIENDS_STATE;

                },
                volleyError -> {
                    Log.e(TAG, "Error accepting request" + volleyError.toString());
                    String responseBody = "";
                    if(volleyError.networkResponse.data != null) {
                        responseBody = new String(volleyError.networkResponse.data, StandardCharsets.UTF_8);
                    }
                    Log.e(TAG, "Accepting friends error response body: " + responseBody);
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(acceptRequest);
    }

    /**
     * Rejects their friend request if they sent on to you
     */
    private void rejectFriend() {

        String friendsURL = ApiConstants.BASE_URL + ApiConstants.Reject_Friend_Request_ENDPOINT + "?primaryId=" + userId + "&targetId=" + friendID ;

        StringRequest rejectRequest = new StringRequest(
                Request.Method.DELETE,
                friendsURL,
                response -> {
                    Log.d(TAG, "Response: " + response.toString());
                    state = NOT_FRIENDS_STATE;

                },
                volleyError -> {
                    Log.e(TAG, "Error removing friends" + volleyError.toString());
                    String responseBody = "";
                    if(volleyError.networkResponse.data != null) {
                        responseBody = new String(volleyError.networkResponse.data, StandardCharsets.UTF_8);
                    }
                    Log.e(TAG, "Accepting friends error response body: " + responseBody);
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(rejectRequest);
    }

    /**
     * Challenges a Friend and sends request to multiplayer lobby
     */
    private void challengeFriend() {
    }

    /**
     * Removes a friend
     */
    private void removeFriend() {
        String friendsURL = ApiConstants.BASE_URL + ApiConstants.Remove_FRIEND + "?primaryId=" + userId + "&targetId=" + friendID ;

        StringRequest removeRequest = new StringRequest(
                Request.Method.DELETE,
                friendsURL,
                response -> {
                    Log.d(TAG, "Response: "+ response);
                    state = NOT_FRIENDS_STATE;

                },
                volleyError -> {
                    Log.e(TAG, "Error removing friends " + volleyError.toString());
                    String responseBody = "";
                    if(volleyError.networkResponse.data != null) {
                        responseBody = new String(volleyError.networkResponse.data, StandardCharsets.UTF_8);
                    }
                    Log.e(TAG, "Accepting friends error response body: " + responseBody);
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(removeRequest);
    }

    /**
     * Sets the state of relationship
     * Help when a button is clicked to change relationship
     */
    private void setState() {
        if(state == NOT_FRIENDS_STATE){
            friendRequestButton.setText("Send Request");
            rejectRemoveButton.setVisibility(TextView.INVISIBLE);
        }
        else if (state == RECEIVED_REQUEST_STATE) {
            friendRequestButton.setText("Accept");
            rejectRemoveButton.setVisibility(TextView.VISIBLE);
            rejectRemoveButton.setText("Reject");
        }
        else if (state == SENT_REQUEST_STATE) {
            friendRequestButton.setText("Pending");
            rejectRemoveButton.setVisibility(TextView.INVISIBLE);
        }
        else if (state == ARE_FRIENDS_STATE) {
            friendRequestButton.setText("Challenge");
            rejectRemoveButton.setVisibility(TextView.VISIBLE);
            rejectRemoveButton.setText("Remove Friend");
        }
    }

    /**
     * Gets all the information from the account
     */
    private void getFriendProfile() {
        nameText.setText(friendUsername);

        String searchURL = ApiConstants.BASE_URL + ApiConstants.GET_ACCOUNT_BY_USERNAME_ENDPOINT + "?name=" + friendUsername;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                searchURL,
                null,
                response -> {
                    Log.d(TAG, "Account Search Response: "+ response.toString());

                    String pfp = response.optString("pfp");
                    if (pfp != null && !pfp.isEmpty()) {
                        byte[] decodedImage = Base64.decode(pfp, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                        profilePic.setImageBitmap(bitmap);
                    } else {
                        profilePic.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                    // Stats later on
                },
                volleyError -> {
                    Log.e(TAG, "Error getting friends" + volleyError.toString());
                    String responseBody = "";
                    if(volleyError.networkResponse.data != null) {
                        responseBody = new String(volleyError.networkResponse.data, StandardCharsets.UTF_8);
                    }
                    Log.e(TAG, "Accepting friends error response body: " + responseBody);
                }
        );
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjReq);
    }
}
