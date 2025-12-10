package com.jubair5.geohunt;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jubair5.geohunt.menu.HomeFragment;
import com.jubair5.geohunt.menu.FriendFragment;
import com.jubair5.geohunt.menu.ProfileFragment;
import com.jubair5.geohunt.menu.ShopFragment;

/**
 * Activity that hosts the main navigation and fragments
 * @author Alex Remiasz
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView botNav;
    private final FragmentManager fm = getSupportFragmentManager();
    private final Fragment homeFragment = new HomeFragment();
    private final Fragment friendFragment = new FriendFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private final Fragment shopFragment = new ShopFragment();
    private Fragment active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        active = homeFragment;
        botNav = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            fm.beginTransaction().add(R.id.fragment_container, shopFragment, "4").hide(shopFragment).commit();
            fm.beginTransaction().add(R.id.fragment_container, profileFragment, "3").hide(profileFragment).commit();
            fm.beginTransaction().add(R.id.fragment_container, friendFragment, "2").hide(friendFragment).commit();
            fm.beginTransaction().add(R.id.fragment_container, homeFragment, "1").commit();

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Home");
            }
        }

        onNavItemSelected();
    }

    /**
     * Sets up the listener for bottom navigation item selection.
     */
    public void onNavItemSelected() {
        botNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            String title = "";

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = homeFragment;
                title = "Home";
            } else if (itemId == R.id.nav_friends) {
                selectedFragment = friendFragment;
                title = "Friends";
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = profileFragment;
                title = "Profile";
            } else if (itemId == R.id.nav_shop) {
                selectedFragment = shopFragment;
                title = "Shop";
            }

            if (selectedFragment != null) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(title);
                }
                fm.beginTransaction().hide(active).show(selectedFragment).commit();
                active = selectedFragment;
                return true;
            }
            return false;
        });
    }
}
