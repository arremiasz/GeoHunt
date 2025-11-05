/**
 * Activity for displaying details of a place.
 * @author Nathan Imig
 */
package com.jubair5.geohunt.friends;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jubair5.geohunt.R;

import java.util.ArrayList;
import java.util.List;

public class FriendsListActivity extends AppCompatActivity {

    private EditText searchBar;


    // Set up for the actual list
    private RecyclerView friendsRecycleViewer;
    private FriendAdapter friendAdapter;
    private List<Friend> friendList;

    @Override
    protected void onCreate(Bundle savedInstancesState){
        super.onCreate(savedInstancesState);
        setContentView(R.layout.friends_list_activity);

        // Sets up the title
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
        if(name.isEmpty()){
            friendList.clear();
            friendAdapter.notifyDataSetChanged();
            return;
        }
        // Get users to backend
    }


}
