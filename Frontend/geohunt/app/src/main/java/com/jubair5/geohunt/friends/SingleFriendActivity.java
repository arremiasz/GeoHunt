package com.jubair5.geohunt.friends;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;

public class SingleFriendActivity extends AppCompatActivity {

    private static final String TAG = "SingleFriendActivity";
    private static final int NOT_FRIENDS_STATE = 0;
    private static final int SENT_REQUEST_STATE = 1;
    private static final int RECEIVED_REQUEST_STATE = 2;
    private static final int ARE_FRIENDS_STATE = 3;
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_ID = "userId";


    private int userId;
    private int friendID;
    private SharedPreferences prefs;
    private TextView nameText;
    private ImageView profilePic;
    private Button friendRequestButton;
    private int state;

    public void onCreate(Bundle savedInstancesState){
        super.onCreate(savedInstancesState);
        setContentView(R.layout.single_friend);
        prefs = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("USERNAME"));
        }


        nameText = findViewById(R.id.friendName);
        profilePic = findViewById(R.id.friendProfileImage);
        friendRequestButton = findViewById(R.id.friendRequestButton);

        userId = prefs.getInt(KEY_USER_ID, -1);
        if (userId == -1) {
            Log.e(TAG, "User ID not found in shared preferences.");
            return;
        }

        friendID = getIntent().getIntExtra("FID", 0);
        getFriendProfile();

        friendRequestButton.setOnClickListener(V->buttonClicked());


    }

    private void buttonClicked() {
        if(state == NOT_FRIENDS_STATE){
            sendFriendRequest();
        }
        else if (state == RECEIVED_REQUEST_STATE) {
            accpetFriendRequest();
        }
        else if (state == ARE_FRIENDS_STATE) {
            removeFriend();
        }

        setState();

    }

    private void setState() {
        if(state == NOT_FRIENDS_STATE){
            friendRequestButton.setText("Send Friend Request");
        }
        else if (state == SENT_REQUEST_STATE) {
            friendRequestButton.setText("Pending");
        }
        else if (state == RECEIVED_REQUEST_STATE) {
            friendRequestButton.setText("Accept");
        }
        else if (state == ARE_FRIENDS_STATE) {
            friendRequestButton.setText("Unfriend");
        }
    }


    private void sendFriendRequest() {
        String friendsURL = ApiConstants.BASE_URL + ApiConstants.Send_Friend_Request_ENDPOINT + "?primaryId=" + userId + "&targetId=" + friendID;

        // Getting state
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,
                friendsURL,
                null,
                response -> {
                    Log.d(TAG, "Response: "+ response.toString());

                    state = SENT_REQUEST_STATE;

                },
                volleyError -> {
                    Log.e(TAG, "Error Sending Request", volleyError);
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }

    private void accpetFriendRequest() {
        String friendsURL = ApiConstants.BASE_URL + ApiConstants.Accept_Friend_Request_ENDPOINT + "?primaryId=" + userId + "&targetId=" + friendID ;

        // Getting state
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.PUT,
                friendsURL,
                null,
                response -> {
                    Log.d(TAG, "Response: "+ response.toString());
                    state = ARE_FRIENDS_STATE;

                },
                volleyError -> {
                    Log.e(TAG, "Error accepting request", volleyError);
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }

    private void removeFriend() {
        String friendsURL = ApiConstants.BASE_URL + ApiConstants.Remove_FRIEND + "?primaryId=" + userId + "&targetId=" + friendID ;

        // Getting state
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.DELETE,
                friendsURL,
                null,
                response -> {
                    Log.d(TAG, "Response: "+ response.toString());
                    state = NOT_FRIENDS_STATE;

                },
                volleyError -> {
                    Log.e(TAG, "Error removing friends", volleyError);
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }

    private void getFriendProfile() {
        String name = getIntent().getStringExtra("USERNAME");

        nameText.setText(name);

        String friendsURL = ApiConstants.BASE_URL + ApiConstants.GET_FRIENDS_ENDPOINT + "?primaryId=" + userId + "&targetId=" + friendID ;

        // Getting state
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                friendsURL,
                null,
                response -> {
                    Log.d(TAG, "Response: "+ response.toString());

                    state = Integer.parseInt(response.optString("state"));

                    // Not friends
                    if(state == NOT_FRIENDS_STATE){
                        friendRequestButton.setText("Send Friend Request");
                    }
                    else if (state == SENT_REQUEST_STATE) {
                        friendRequestButton.setText("Pending");
                    }
                    else if (state == RECEIVED_REQUEST_STATE) {
                        friendRequestButton.setText("Accept");
                    }
                    else if (state == ARE_FRIENDS_STATE) {
                        friendRequestButton.setText("Unfriend");
                    }


                },
                volleyError -> {
                    Log.e(TAG, "Error getting friends", volleyError);
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }
}
