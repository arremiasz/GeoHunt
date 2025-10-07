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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.jubair5.geohunt.LauncherActivity;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_ID = "userId";

    private TextView usernameLabel;
    private Button editButton;
    private Button deleteButton;
    private SharedPreferences prefs;
    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.profile_fragment, container, false);

        prefs = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        usernameLabel = root.findViewById(R.id.username_label);
        usernameLabel.setText("@" + prefs.getString(KEY_USER_NAME, "User"));

        editButton= root.findViewById(R.id.edit_account_button);
        editButton.setOnClickListener(v -> showEditOptions());

        deleteButton = root.findViewById(R.id.delete_account_button);
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());

        return root;
    }

    private void showEditOptions() {
        int userId = prefs.getInt(KEY_USER_ID, -1);
        if (userId != -1) {
            String url = ApiConstants.BASE_URL + ApiConstants.UPDATE_ACCOUNT_ENDPOINT + "?id=" + userId;
            // TODO: Implement account editing functionality
        } else {
            Toast.makeText(getContext(), "Error: Could not find user ID.", Toast.LENGTH_SHORT).show();
        }
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
}
