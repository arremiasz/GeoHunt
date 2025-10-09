package com.jubair5.geohunt.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputLayout;
import com.jubair5.geohunt.R;

public class LocationsFragment extends Fragment {

    private static final String TAG = "LocationFragment";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_LOGGED_IN = "isUserLoggedIn";
    private static final String KEY_LOGIN_TIMESTAMP = "loginTimestamp";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PFP = "userPfp";


    private ListView locations;
    private ImageButton addLocalButton;
    private ImageButton backButton;
    private View root;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.locations_fragment, container, false);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Locations");
        }

        // Layout
        locations = root.findViewById(R.id.locations);
        locations.setOnClickListener(v -> getLocations());


        // Buttons
        addLocalButton = root.findViewById(R.id.addLocal);
        addLocalButton.setOnClickListener(v -> gotoAddLocal());

        backButton = root.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> goBack());


        return root;
    }

    private void getLocations(){

    }

    private void goBack() {
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, new LocationsFragment())
                    .commit();
        }
    }

    private void gotoAddLocal() {
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, new AddLocationFragment())
                    .commit();
        }
    }
}