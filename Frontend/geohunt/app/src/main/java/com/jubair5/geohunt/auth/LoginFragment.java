/**
 * Login page
 * @author Nathan Imig
 */
package com.jubair5.geohunt.auth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.jubair5.geohunt.R;
import com.jubair5.geohunt.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    //private static final String BASE_URL = "http://coms-3090-030.class.las.iastate.edu:3306";
    private static final String BASE_URL = "https://137a4fb6-e022-436d-adbe-33d46869fef9.mock.pstmn.io";
    private static final String LOGIN_ENDPOINT = "/login";
    private static final String LOGIN_URL = BASE_URL + LOGIN_ENDPOINT;

    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_USER_LOGGED_IN = "isUserLoggedIn";
    private static final String KEY_LOGIN_TIMESTAMP = "loginTimestamp";


    private TextInputLayout emailLoginLayout;
    private TextInputLayout passwordLoginLayout;
    private EditText emailEditText;
    private EditText passwordEditText;
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

        emailLoginLayout = view.findViewById(R.id.emailLoginLayout);
        passwordLoginLayout = view.findViewById(R.id.passwordLoginLayout);

        emailEditText = view.findViewById(R.id.emailLogin);
        passwordEditText = view.findViewById(R.id.passwordLogin);

        loginButton = view.findViewById(R.id.login);
        goToSignUpTextView = view.findViewById(R.id.goToSignup);

        goToSignUpTextView.setOnClickListener(v -> {
            if (getActivity() != null) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(); // Login page will be default and this will send it back to it
            }
        });

        loginButton.setOnClickListener(v -> {
            performLogin();
        });
    }

    /**
     * Validates the login form inputs and initiates the signup network request if all inputs are valid.
     * Handles UI updates for errors and successful signup, including saving session data.
     */
    private void performLogin() {
        emailLoginLayout.setError(null);
        passwordLoginLayout.setError(null);

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!validateEmail(email) || !validatePassword(password)) {
            return;
        }

        Log.d(TAG, "All validations passed. Proceeding with login.");

        final JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON request body", e);
            Toast.makeText(getContext(), "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login successful: " + response);
                        Toast.makeText(getContext(), "Account found successfully!", Toast.LENGTH_LONG).show();

                        if (getContext() != null) {
                            SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(KEY_USER_LOGGED_IN, true);
                            editor.putLong(KEY_LOGIN_TIMESTAMP, System.currentTimeMillis());
                            editor.apply();
                            Log.d(TAG, "Login status and timestamp saved.");
                        }

                        if (getActivity() != null) {
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish(); // Finish AuthenticationActivity
                        }
                    }
                },
                new Response.ErrorListener() { // TODO: More specific error codes
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Login error: " + error.toString());
                        if (error.networkResponse != null) {
                            Log.e(TAG, "Login error status code: " + error.networkResponse.statusCode);
                            String responseBody = "";
                            if(error.networkResponse.data != null) {
                                responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            }
                            Log.e(TAG, "Login error response body: " + responseBody);

                            if (error.networkResponse.statusCode == 400) {
                                Toast.makeText(getContext(), "Account already exists or invalid input.", Toast.LENGTH_LONG).show();
                                emailLoginLayout.setError("This email or username might already be taken.");
                                emailEditText.requestFocus();
                            } else {
                                Toast.makeText(getContext(), "Login failed. Server error: " + error.networkResponse.statusCode, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Login failed. Check network connection.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ) {
            @Override
            public byte[] getBody() throws AuthFailureError {
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
     * Validates the provided email address.
     * A valid email must not be empty and must match the standard email pattern.
     *
     * @param email The email string to validate.
     * @return {@code true} if the email is valid, {@code false} otherwise.
     */
    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            emailLoginLayout.setError("Email cannot be empty");
            emailEditText.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLoginLayout.setError("Enter a valid email address");
            emailEditText.requestFocus();
            return false;
        }
        emailLoginLayout.setError(null);
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
