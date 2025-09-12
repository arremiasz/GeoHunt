package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AgeActivity extends AppCompatActivity {

    private TextView nameTxt; // define name textview variable
    private Button submitBtn; // define button variable to submit name
    private Button continBtn; // define button variable to continue
    private Button backBtn;   // define back button variable
    private EditText input;  // define text to input name

    int age = 0;
    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age);

        /* initialize UI elements */
        nameTxt = findViewById(R.id.age);
        submitBtn = findViewById(R.id.submit_age_btn);
        backBtn = findViewById(R.id.menu_btn);
        input = findViewById(R.id.inputField);

        Bundle extras = getIntent().getExtras();
        String name = extras.getString("NAME");  // this will come from LoginActivity

        /* when increase btn is pressed, counter++, reset number textview */
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                age = Integer.parseInt(input.getText().toString());
                nameTxt.setText("Age is: " + age);
            }
        });

        /* when back btn is pressed, switch back to MainActivity */
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AgeActivity.this, MainActivity.class);
                intent.putExtra("NAME", name);
                intent.putExtra("AGE", age);  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });

    }
}