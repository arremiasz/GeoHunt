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
        //locations = root.findViewById(R.id.locations);
        //locations.setOnClickListener(v -> getLocations());


        // Buttons
        addLocalButton = root.findViewById(R.id.addLocal);
        addLocalButton.setOnClickListener(v -> gotoAddLocal(new AddLocationFragment()));

        backButton = root.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> goBack(new MapFragment()));


        return root;
    }

    private void getLocations(){

    }

    private boolean goBack(Fragment fragment) {
        if (fragment != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private boolean gotoAddLocal(Fragment fragment) {
        if (fragment != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}