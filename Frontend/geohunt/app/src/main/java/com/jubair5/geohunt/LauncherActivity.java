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

/**
 * Activity Handler for login/signup and the main app
 * @author Alex Remiasz
 */
public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = "LauncherActivity";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_LOGGED_IN = "isUserLoggedIn";
    private static final String KEY_LOGIN_TIMESTAMP = "loginTimestamp";
    private static final long THIRTY_DAYS_IN_MILLIS = 30L * 24 * 60 * 60 * 1000;

    private static final int AUTH_REQUEST_CODE = 1001; // Request code for starting AuthenticationActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

//        clearSessionOnLaunch(); // TODO: Remove this line in production

        if (checkActiveSession()) {
            Log.d(TAG, "Active session found and refreshed. Launching MainActivity.");
            launchMainActivity();
        } else {
            Log.d(TAG, "No active session or session expired. Launching AuthenticationActivity for result.");
            launchAuthenticationActivityForResult();
        }
    }

    /**
     * Checks if there's an active user session.
     * A session is active if the user is marked as logged in and the login timestamp is within the 30-day validity period.
     * If the session is active, the login timestamp is refreshed to the current time.
     *
     * @return {@code true} if an active session exists and is refreshed, {@code false} otherwise.
     */
    private boolean checkActiveSession() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_USER_LOGGED_IN, false);

        if (!isLoggedIn) {
            Log.d(TAG, "User not logged in.");
            return false;
        }

        long loginTime = sharedPreferences.getLong(KEY_LOGIN_TIMESTAMP, 0);
        if (loginTime == 0) { // Should not happen if isLoggedIn is true
            Log.e(TAG, "User logged in but no login timestamp found. Clearing session.");
            clearLoginSession(sharedPreferences);
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if ((currentTime - loginTime) > THIRTY_DAYS_IN_MILLIS) {
            Log.d(TAG, "Session expired. Last login was more than 30 days ago.");
            clearLoginSession(sharedPreferences);
            return false;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_LOGIN_TIMESTAMP, currentTime);
        editor.apply();
        Log.d(TAG, "Active session confirmed and timestamp refreshed. Last login: " + new java.util.Date(currentTime));
        return true;
    }

    /**
     * Clears the user's login status and timestamp from SharedPreferences.
     *
     * @param sharedPreferences The SharedPreferences instance from which to remove session data.
     */
    private void clearLoginSession(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_LOGGED_IN);
        editor.remove(KEY_LOGIN_TIMESTAMP);
        editor.apply();
        Log.d(TAG, "Login session data cleared from SharedPreferences.");
    }

    /**
     * Launches the {@link AuthenticationActivity} to allow the user to log in or sign up.
     * The activity is started for a result.
     */
    private void launchAuthenticationActivityForResult() {
        Intent intent = new Intent(LauncherActivity.this, AuthenticationActivity.class);
        startActivityForResult(intent, AUTH_REQUEST_CODE);
    }

    /**
     * Launches the main content of the app.
     * It clears the activity stack and finishes the current LauncherActivity to prevent
     * the user from navigating back to it after successful authentication.
     */
    private void launchMainActivity() {
        Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
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
                    Log.d(TAG, "Session confirmed active after auth. Launching MainActivity.");
                    launchMainActivity();
                } else {
                    Log.e(TAG, "Auth reported RESULT_OK, but no active session found. Finishing app.");
                    Toast.makeText(this, "Login failed or session issue. Please try again.", Toast.LENGTH_LONG).show();
                    finish();
                }
            } else {
                Log.d(TAG, "AuthenticationActivity finished without RESULT_OK (resultCode: " + resultCode + "). Finishing app.");
                Toast.makeText(this, "Authentication process was cancelled or failed.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * Clears the login session for testing purposes.
     * This method should be removed or disabled in production.
     */
    private void clearSessionOnLaunch() { // TODO: Remove this method in production
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        clearLoginSession(sharedPreferences);
        Log.d(TAG, "Session cleared on launch for testing purposes.");
    }
}
