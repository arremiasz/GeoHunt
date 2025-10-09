/**
 * Home Page Fragment
 * Displays user information, statistics and account settings.
 * @author Nathan Imig
 */
package com.jubair5.geohunt.menu;

//import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.jubair5.geohunt.R;

public class MapFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private Button location_button;
    private SharedPreferences prefs;
    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.map_fragment, container, false);

        prefs = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);


        location_button= root.findViewById(R.id.location_button);
        location_button.setOnClickListener(v -> showLocationMenu());



        return root;
    }

    private void showLocationMenu() {
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, new LocationsFragment())
                    .commit();
        }
    }

}

