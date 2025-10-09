package com.jubair5.geohunt.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.jubair5.geohunt.LauncherActivity;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class SingleLocationFragment extends Fragment {

    private static final String TAG = "SingleLocationFragment";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PFP = "userPfp";

    private LinearLayout displayContainer, editContainer;
    private TextView nameLabel, latitudeLabel, longitudeLabel, radiusLabel, creatorLabel;
    private TextInputLayout editNameLayout, editLatitudeLayout, editLongitudeLayout, editRadiusLayout;
    private EditText editName, editLatitude, editLongitude, editRadius;
    private Button editButton, deleteButton, saveChangesButton, cancelButton;

    private SharedPreferences prefs;
    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.single_location_fragment, container, false);
        prefs = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        displayContainer = root.findViewById(R.id.display_container);
        editContainer = root.findViewById(R.id.edit_container);

        nameLabel = root.findViewById(R.id.locationName_label);
        latitudeLabel = root.findViewById(R.id.latitude_label);
        longitudeLabel = root.findViewById(R.id.longitude_label);
        radiusLabel = root.findViewById(R.id.radius_label);
        creatorLabel = root.findViewById(R.id.creator_label);


        editNameLayout = root.findViewById(R.id.edit_username_layout);
        editName = root.findViewById(R.id.edit_username);
        editLatitudeLayout = root.findViewById(R.id.edit_email_layout);
        editLatitude = root.findViewById(R.id.edit_email);
        editLongitudeLayout = root.findViewById(R.id.edit_new_password_layout);
        editLongitude = root.findViewById(R.id.edit_new_password);
        editRadius = root.findViewById(R.id.edit_current_password_layout);
        editRadiusLayout = root.findViewById(R.id.edit_current_password);

        editButton = root.findViewById(R.id.edit_account_button);
        deleteButton = root.findViewById(R.id.delete_account_button);
        saveChangesButton = root.findViewById(R.id.save_changes_button);
        cancelButton = root.findViewById(R.id.cancel_button);

        getLables();

        editButton.setOnClickListener(v -> showEditOptions());
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
        saveChangesButton.setOnClickListener(v -> updatePreface());
        cancelButton.setOnClickListener(v -> showDisplayOptions());

        return root;
    }

    private void getLables() {
        String locationsURL = ApiConstants.BASE_URL + ApiConstants.LOCATIONS_ENDPOINT;
        StringRequest locationRequest = new StringRequest(Request.Method.GET, locationsURL,
                locationResponse -> {
                    try {
                        JSONObject userJson = new JSONObject(locationResponse);
                        String name = userJson.getString("name");
                        String latitude = userJson.getString("latitude");
                        String longitude = userJson.getString("longitude");
                        String radius = userJson.getString("radius");
                        String creator = userJson.getString("creator");
                        nameLabel.setText(name);
                        latitudeLabel.setText(latitude);
                        longitudeLabel.setText(longitude);
                        radiusLabel.setText(radius);
                        creatorLabel.setText(creator);;

                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing locations details from GET request", e);
                        Toast.makeText(getContext(), "Getting successful, but failed to parse user details.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching location details", error);
                });
    }

    /**
     * Switches the UI to edit mode
     */
    private void showEditOptions() {
        displayContainer.setVisibility(View.GONE);
        editContainer.setVisibility(View.VISIBLE);

        editName.setText("");
        editLongitude.setText("");
        editLatitude.setText("");
        editRadius.setText("");
    }

    /**
     * Switches the UI to display mode
     */
    private void showDisplayOptions() {
        displayContainer.setVisibility(View.VISIBLE);
        editContainer.setVisibility(View.GONE);
    }

    /**
     * Initiates the update process by validating inputs and current password.
     */
    private void updatePreface() {
        editNameLayout.setError(null);
        editLatitudeLayout.setError(null);
        editLongitudeLayout.setError(null);
        editRadiusLayout.setError(null);

        String newName = editName.getText().toString().trim();
        String newLatitude = editLatitude.getText().toString().trim();
        String newLongitude = editLongitude.getText().toString().trim();
        String newRadius = editRadius.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword)) {
            editCurrentPasswordLayout.setError("Enter your current password to save changes");
            editCurrentPassword.requestFocus();
            return;
        }

        validatePasswordAndContinue(newUsername, newEmail, newPassword, currentPassword);
    }

    /**
     * Validates the current password by attempting to log in.
     * If successful, proceeds with the update.
     * @param newUsername The new username to set (can be empty to keep unchanged).
     * @param newEmail The new email to set (can be empty to keep unchanged).
     * @param newPassword The new password to set (can be empty to keep unchanged).
     * @param currentPassword The current password for validation.
     */
    private void validatePasswordAndContinue(String newUsername, String newEmail, String newPassword, String currentPassword) {
        String currentUsername = prefs.getString(KEY_USER_NAME, "");

        JSONObject loginBody = new JSONObject();
        try {
            loginBody.put("username", currentUsername);
            loginBody.put("password", currentPassword);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating login JSON for validation", e);
            return;
        }

        StringRequest loginRequest = new StringRequest(Request.Method.POST, ApiConstants.BASE_URL + ApiConstants.LOGIN_ENDPOINT,
                response -> {
                    Log.d(TAG, "Password validation successful. Proceeding with update.");
                    performUpdate(newUsername, newEmail, newPassword);
                },
                error -> {
                    Log.e(TAG, "Password validation failed", error);
                    editCurrentPasswordLayout.setError("Incorrect password");
                    editCurrentPassword.requestFocus();
                }
        ) {
            @Override
            public byte[] getBody() {
                return loginBody.toString().getBytes(StandardCharsets.UTF_8);
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(loginRequest);
    }

    /**
     * Sends the update request to the server with the new account details.
     * @param newUsername The new username to set (can be empty to keep unchanged).
     * @param newEmail The new email to set (can be empty to keep unchanged).
     * @param newPassword The new password to set (can be empty to keep unchanged).
     */
    private void performUpdate(String newUsername, String newEmail, String newPassword) {
        int userId = prefs.getInt(KEY_USER_ID, -1);
        if (userId == -1) {
            Toast.makeText(getContext(), "An Error Occurred", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "User ID not found in shared preferences.");
            return;
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", userId);
            if (newUsername.equals(prefs.getString(KEY_USER_NAME, ""))) {
                newUsername = "";
            }
            if (newEmail.equals(prefs.getString(KEY_USER_EMAIL, ""))) {
                newEmail = "";
            }
            if (newPassword.isEmpty()) {
                newPassword = "";
            }

            requestBody.put("username", newUsername);
            requestBody.put("email", newEmail);
            requestBody.put("pfp", ""); // TODO: Implement profile picture update
            requestBody.put("password", newPassword);


        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON for update", e);
            return;
        }

        StringRequest updateRequest = getUpdateRequest(newUsername, newEmail, requestBody);

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(updateRequest);
    }

    /**
     * Creates a StringRequest for updating the user account.
     * @param newUsername The new username to set (can be empty to keep unchanged).
     * @param newEmail The new email to set (can be empty to keep unchanged).
     * @param requestBody The JSON body containing update details.
     * @return The configured StringRequest.
     */
    private StringRequest getUpdateRequest(String newUsername, String newEmail, JSONObject requestBody) {
        String url = ApiConstants.BASE_URL + ApiConstants.UPDATE_ACCOUNT_ENDPOINT;
        StringRequest updateRequest = new StringRequest(Request.Method.PUT, url,
                response -> {
                    Log.d(TAG, "Account updated successfully");
                    Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = prefs.edit();
                    if (!newUsername.isBlank()) {
                        editor.putString(KEY_USER_NAME, newUsername);
                        usernameLabel.setText("@" + newUsername);
                    }
                    if (!newEmail.isBlank()) {
                        editor.putString(KEY_USER_EMAIL, newEmail);
                    }
                    editor.apply();

                    showDisplayOptions();
                },
                error -> {
                    Log.e(TAG, "Error updating account", error);
                    Toast.makeText(getContext(), "Failed to update profile.", Toast.LENGTH_LONG).show();
                }) {
            @Override
            public byte[] getBody() {
                return requestBody.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        return updateRequest;
    }

    /**
     * Displays a confirmation dialog before deleting the account.
     */
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Account Deletion Confirmation")
                .setMessage("Are you sure you want to PERMANENTLY delete your account? This action cannot be undone.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Yes", (dialog, which) -> {
                    int userId = prefs.getInt(KEY_USER_ID, -1);
                    if (userId != -1) {
                        deleteLocation(userId);
                    } else {
                        Toast.makeText(getContext(), "Error: Could not find user ID.", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    /**
     * Sends a DELETE request to the server to delete the user account.
     * On success, clears SharedPreferences and navigates to LauncherActivity.
     * @param userId The ID of the user to delete.
     */
    private void deleteLocation(int userId) {
        String url = ApiConstants.BASE_URL + ApiConstants.DELETE_ACCOUNT_ENDPOINT + "?id=" + userId;
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Log.d(TAG, "Account deleted successfully: " + response);
                    Toast.makeText(getContext(), "Account deleted successfully.", Toast.LENGTH_SHORT).show();

                    prefs.edit().clear().apply();

                    if (getActivity() != null) {
                        Intent intent = new Intent(getActivity(), LauncherActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }
                },
                error -> {
                    Log.e(TAG, "Failed to delete account", error);
                    Toast.makeText(getContext(), "Failed to delete account. Please try again.", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(deleteRequest);
    }

    /**
     * Logs out the user by clearing SharedPreferences and navigating to LauncherActivity.
     */
    private void logout() {
        prefs.edit().clear().apply();

        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), LauncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Log.d(TAG, "Logging out of " + prefs.getString(KEY_USER_NAME, "account"));
            Toast.makeText(getContext(), "Logging out", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            getActivity().finish();
        }
    }
}