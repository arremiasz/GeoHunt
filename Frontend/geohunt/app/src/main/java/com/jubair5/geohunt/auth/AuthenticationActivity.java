/**
 * Activity for handling login/signup fragments
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jubair5.geohunt.R;

public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Sign Up");
        }

        if (savedInstanceState == null) {
            // TODO: Replace SignupFragment with LoginFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view, new SignupFragment())
                    .commit();
        }
    }
}
