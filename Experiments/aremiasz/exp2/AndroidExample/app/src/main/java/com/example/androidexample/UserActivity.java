package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class UserActivity extends AppCompatActivity {

    private EditText name;
    private EditText age;
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        name = findViewById(R.id.user_name_edt);
        age = findViewById(R.id.user_age_edt);
        submitBtn = findViewById(R.id.user_submit_btn);

        submitBtn.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, MainActivity.class);
            String ageText = age.getText().toString();
            int ageValue = ageText.isEmpty() ? 0 : Integer.parseInt(ageText);
            User user = new User(name.getText().toString(), ageValue);
            intent.putExtra("USER", user);
            startActivity(intent);
        });

    }
}
