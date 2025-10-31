package com.jubair5.geohunt.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jubair5.geohunt.R;
import com.jubair5.geohunt.friends.FriendsListActivity;
import com.jubair5.geohunt.places.AddPlaceActivity;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private View root;
    private SharedPreferences prefs;
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";

    private Button friendList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.home_fragment, container, false);
        prefs = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        friendList = root.findViewById(R.id.friendList);
        friendList.setOnClickListener(v -> goToFriends());




        return root;
    }

    private void goToFriends() {
        Intent intent = new Intent(getActivity(), FriendsListActivity.class);
        startActivity(intent);
    }
}
