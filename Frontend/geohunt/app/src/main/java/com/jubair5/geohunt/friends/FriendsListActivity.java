/**
 * Activity for displaying details of a place.
 * @author Nathan Imig
 */
package com.jubair5.geohunt.friends;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;
import com.jubair5.geohunt.places.Place;

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
        friendsRecycleViewer = findViewById(R.id.friends_recycler_view);

        friendsRecycleViewer.setLayoutManager(new LinearLayoutManager(this));

        friendList = new ArrayList<>();
        friendAdapter = new FriendAdapter(getBaseContext(), friendList, friend ->{
            Intent intent = new Intent(FriendsListActivity.this, SingleFriendActivity.class);
            intent.putExtra("FRIEND_ID", friend.getId());
            startActivity(intent);
        });

        friendsRecycleViewer.setAdapter(friendAdapter);

        // Search Bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searchForFriends(s.toString().trim());
            }
        });



    }

    private void searchForFriends(String name){
        int userId = prefs.getInt(KEY_USER_ID, -1);
        if (userId == -1) {
            Log.e(TAG, "User ID not found in shared preferences.");
            return;
        }

        String url = ApiConstants.BASE_URL + ApiConstants.GET_FRIENDS_ENDPOINT + "?id=" + userId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "Response: " + response.toString());
                    try {
                        friendList.clear();
                        for (int i = response.length() - 1; i >= 0; i--) {
                            JSONObject friendObject = response.getJSONObject(i);
                            friendList.add(new Friend(friendObject));
                        }
                        friendAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing places JSON", e);
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching places", error);
                });

        //VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
    }


}
