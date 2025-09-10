package com.example.androidexample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity {

    private EditText xyz;
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        xyz = findViewById(R.id.coordinates);
        submitBtn = findViewById(R.id.map_submit_btn);

        submitBtn.setOnClickListener(v -> {
            String coordinates = xyz.getText().toString();
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + coordinates + "Pinned Location");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            startActivity(mapIntent);
            finish();


            // ames: 42.025972, -93.646417
            // nchs: 41.767643, -88.154288
        });
    }
}
