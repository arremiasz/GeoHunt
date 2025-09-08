package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.graphics.Color;
import android.animation.ArgbEvaluator;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout textContainer = findViewById(R.id.text_container);

        String message = "THIS IS DEMO 1";

        final TextView[] letters = new TextView[message.length()];

        for (int i = 0; i < message.length(); i++) {
            final TextView letterView = new TextView(this);
            letterView.setText(String.valueOf(message.charAt(i)));
            letterView.setTextSize(55);
            textContainer.addView(letterView);
            letters[i] = letterView;
        }

        // Animate using sine wave
        ValueAnimator waveAnimator = ValueAnimator.ofFloat(0, (float)(2 * Math.PI));
        waveAnimator.setDuration(2000); // wave speed
        waveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        waveAnimator.setInterpolator(new LinearInterpolator());

        waveAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            float amplitude = 50f;
            float wavelength = (float) (Math.PI / 4);

            for (int i = 0; i < letters.length; i++) {
                float y = (float) (amplitude * Math.sin(value + i * wavelength));
                letters[i].setTranslationY(y);
            }
        });

        waveAnimator.start();

        RelativeLayout rootLayout = findViewById(R.id.root_layout);

        ValueAnimator colorAnimator = ValueAnimator.ofFloat(0, 360);
        colorAnimator.setRepeatMode(ValueAnimator.RESTART);
        colorAnimator.setDuration(20000); // speed of color change
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimator.setInterpolator(new LinearInterpolator());

        // contrast text color based on background color
        colorAnimator.addUpdateListener(anim -> {
            float hue = (float) anim.getAnimatedValue();
            int color = Color.HSVToColor(new float[]{hue, 1f, 1f});
            rootLayout.setBackgroundColor(color);

            // calculate luminance for contrast
            double luminance = (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
            int targetColor = (luminance > 0.5) ? Color.BLACK : Color.WHITE;

            for (TextView letter : letters) {
                int currentColor = letter.getCurrentTextColor();
                int blendedColor = (int) new ArgbEvaluator().evaluate(0.1f, currentColor, targetColor);
                letter.setTextColor(blendedColor);
            }
        });

        colorAnimator.start();
    }
}