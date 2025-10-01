/**
 * Login page
 * @author Nathan Imig
 */
package com.jubair5.geohunt;

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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputLayout;
//import com.jubair5.geohunt.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class LogninFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final Pattern PASSWORD_HAS_DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern PASSWORD_HAS_SPECIAL_CHAR = Pattern.compile(".*[^a-zA-Z0-9].*");

    //private static final String BASE_URL = "http://coms-3090-030.class.las.iastate.edu:3306";
    private static final String BASE_URL = "https://137a4fb6-e022-436d-adbe-33d46869fef9.mock.pstmn.io";
    private static final String LOGIN_ENDPOINT = "/login";
    private static final String LOGIN_URL = BASE_URL + LOGIN_ENDPOINT;

    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private static final String KEY_SESSION_TOKEN = "sessionToken";

    private TextInputLayout usernameLoginLayout;
    private TextInputLayout emailSignupLayout;
    private TextInputLayout passwordSignupLayout;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signupButton;
    private TextView goToLoginTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usernameLoginLayout = view.findViewById(R.id.usernameSignupLayout);
        emailSignupLayout = view.findViewById(R.id.emailSignupLayout);
        passwordSignupLayout = view.findViewById(R.id.passwordSignupLayout);

        usernameEditText = view.findViewById(R.id.usernameSignup);
        emailEditText = view.findViewById(R.id.emailSignup);
        passwordEditText = view.findViewById(R.id.passwordSignup);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordSignup);

        signupButton = view.findViewById(R.id.signup);
        goToLoginTextView = view.findViewById(R.id.goToLogin);

        goToLoginTextView.setOnClickListener(v -> {
            if (getActivity() != null) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(); // login page will be default and this will send it back to it
            }
        });

        signupButton.setOnClickListener(v -> {
            performSignup();
        });
    }

    private void performSignup() {
        usernameLoginLayout.setError(null);
        emailSignupLayout.setError(null);
        passwordSignupLayout.setError(null);

        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (!validateUsername(username) || !validateEmail(email) || !validatePassword(password) || !validateConfirmPassword(password, confirmPassword)) {
            return;
        }

        Log.d(TAG, "All validations passed. Proceeding with signup.");

        final JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", username);
            requestBody.put("pfp", JSONObject.NULL);
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON request body", e);
            Toast.makeText(getContext(), "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, LOGIN_URL, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Login successful: " + response.toString());
                        try {
                            String token = response.getString("token");
                            saveSessionToken(token);
                            Toast.makeText(getContext(), "Account created successfully!", Toast.LENGTH_LONG).show();

                            if (getActivity() != null) {
                                getActivity().setResult(Activity.RESULT_OK);
                                getActivity().finish(); // Finish AuthenticationActivity
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing token from response", e);
                            Toast.makeText(getContext(), "Login successful, but failed to process session.", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() { // TODO: More specific error codes
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Signup error: " + error.toString());
                        if (error.networkResponse != null) {
                            Log.e(TAG, "Signup error status code: " + error.networkResponse.statusCode);
                            String responseBody = "";
                            if(error.networkResponse.data != null) {
                                responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            }
                            Log.e(TAG, "Signup error response body: " + responseBody);

                            if (error.networkResponse.statusCode == 400) {
                                Toast.makeText(getContext(), "Account already exists or invalid input.", Toast.LENGTH_LONG).show();
                                emailSignupLayout.setError("This email or username might already be taken.");
                                emailEditText.requestFocus();
                            } else {
                                Toast.makeText(getContext(), "Signup failed. Server error: " + error.networkResponse.statusCode, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Signup failed. Check network connection.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

        if (getContext() != null) {
            VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
        }
    }

    private void saveSessionToken(String token) {
        if (getContext() == null) return;
        Log.d(TAG, "Saving session token: " + token);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SESSION_TOKEN, token);
        editor.apply();
    }

    private boolean validateUsername(String username) {
        if (TextUtils.isEmpty(username)) {
            usernameLoginLayout.setError("Username cannot be empty");
            usernameEditText.requestFocus();
            return false;
        }
        usernameLoginLayout.setError(null);
        return true;
    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            emailSignupLayout.setError("Email cannot be empty");
            emailEditText.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailSignupLayout.setError("Enter a valid email address");
            emailEditText.requestFocus();
            return false;
        }
        emailSignupLayout.setError(null);
        return true;
    }

    private boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            passwordSignupLayout.setError("Password cannot be empty");
            passwordEditText.requestFocus();
            return false;
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            passwordSignupLayout.setError("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
            passwordEditText.requestFocus();
            return false;
        }
        if (!PASSWORD_HAS_DIGIT.matcher(password).matches()) {
            passwordSignupLayout.setError("Password must contain at least one digit");
            passwordEditText.requestFocus();
            return false;
        }
        if (!PASSWORD_HAS_SPECIAL_CHAR.matcher(password).matches()) {
            passwordSignupLayout.setError("Password must contain at least one special character");
            passwordEditText.requestFocus();
            return false;
        }
        passwordSignupLayout.setError(null);
        return true;
    }
}
