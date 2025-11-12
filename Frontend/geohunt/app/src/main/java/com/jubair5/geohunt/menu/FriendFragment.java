package com.jubair5.geohunt.menu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.friends.Friend;
import com.jubair5.geohunt.friends.FriendAdapter;
import com.jubair5.geohunt.friends.SingleFriendActivity;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment implements FriendAdapter.OnFriendClickListener {


    private static final String TAG = "FriendsList";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_ID = "userId";

    private SearchView searchBar;
    private Button searchButton;


    // Set up for the actual list
    private RecyclerView friendsRecycleViewer;
    private FriendAdapter friendAdapter;
    private List<Friend> friendList;
    private SharedPreferences prefs;
    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.friends_fragment, container, false);
        prefs = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);


        // Set up Ui elements
        searchBar = root.findViewById(R.id.friend_search_bar);
        friendsRecycleViewer = root.findViewById(R.id.friends_recycler_view);

        friendList = new ArrayList<>();



        friendAdapter = new FriendAdapter(getContext(), friendList, this);
        friendsRecycleViewer.setAdapter(friendAdapter);
        friendsRecycleViewer.setLayoutManager(new LinearLayoutManager((getContext())));
        getStartingFriends();

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                searchForFriends(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        }
        );


        return root;
    }

    private void searchForFriends(String name){
        String searchURL = ApiConstants.BASE_URL + ApiConstants.GET_ACCOUNT_BY_USERNAME_ENDPOINT + "?name=" + name;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                searchURL,
                null,
                response -> {
                    Log.d(TAG, "Account Search Response: "+ response.toString());

                    // Display Friends
                    friendList.clear();
                    friendList.add(new Friend(response));
                    friendAdapter.notifyDataSetChanged();
                },
                volleyError -> {
                    Log.e(TAG, "Error getting friends", volleyError);
                    if (volleyError.networkResponse != null) {
                        Log.e(TAG, "Friends error status code: " + volleyError.networkResponse.statusCode);
                        String responseBody = "";
                        if(volleyError.networkResponse.data != null) {
                            responseBody = new String(volleyError.networkResponse.data, StandardCharsets.UTF_8);
                        }
                        Log.e(TAG, "Friends error response body: " + responseBody);

                        if (volleyError.networkResponse.statusCode == 404) {
                            friendList.clear();;
                            friendAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Finding Account failed. Server error: " + volleyError.networkResponse.statusCode, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Getting account failed. Check network connection.", Toast.LENGTH_LONG).show();
                    }
                }
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonObjReq);
    }


    private void getStartingFriends(){
        Log.e(TAG, "Made it into the function");
        int userId = prefs.getInt(KEY_USER_ID, -1);
        if (userId == -1) {
            Log.e(TAG, "User ID not found in shared preferences.");
            return;
        }


        // Friends
        String friendsURL = ApiConstants.BASE_URL + ApiConstants.GET_FRIENDS_ENDPOINT + "?id=" + userId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                friendsURL,
                null,
                response -> {
                    try{
                        Log.d(TAG, "Friends Response: "+ response.toString());

                        friendList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject friendJson = response.getJSONObject(i);
                            friendList.add(new Friend(friendJson));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing Friends Json", e);
                    }

                },
                VolleyError -> {
                    Log.e(TAG, "Error getting Friends", VolleyError);
                }
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);


        friendsURL = ApiConstants.BASE_URL + ApiConstants.GET_SENT_FRIENDS_ENDPOINT + "?id=" + userId;
        jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                friendsURL,
                null,
                response -> {
                    try{
                        Log.d(TAG, "People who sent Response: "+ response.toString());

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject friendJson = response.getJSONObject(i);
                            friendList.add(new Friend(friendJson));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing Friends Json", e);
                    }

                },
                VolleyError -> {
                    Log.e(TAG, "Error getting Friends", VolleyError);
                }
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);


        friendsURL = ApiConstants.BASE_URL + ApiConstants.GET_Received_FRIENDS_ENDPOINT + "?id=" + userId;
        jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                friendsURL,
                null,
                response -> {
                    try{
                        Log.d(TAG, "People you sent Response: "+ response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject friendJson = response.getJSONObject(i);
                            friendList.add(new Friend(friendJson));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing Friends Json", e);
                    }

                },
                VolleyError -> {
                    Log.e(TAG, "Error getting Friends", VolleyError);
                }
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);

        friendAdapter.notifyDataSetChanged();




    }

    @Override
    public void onFriendClick(Friend friend) {
        Intent intent = new Intent(getActivity(), SingleFriendActivity.class);
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
