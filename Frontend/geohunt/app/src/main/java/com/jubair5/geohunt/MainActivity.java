package com.jubair5.geohunt;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jubair5.geohunt.menu.AddLocationFragment;
import com.jubair5.geohunt.menu.HomeFragment;
import com.jubair5.geohunt.menu.LocationsFragment;
import com.jubair5.geohunt.menu.MapFragment;
import com.jubair5.geohunt.menu.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView botNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        botNav = findViewById(R.id.bottom_navigation);
        onNavItemSelected();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Home");
            }
        }
    }

    /**
     * Sets up the listener for bottom navigation item selection
     */
    public void onNavItemSelected() {
        botNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            String title = "";

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
                title = "Home";
            } else if (itemId == R.id.nav_map) {
                fragment = new MapFragment();
                title = "Map";
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment();
                title = "Profile";
            }

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }

            return loadFragment(fragment);
        });
    }

    /**
     * Loads the specified fragment into the fragment container
     * @param fragment The fragment to load
     * @return true if the fragment was loaded successfully, false otherwise
     */
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
