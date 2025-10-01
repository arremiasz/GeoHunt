/**
 * Activity Handler for login/signup and the main app
 * @author Alex Remiasz
 */
package com.jubair5.geohunt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jubair5.geohunt.auth.AuthenticationActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_SESSION_TOKEN = "sessionToken";
    private static final int AUTH_REQUEST_CODE = 1001; // Request code for starting AuthenticationActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        clearSession();

        if (checkActiveSession()) {
            Log.d(TAG, "Active session found. Launching PlaceholderActivity.");
            launchPlaceholderActivity();
        } else {
            Log.d(TAG, "No active session. Launching AuthenticationActivity for result.");
            launchAuthenticationActivityForResult();
        }
    }

    private boolean checkActiveSession() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(KEY_SESSION_TOKEN, null);
        Log.d(TAG, "checkActiveSession - Token: " + token);
        return token != null && !token.isEmpty();
    }

    private void launchAuthenticationActivityForResult() {
        Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
        startActivityForResult(intent, AUTH_REQUEST_CODE);
    }

    private void launchPlaceholderActivity() {
        Intent intent = new Intent(MainActivity.this, Placeholder.class); // TODO: Replace Placeholder.class and change function name
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult - requestCode: " + requestCode + ", resultCode: " + resultCode);

        if (requestCode == AUTH_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "AuthenticationActivity finished with RESULT_OK.");
                if (checkActiveSession()) {
                    Log.d(TAG, "Session confirmed active after auth. Launching PlaceholderActivity.");
                    launchPlaceholderActivity();
                } else {
                    // Authentication reported success but no token found.
                    Log.e(TAG, "Auth reported RESULT_OK, but no active session found. Finishing app.");
                    Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_LONG).show();
                    finish();
                }
            } else {
                // Authentication was cancelled or failed
                Log.d(TAG, "AuthenticationActivity finished without RESULT_OK (resultCode: " + resultCode + "). Finishing app.");
                Toast.makeText(this, "Authentication process was cancelled.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void clearSession() {
        // Clear the session token from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_SESSION_TOKEN);
        editor.apply();
    }
}