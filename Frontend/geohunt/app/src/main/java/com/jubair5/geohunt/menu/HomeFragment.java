package com.jubair5.geohunt.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.jubair5.geohunt.R;
import com.jubair5.geohunt.places.AddPlaceActivity;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private Button FriendList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_list_activity)

    }






    private final ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.d(TAG, "Returned from an activity with success. Refreshing submissions.");
                }
            });

    @Override
    public void onFreindsClick() {
        Intent intent = new Intent(getActivity(), AddPlaceActivity.class);
        activityLauncher.launch(intent);
    }
}
