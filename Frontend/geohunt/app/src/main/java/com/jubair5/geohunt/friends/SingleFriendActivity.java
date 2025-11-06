package com.jubair5.geohunt.friends;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;

public class SingleFriendActivity extends AppCompatActivity {

    private static final String TAG = "SingleFriendActivity";
    private TextView nameText;
    private ImageView profilePic;
    private Button friendRequestButton;

    private int friendID;

    public void onCreate(Bundle savedInstancesState){
        super.onCreate(savedInstancesState);
        setContentView(R.layout.single_friend);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("USERNAME"));
        }

        nameText = findViewById(R.id.friendName);
        profilePic = findViewById(R.id.friendProfileImage);
        friendRequestButton = findViewById(R.id.friendRequestButton);

        getFriendProfile();

        friendRequestButton.setOnClickListener(V->sendFriendRequest());


    }

    private void sendFriendRequest() {

    }

    private void getFriendProfile() {
        //Testing
        //int userId = prefs.getInt(KEY_USER_ID, -1);
        int userId = 45327;
        if (userId == -1) {
            Log.e(TAG, "User ID not found in shared preferences.");
            return;
        }

        friendID = getIntent().getIntExtra("FID", 0);
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
                    
                    if(reponse.)



                },
                volleyError -> {
                    Log.e(TAG, "Error getting friends", volleyError);
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }
}
