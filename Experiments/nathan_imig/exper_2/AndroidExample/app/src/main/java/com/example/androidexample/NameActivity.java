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
    private TextView ageTxt; // define name textview variable
    private Button submitBtn; // define button variable to submit name
    private Button continBtn; // define button variable to continue
    private Button backBtn;   // define back button variable
    private EditText inputName;  // define text to input name
    private EditText inputAge;  // define text to input age

    String name = "";

    int age = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        /* initialize UI elements */
        nameTxt = findViewById(R.id.name);
        ageTxt = findViewById(R.id.age);
        submitBtn = findViewById(R.id.submit_name_btn);
        continBtn = findViewById(R.id.continue_btn);
        inputName = findViewById(R.id.inputNameField);
        inputAge = findViewById(R.id.inputAgeField);

        /* when increase btn is pressed, counter++, reset number textview */
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = inputName.getText().toString();
                nameTxt.setText("Name is: " + name);
                age = Integer.parseInt(inputAge.getText().toString());
                ageTxt.setText("Age is: " + age);
            }
        });

        /* when decrease btn is pressed, counter--, reset number textview */
        continBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NameActivity.this, MainActivity.class);
                intent.putExtra("NAME", name);  // key-value to pass to the MainActivity
                intent.putExtra("AGE", age);
                startActivity(intent);
            }
        });

    }
}