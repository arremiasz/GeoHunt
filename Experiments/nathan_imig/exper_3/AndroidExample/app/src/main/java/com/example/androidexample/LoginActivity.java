package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;  // define username edittext variable
    private EditText passwordEditText;  // define password edittext variable
    private Button loginButton;         // define login button variable
    private Button signupButton;        // define signup button variable
    private String username;            // Username linked with an account
    private String password;            // Password linked with an account

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);            // link to Login activity XML

        /* initialize UI elements */
        usernameEditText = findViewById(R.id.login_username_edt);
        passwordEditText = findViewById(R.id.login_password_edt);
        loginButton = findViewById(R.id.login_submit_btn);    // link to login button in the Login activity XML
        signupButton = findViewById(R.id.login_signup_btn);  // link to signup button in the Login activity XML

        // Username and password for a made account
        Bundle extras = getIntent().getExtras();
        username = extras.getString("USERNAME"); // this will come from Welcome or Signup
        password = extras.getString("PASSWORD"); // this will come from Welcome or Signup



        /* click listener on login button pressed */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* grab strings from user inputs */
                String usernameEntered = usernameEditText.getText().toString();
                String passwordEntered = passwordEditText.getText().toString();

                if (password.equals(passwordEntered) && username.equals(usernameEntered) && !(username.isEmpty())){
                    Toast.makeText(getApplicationContext(), "Logging In", Toast.LENGTH_LONG).show();

                    /* when sign up passes, use intent to switch to Login Activity */
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("USERNAME", username);  // key-value to pass to the MainActivity
                    intent.putExtra("PASSWORD", password);  // key-value to pass to the MainActivity
                    startActivity(intent);  // go to MainActivity with the key-value data
                }
                // Fails and does nothing
                else {
                    Toast.makeText(getApplicationContext(), "No account found", Toast.LENGTH_LONG).show();
                }
            }
        });

        /* click listener on signup button pressed */
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when signup button is pressed, use intent to switch to Signup Activity */
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);  // go to SignupActivity
            }
        });
    }
}