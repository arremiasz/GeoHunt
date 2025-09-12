package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView usernameText;  // define username edittext variable
    private TextView passwordText;  // define password edittext variable
    private Button logoutButton;         // define login button variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);            // link to profile activity XML

        /* initialize UI elements */
        usernameText = findViewById(R.id.profile_username);
        passwordText = findViewById(R.id.profile_password);
        logoutButton = findViewById(R.id.profile_logout_btn);    // link to logout button in the Profile activity XML

        // Username and password for a made account
        Bundle extras = getIntent().getExtras();
        usernameText.setText("Username : " + extras.getString("USERNAME")); // this will come from login
        passwordText.setText("Password : " + extras.getString("PASSWORD")); // this will come from login



        /* click listener on login button pressed */
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intent);  // go to MainActivity with the key-value data

            }
        });
    }
}