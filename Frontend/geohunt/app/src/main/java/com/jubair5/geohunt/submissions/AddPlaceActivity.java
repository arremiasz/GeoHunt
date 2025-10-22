/**
 * Activity for adding places
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.submissions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jubair5.geohunt.R;

public class AddPlaceActivity extends AppCompatActivity {

    private static final String TAG = "AddPlaceActivity";

    private ImageView capturedImage;


    private final ActivityResultLauncher<Void> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(),
            bitmap -> {
                if (bitmap != null) {
                    Log.d(TAG, "Image capture successful.");
                    capturedImage.setImageBitmap(bitmap);
                } else {
                    Log.w(TAG, "Image capture cancelled or failed.");
                    Toast.makeText(this, "Cancelled.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    Log.d(TAG, "Camera permission granted.");
                    takePictureLauncher.launch(null);
                } else {
                    Log.w(TAG, "Camera permission denied.");
                    Toast.makeText(this, "Camera permission is required to add a place.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        capturedImage = findViewById(R.id.captured_image);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            takePictureLauncher.launch(null);
        }
    }
}
