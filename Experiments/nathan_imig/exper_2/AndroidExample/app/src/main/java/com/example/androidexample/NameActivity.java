package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NameActivity extends AppCompatActivity {

    private TextView nameTxt; // define name textview variable
    private Button submitBtn; // define button variable to submit name
    private Button continBtn; // define button variable to continue
    private Button backBtn;   // define back button variable
    private EditText input;  // define text to input name

    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        /* initialize UI elements */
        nameTxt = findViewById(R.id.name);
        submitBtn = findViewById(R.id.submit_name_btn);
        continBtn = findViewById(R.id.continue_btn);
        backBtn = findViewById(R.id.back_btn);
        input = findViewById(R.id.inputField);

        /* when increase btn is pressed, counter++, reset number textview */
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = input.getText().toString();
                nameTxt.setText("Name is: " + name);
            }
        });

        /* when decrease btn is pressed, counter--, reset number textview */
        continBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameTxt.setText("Name is: " + input.getText());
            }
        });

        /* when back btn is pressed, switch back to MainActivity */
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NameActivity.this, MainActivity.class);
                intent.putExtra("NAME", name);  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });

    }
}