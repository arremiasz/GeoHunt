package com.jubair5.geohunt.friends;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jubair5.geohunt.R;

public class SingleFriendActivity extends AppCompatActivity {

    private TextView name;
    private ImageView profilePic;
    private Button friendRequestButton;

    private String friendID;

    public void onCreate(Bundle savedInstancesState){
        super.onCreate(savedInstancesState);
        setContentView(R.layout.single_friend);

        name = findViewById(R.id.friendName);
        profilePic = findViewById(R.id.friendProfileImage);
        friendRequestButton = findViewById(R.id.friendRequestButton);

        friendID = getIntent().getStringExtra("FRIEND_ID");
        getFriendProfile();

        friendRequestButton.setOnClickListener(V->sendFriendRequest());

    }

    private void sendFriendRequest() {

    }

    private void getFriendProfile() {
        // Database Integration
    }
}
