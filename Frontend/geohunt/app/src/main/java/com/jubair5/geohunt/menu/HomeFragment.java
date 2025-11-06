package com.jubair5.geohunt.menu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jubair5.geohunt.R;
import com.jubair5.geohunt.friends.FriendsListActivity;
import com.jubair5.geohunt.reward.theme.ThemeListActivity;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private View root;
    private SharedPreferences prefs;
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";

    private Button friendList;
    private Button themeList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Sets up view and preference
        root = inflater.inflate(R.layout.home_fragment, container, false);
        prefs = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        // Sets up all ui elements
        friendList = root.findViewById(R.id.friendList);
        themeList = root.findViewById(R.id.themeList);

        //Set up button methods
        friendList.setOnClickListener(v -> goToFriends());
        themeList.setOnClickListener(v -> goToThemes());




        return root;
    }

    private void goToFriends() {
        Intent intent = new Intent(getActivity(), FriendsListActivity.class);
        startActivity(intent);
    }

    private void goToThemes() {
        Intent intent = new Intent(getActivity(), ThemeListActivity.class);
        startActivity(intent);
    }
}
