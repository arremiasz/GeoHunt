package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameEditText;  // define username edittext variable
    private EditText passwordEditText;  // define password edittext variable
    private EditText confirmEditText;   // define confirm edittext variable
    private Button loginButton;         // define login button variable
    private Button signupButton;        // define signup button variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        /* initialize UI elements */
        usernameEditText = findViewById(R.id.signup_username_edt);  // link to username edtext in the Signup activity XML
        passwordEditText = findViewById(R.id.signup_password_edt);  // link to password edtext in the Signup activity XML
        confirmEditText = findViewById(R.id.signup_confirm_edt);    // link to confirm edtext in the Signup activity XML
        signupButton = findViewById(R.id.signup_submit_btn);  // link to signup button in the Login activity XML
        loginButton = findViewById(R.id.signup_login_btn);    // link to login button in the Signup activity XML


        /* click listener on login button pressed */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when login button is pressed, use intent to switch to Login Activity */
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);  // go to LoginActivity
            }
        });

        /* click listener on signup button pressed */
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* grab strings from user inputs */
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirm = confirmEditText.getText().toString();

                // If password does not match
                if (!(password.equals(confirm))){
                    Toast.makeText(getApplicationContext(), "Password don't match", Toast.LENGTH_LONG).show();
                }
                // If password does not have a number
                else if (!(password.contains("1") ||
                        password.contains("2") ||
                        password.contains("3") ||
                        password.contains("4") ||
                        password.contains("5") ||
                        password.contains("6") ||
                        password.contains("7") ||
                        password.contains("8") ||
                        password.contains("9") ||
                        password.contains("0"))) {
                    Toast.makeText(getApplicationContext(), "Password must have a number", Toast.LENGTH_LONG).show();
                }
                // Sends to longin with account information
                else {
                    Toast.makeText(getApplicationContext(), "Signing up", Toast.LENGTH_LONG).show();

                    /* when sign up passes, use intent to switch to Login Activity */
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    intent.putExtra("USERNAME", username);  // key-value to pass to the MainActivity
                    intent.putExtra("PASSWORD", password);  // key-value to pass to the MainActivity
                    startActivity(intent);  // go to MainActivity with the key-value data
                }



            }
        });
    }
}