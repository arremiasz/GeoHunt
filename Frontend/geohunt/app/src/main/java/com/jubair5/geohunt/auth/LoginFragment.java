package com.jubair5.geohunt.auth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.ApiConstants;
import com.jubair5.geohunt.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Login page
 * @author Nathan Imig
 */
public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_LOGGED_IN = "isUserLoggedIn";
    private static final String KEY_LOGIN_TIMESTAMP = "loginTimestamp";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PFP = "userPfp";


    private TextInputLayout usernameLoginLayout, passwordLoginLayout;
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView goToSignUpTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Log In");
        }

        // Layout
        usernameLoginLayout = view.findViewById(R.id.usernameLoginLayout);
        passwordLoginLayout = view.findViewById(R.id.passwordLoginLayout);

        // Textfields
        usernameEditText = view.findViewById(R.id.usernameLogin);
        passwordEditText = view.findViewById(R.id.passwordLogin);

        // Buttons
        loginButton = view.findViewById(R.id.login);
        goToSignUpTextView = view.findViewById(R.id.goToSignup);

        goToSignUpTextView.setOnClickListener(v -> {
            gotoSignUp();
        });

        loginButton.setOnClickListener(v -> {
            performLogin();
        });

        usernameEditText.setOnKeyListener((v, keyCode, event) -> {
            usernameLoginLayout.setError(null);
            passwordLoginLayout.setError(null);
            return false;
        });

        passwordEditText.setOnKeyListener((v, keyCode, event) -> {
            usernameLoginLayout.setError(null);
            passwordLoginLayout.setError(null);
            return false;
        });
    }

    /**
     * Navigates back to the signup fragment by popping the back stack.
     */
    private void gotoSignUp() {
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, new SignupFragment())
                    .commit();
        }
    }

    /**
     * Validates the login form inputs and initiates the login network request if all inputs are valid.
     * Handles UI updates for errors and successful login, including saving session data.
     */
    private void performLogin() {
        usernameLoginLayout.setError(null);
        passwordLoginLayout.setError(null);

        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!validateUsername(username) || !validatePassword(password)) {
            return;
        }

        Log.d(TAG, "All validations passed. Proceeding with login.");

        final JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", username);
            requestBody.put("password", password);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON request body", e);
            Toast.makeText(getContext(), "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
            return;
        }

        String loginUrl = ApiConstants.BASE_URL + ApiConstants.LOGIN_ENDPOINT;
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, loginUrl,
                response -> {
                    Log.d(TAG, "Login successful: " + response);
                    int userId = Integer.parseInt(response);

                    String userDetailsUrl = ApiConstants.BASE_URL + ApiConstants.GET_ACCOUNT_BY_ID_ENDPOINT + "?id=" + userId;
                    StringRequest userDetailsRequest = new StringRequest(Request.Method.GET, userDetailsUrl,
                            userDetailsResponse -> {
                                try {
                                    JSONObject userJson = new JSONObject(userDetailsResponse);
                                    String email = userJson.getString("email");
                                    String pfp = userJson.getString("pfp");

                                    if (getContext() != null) {
                                        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean(KEY_USER_LOGGED_IN, true);
                                        editor.putLong(KEY_LOGIN_TIMESTAMP, System.currentTimeMillis());
                                        editor.putInt(KEY_USER_ID, userId);
                                        editor.putString(KEY_USER_NAME, username);
                                        editor.putString(KEY_USER_EMAIL, email);
                                        editor.putString(KEY_USER_PFP, pfp);
                                        editor.apply();
                                        Log.d(TAG, "User data and session timestamp saved.");
                                        Toast.makeText(getContext(), "Login successful!", Toast.LENGTH_LONG).show();
                                    }

                                    if (getActivity() != null) {
                                        getActivity().setResult(Activity.RESULT_OK);
                                        getActivity().finish();
                                    }
                                } catch (JSONException e) {
                                    Log.e(TAG, "Error parsing user details from GET request", e);
                                    Toast.makeText(getContext(), "Login successful, but failed to parse user details.", Toast.LENGTH_LONG).show();
                                }
                            },
                            error -> {
                                Log.e(TAG, "Error fetching user details", error);
                                Toast.makeText(getContext(), "Login successful, but failed to fetch user details.", Toast.LENGTH_LONG).show();
                            });

                    if (getContext() != null) {
                        VolleySingleton.getInstance(getContext()).addToRequestQueue(userDetailsRequest);
                    }
                },
                error -> {
                    Log.e(TAG, "Login error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Login error status code: " + error.networkResponse.statusCode);
                        String responseBody = "";
                        if(error.networkResponse.data != null) {
                            responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        }
                        Log.e(TAG, "Login error response body: " + responseBody);

                        if (error.networkResponse.statusCode == 400) {
                            Toast.makeText(getContext(), "Username and password doesn't match", Toast.LENGTH_LONG).show();
                            usernameLoginLayout.setError("The username or password is incorrect");
                            passwordLoginLayout.setError("The username or password is incorrect");
//                            usernameEditText.requestFocus();
//                            passwordEditText.requestFocus();
                        } else {
                            Toast.makeText(getContext(), "Login failed. Server error: " + error.networkResponse.statusCode, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Login failed. Check network connection.", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                return requestBody.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders() != null ? super.getHeaders() : new HashMap<>();
            }
        };

        if (getContext() != null) {
            VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
        }
    }


    /**
     * Validates the provided username.
     * A valid username must not be empty.
     *
     * @param username The username string to validate.
     * @return {@code true} if the username is valid, {@code false} otherwise.
     */
    private boolean validateUsername(String username) {
        if (TextUtils.isEmpty(username)) {
            usernameLoginLayout.setError("Username cannot be empty");
            usernameEditText.requestFocus();
            return false;
        }
        usernameLoginLayout.setError(null);
        return true;
    }

    /**
     * Validates that the password is not empty
     *
     * @param password The password string to validate.
     * @return {@code true} if the password meets all criteria, {@code false} otherwise.
     */
    private boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            passwordLoginLayout.setError("Password cannot be empty");
            passwordEditText.requestFocus();
            return false;
        }
        passwordLoginLayout.setError(null);
        return true;
    }
}
