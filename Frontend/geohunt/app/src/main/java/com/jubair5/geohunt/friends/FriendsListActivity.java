/**
 * Activity for displaying details of a place.
 * @author Nathan Imig
 */
package com.jubair5.geohunt.friends;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;
import com.jubair5.geohunt.places.AddPlaceActivity;
import com.jubair5.geohunt.places.Place;
import com.jubair5.geohunt.places.PlaceDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendsListActivity extends AppCompatActivity {

    private static final String TAG = "FriendsList";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_ID = "userId";

    private EditText searchBar;
    private Button searchButton;


    // Set up for the actual list
    private RecyclerView friendsRecycleViewer;
    private FriendAdapter friendAdapter;
    private List<Friend> friendList;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstancesState){
        super.onCreate(savedInstancesState);
        setContentView(R.layout.friends_list_activity);
        prefs = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Search for Friends");
        }

        // Set up Ui elements
        searchBar = findViewById(R.id.search_bar);
        searchButton = findViewById(R.id.search_button);
        friendsRecycleViewer = findViewById(R.id.friends_recycler_view);

        friendsRecycleViewer.setLayoutManager(new LinearLayoutManager(this));

        friendList = new ArrayList<>();
        friendAdapter = new FriendAdapter(getBaseContext(), friendList, friend ->{
            Intent intent = new Intent(FriendsListActivity.this, SingleFriendActivity.class);
            intent.putExtra("FRIEND_USERNAME", friend.getUsername());
            startActivity(intent);
        });

        friendsRecycleViewer.setAdapter(friendAdapter);

        searchButton.setOnClickListener(v->searchForFriends(searchBar.getText().toString().trim()));


    }

    private void searchForFriends(String name){


        /** Removed for testing purposes
        int userId = prefs.getInt(KEY_USER_ID, -1);
        if (userId == -1) {
            Log.e(TAG, "User ID not found in shared preferences.");
            return;
        }
         */

        String url = ApiConstants.BASE_URL + ApiConstants.GET_SUBMITTED_PLACES_ENDPOINT + "?id=" + name;
        StringRequest userDetailsRequest = new StringRequest(Request.Method.GET, url,
                userDetailsResponse -> {
                    try {
                        JSONObject userJson = new JSONObject(userDetailsResponse);
                        String email = userJson.getString("email");
                        String pfp = userJson.getString("pfp");
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing user details from GET request", e);
                        Toast.makeText(getBaseContext(), "Login successful, but failed to parse user details.", Toast.LENGTH_LONG).show();
                    }
                });

    }

    public void onFriend(Friend friend) {
        Intent intent = new Intent(FriendsListActivity.this, PlaceDetailActivity.class);
        int uid = prefs.getInt(KEY_USER_ID, -1);
        if (uid == -1) {
            Log.e(TAG, "User ID not found in shared preferences.");
            return;
        }
        intent.putExtra("UID", uid);
        intent.putExtra("FID", friend.getId());
        intent.putExtra("USERNAME", friend.getUsername());
        intent.putExtra("STATE", friend.getState());
        startActivity(intent);
    }


}
