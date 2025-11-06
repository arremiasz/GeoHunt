/**
 * Activity for displaying a list of friends.
 * @author Nathan Imig
 */
package com.jubair5.geohunt.friends;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;
import com.jubair5.geohunt.places.Place;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendsListActivity extends AppCompatActivity implements FriendAdapter.OnFriendClickListener{

    private static final String TAG = "FriendsList";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_ID = "userId";

    private EditText searchBar;
    private Button searchButton;
    private TextView friendUsername;
    private Friend freind;


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
        friendAdapter = new FriendAdapter(getBaseContext(), friendList, this);

        friendsRecycleViewer.setAdapter(friendAdapter);

        searchButton.setOnClickListener(v->searchForFriends(searchBar.getText().toString().trim()));


    }

    private void searchForFriends(String name){


        String searchURL = ApiConstants.BASE_URL + ApiConstants.GET_ACCOUNT_BY_USERNAME_ENDPOINT + "?name=" + name;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                searchURL,
                null,
                response -> {
                    Log.d(TAG, "Response: "+ response.toString());

                    // Display Friends
                    friendList.clear();
                    friendList.add(new Friend(response));
                    friendAdapter.notifyDataSetChanged();
                },
                volleyError -> {
                    Log.e(TAG, "Error getting friends", volleyError);
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);





    }

    @Override
    public void onFriendClick(Friend friend) {
        Intent intent = new Intent(FriendsListActivity.this, SingleFriendActivity.class);
        int uid = prefs.getInt(KEY_USER_ID, -1);
        if (uid == -1) {
            Log.e(TAG, "User ID not found in shared preferences.");
            return;
        }
        intent.putExtra("FID", friend.getId());
        intent.putExtra("USERNAME", friend.getUsername());
        startActivity(intent);
    }


}
