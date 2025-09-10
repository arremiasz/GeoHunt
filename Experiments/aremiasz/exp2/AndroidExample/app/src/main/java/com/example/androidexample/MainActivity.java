package com.example.androidexample;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/*

1. To run this project, open the directory "Android Example", otherwise it may not recognize the file structure properly

2. Ensure you are using a compatible version of gradle, to do so you need to check 2 files.

    AndroidExample/Gradle Scripts/build.gradle
    Here, you will have this block of code. Ensure it is set to a compatible version,
    in this case 8.12.2 should be sufficient:
        plugins {
            id 'com.android.application' version '8.12.2' apply false
        }

    Gradle Scripts/gradle-wrapper.properties

3. This file is what actually determines the Gradle version used, 8.13 should be sufficient.
    "distributionUrl=https\://services.gradle.org/distributions/gradle-8.13-bin.zip" ---Edit the version if needed

4. You might be instructed by the plugin manager to upgrade plugins, accept it and you may execute the default selected options.

5. Press "Sync project with gradle files" located at the top right of Android Studio,
   once this is complete you will be able to run the app

   This version is compatible with both JDK 17 and 21. The Java version you want to use can be
   altered in Android Studio->Settings->Build, Execution, Deployment->Build Tools->Gradle

 */


public class MainActivity extends AppCompatActivity {

    private TextView messageText;     // define message textview variable
    private Button counterButton;     // define counter button variable
    private Button userButton;        // define user button variable
    private Button mapButton;         // define map button variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg_txt);      // link to message textview in the Main activity XML
        counterButton = findViewById(R.id.main_counter_btn);// link to counter button in the Main activity XML
        userButton = findViewById(R.id.main_user_btn);      // link to user button in the Main activity XML
        mapButton = findViewById(R.id.main_map_btn);        // link to map button in the Main activity XML

        /* extract data passed into this activity from another activity */
        Bundle extras = getIntent().getExtras();
        /* check which activity passed the intent */
        if (extras != null) {
            if (extras.containsKey("NUM")) { // if the key "NUM" exists, it came from CounterActivity
                String num = extras.getString("NUM");
                messageText.setText("Counter Value: " + num); // set message text to the counter value
            } else if (extras.containsKey("USER")) { // if the key "USER" exists, it came from UserActivity
                User user = (User) extras.getSerializable("USER");
                messageText.setText("Name & Age: " + user.getName() + ", Age: " + user.getAge()); // set message text to the user info
            }
        } else {
            messageText.setText("Intent Experiment");
        }

        /* click listener on counter button pressed */
        counterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* when counter button is pressed, use intent to switch to Counter Activity */
                Intent intent = new Intent(MainActivity.this, CounterActivity.class);
                startActivity(intent);
            }
        });

        /* click listener on user button pressed */
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
        /* click listener on map button pressed */
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
        /* click listener on title text pressed */
        messageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Hello!");
                builder.setMessage("There is nothing to see here");
                builder.setPositiveButton("OK!", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}