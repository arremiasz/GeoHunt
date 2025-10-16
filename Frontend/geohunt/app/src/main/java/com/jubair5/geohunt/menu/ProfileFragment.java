/**
 * Profile Page Fragment
 * Displays user information, statistics and account settings.
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.menu;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PFP = "userPfp";

    private LinearLayout displayContainer, editContainer;
    private TextView usernameLabel;
    private TextInputLayout editUsernameLayout, editEmailLayout, editNewPasswordLayout, editCurrentPasswordLayout;
    private EditText editUsername, editEmail, editNewPassword, editCurrentPassword;
    private Button editButton, deleteButton, saveChangesButton, cancelButton, logoutButton;

    private SharedPreferences prefs;
    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.profile_fragment, container, false);
        prefs = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        displayContainer = root.findViewById(R.id.display_container);
        editContainer = root.findViewById(R.id.edit_container);
        usernameLabel = root.findViewById(R.id.username_label);
        editUsernameLayout = root.findViewById(R.id.edit_username_layout);
        editUsername = root.findViewById(R.id.edit_username);
        editEmailLayout = root.findViewById(R.id.edit_email_layout);
        editEmail = root.findViewById(R.id.edit_email);
        editNewPasswordLayout = root.findViewById(R.id.edit_new_password_layout);
        editNewPassword = root.findViewById(R.id.edit_new_password);
        editCurrentPasswordLayout = root.findViewById(R.id.edit_current_password_layout);
        editCurrentPassword = root.findViewById(R.id.edit_current_password);
        editButton = root.findViewById(R.id.edit_account_button);
        deleteButton = root.findViewById(R.id.delete_account_button);
        saveChangesButton = root.findViewById(R.id.save_changes_button);
        cancelButton = root.findViewById(R.id.cancel_button);
        logoutButton = root.findViewById(R.id.logout_button);

        String username = prefs.getString(KEY_USER_NAME, "User");
        usernameLabel.setText("@" + username);

        editButton.setOnClickListener(v -> showEditOptions());
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
        saveChangesButton.setOnClickListener(v -> updatePreface());
        cancelButton.setOnClickListener(v -> showDisplayOptions());
        logoutButton.setOnClickListener(v -> logout());

        editCurrentPassword.setOnKeyListener((v, keyCode, event) -> {
            editCurrentPasswordLayout.setError(null);
            return false;
        });

        return root;
    }

    /**
     * Switches the UI to edit mode
     */
    private void showEditOptions() {
        displayContainer.setVisibility(View.GONE);
        editContainer.setVisibility(View.VISIBLE);

        editUsername.setText(prefs.getString(KEY_USER_NAME, ""));
        editEmail.setText(prefs.getString(KEY_USER_EMAIL, ""));

        editNewPassword.setText("");
        editCurrentPassword.setText("");
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
        editUsernameLayout.setError(null);
        editEmailLayout.setError(null);
        editNewPasswordLayout.setError(null);
        editCurrentPasswordLayout.setError(null);

        String newUsername = editUsername.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();
        String newPassword = editNewPassword.getText().toString().trim();
        String currentPassword = editCurrentPassword.getText().toString().trim();

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

        StringRequest updateRequest = getUpdateRequest(newUsername, newEmail, requestBody, userId);

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(updateRequest);
    }

    /**
     * Creates a StringRequest for updating the user account.
     * @param newUsername The new username to set (can be empty to keep unchanged).
     * @param newEmail The new email to set (can be empty to keep unchanged).
     * @param requestBody The JSON body containing update details.
     * @return The configured StringRequest.
     */
    private StringRequest getUpdateRequest(String newUsername, String newEmail, JSONObject requestBody, int userId) {
        String url = ApiConstants.BASE_URL + ApiConstants.UPDATE_ACCOUNT_ENDPOINT + "?id=" + userId;
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
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Update Profile Error code: " + error.networkResponse.statusCode);
                        String responseBody = "";
                        if(error.networkResponse.data != null) {
                            responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        }
                        Log.e(TAG, "Signup error response body: " + responseBody);

                        if (error.networkResponse.statusCode == 409) {
                            Toast.makeText(getContext(), responseBody, Toast.LENGTH_LONG).show();
                            editUsernameLayout.setError("This email or username might already be taken.");
                            editEmailLayout.setError("This email or username might already be taken.");
//                            emailEditText.requestFocus()
                        } else {
                            Toast.makeText(getContext(), "Signup failed. Server error: " + error.networkResponse.statusCode, Toast.LENGTH_LONG).show();
                        }
                    }
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
                        deleteAccount(userId);
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
    private void deleteAccount(int userId) {
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
