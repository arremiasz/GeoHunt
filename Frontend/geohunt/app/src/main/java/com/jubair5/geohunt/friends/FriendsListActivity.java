/**
 * Activity for displaying details of a place.
 * @author Nathan Imig
 */
package com.jubair5.geohunt.friends;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jubair5.geohunt.R;

import java.util.ArrayList;
import java.util.List;

public class FriendsListActivity extends AppCompatActivity {

    private RecyclerView friendsRecycleViewer;
    private FriendAdapter friendAdapter;
    private List<Friend> friendList;

    @Override
    protected void onCreate(Bundle savedInstancesState){
        super.onCreate(savedInstancesState);

        setContentView(R.layout.friends_list_activity);

        friendsRecycleViewer = findViewById(R.id.friends_recycler_view);
        friendsRecycleViewer.setLayoutManager(new LinearLayoutManager(this));

        // Get all of the data
        friendList = new ArrayList<>();
        friendAdapter = new FriendAdapter(getBaseContext(), friendList, this);

    }
}
