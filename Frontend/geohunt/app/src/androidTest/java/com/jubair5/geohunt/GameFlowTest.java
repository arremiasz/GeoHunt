package com.jubair5.geohunt;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Full gameplay flow test.
 * 
 * @author Alex Remiasz
 */
@RunWith(AndroidJUnit4.class)
public class GameFlowTest {

    private static final String PACKAGE_NAME = "com.jubair5.geohunt";
    private static final long TIMEOUT = 20000;
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";

    private UiDevice device;
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        setupLoginSession();
    }

    @After
    public void tearDown() {
        setupLoginSession();
    }

    private void setupLoginSession() {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isUserLoggedIn", true);
        editor.putLong("loginTimestamp", System.currentTimeMillis());
        editor.putInt("userId", 37);
        editor.putString("username", "aremiasz1");
        editor.putString("email", "aremiasz1@test.com");
        editor.apply();
    }

    private void dismissPermissionDialogIfPresent() {
        device.wait(Until.hasObject(By.textContains("While using")), 2000);
        UiObject2 allowBtn = device.findObject(By.text("While using the app"));
        if (allowBtn != null) {
            allowBtn.click();
            device.waitForIdle();
            return;
        }
        allowBtn = device.findObject(By.text("Allow"));
        if (allowBtn != null) {
            allowBtn.click();
            device.waitForIdle();
        }
    }

    /**
     * Full gameplay flow:
     * 1. Press play button on home
     * 2. Press ready button after map loads
     * 3. Wait for countdown to finish
     * 4. Let image load, tap center to minimize
     * 5. Press guess button and take photo
     * 6. Confirm photo
     * 7. On results screen, press 3-star rating
     * 8. Scroll down and leave a comment
     * 9. Press return to home
     */
    @Test
    public void testFullGameplayFlow() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            dismissPermissionDialogIfPresent();

            // Step 1: Press play button on home
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "play_button")), TIMEOUT);
            UiObject2 playBtn = device.findObject(By.res(PACKAGE_NAME, "play_button"));
            assertNotNull("Play button should exist", playBtn);
            playBtn.click();
            device.waitForIdle();

            dismissPermissionDialogIfPresent();

            // Step 2: Wait for map to load and press Ready button
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "ready_button")), TIMEOUT);
            UiObject2 readyBtn = device.findObject(By.res(PACKAGE_NAME, "ready_button"));
            assertNotNull("Ready button should exist", readyBtn);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }

            readyBtn.click();
            device.waitForIdle();

            // Step 3: Wait for countdown to finish (countdown is 3 seconds + image load)
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
            }

            // Step 4: Tap center of screen to minimize hint image
            int centerX = device.getDisplayWidth() / 2;
            int centerY = device.getDisplayHeight() / 2;
            device.click(centerX, centerY);
            device.waitForIdle();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

            UiObject2 hintBox = device.findObject(By.res(PACKAGE_NAME, "hint_container"));
            int bx = hintBox.getVisibleBounds().centerX();
            int by = hintBox.getVisibleBounds().centerY();

            device.swipe(bx, by, bx * 3, by, 10);
            device.waitForIdle();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }


            // Step 5: Press guess button
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "guess_button")), TIMEOUT);
            UiObject2 guessBtn = device.findObject(By.res(PACKAGE_NAME, "guess_button"));
            if (guessBtn != null) {
                guessBtn.click();
                device.waitForIdle();

                dismissPermissionDialogIfPresent();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }

                UiObject2 shutterBtn = device.findObject(By.descContains("Shutter"));
                if (shutterBtn == null) {
                    shutterBtn = device.findObject(By.descContains("Take photo"));
                }
                if (shutterBtn == null) {
                    shutterBtn = device.findObject(By.descContains("Capture"));
                }
                if (shutterBtn == null) {
                    device.click(centerX, centerY);
                    device.waitForIdle();
                }

                if (shutterBtn != null) {
                    shutterBtn.click();
                    device.waitForIdle();
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }

                // Step 6: Confirm photo
                UiObject2 confirmBtn = device.findObject(By.descContains("Done"));
                if (confirmBtn == null) {
                    confirmBtn = device.findObject(By.descContains("OK"));
                }
                if (confirmBtn == null) {
                    confirmBtn = device.findObject(By.descContains("Confirm"));
                }
                if (confirmBtn == null) {
                    confirmBtn = device.findObject(By.descContains("check"));
                }

                if (confirmBtn != null) {
                    confirmBtn.click();
                    device.waitForIdle();
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }

                // Step 7: Press 3-star rating on results screen
                device.wait(Until.hasObject(By.res(PACKAGE_NAME, "rating_bar")), TIMEOUT);
                UiObject2 ratingBar = device.findObject(By.res(PACKAGE_NAME, "rating_bar"));
                if (ratingBar != null) {
                    int ratingX = ratingBar.getVisibleBounds().left +
                            (ratingBar.getVisibleBounds().width() * 3 / 5);
                    int ratingY = ratingBar.getVisibleCenter().y;
                    device.click(ratingX, ratingY);
                    device.waitForIdle();

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                    }

                    // Step 8: Scroll down and leave a comment
                    device.swipe(centerX, device.getDisplayHeight() * 3 / 4, centerX, device.getDisplayHeight() / 4, 10);
                    device.waitForIdle();

                    device.wait(Until.hasObject(By.res(PACKAGE_NAME, "comment_input")), TIMEOUT);
                    UiObject2 commentInput = device.findObject(By.res(PACKAGE_NAME, "comment_input"));
                    if (commentInput != null) {
                        commentInput.setText("This is an automated test comment!");
                        device.waitForIdle();

                        UiObject2 postCommentBtn = device.findObject(By.res(PACKAGE_NAME, "send_comment_button"));
                        if (postCommentBtn != null) {
                            postCommentBtn.click();
                            device.waitForIdle();
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                    device.swipe(centerX, device.getDisplayHeight() / 4, centerX, device.getDisplayHeight() * 3 / 4, 10);
                    device.waitForIdle();
                }

                // Step 9: Press return to home button
                device.wait(Until.hasObject(By.res(PACKAGE_NAME, "go_home_button")), TIMEOUT);
                UiObject2 goHomeBtn = device.findObject(By.res(PACKAGE_NAME, "go_home_button"));
                if (goHomeBtn != null) {
                    goHomeBtn.click();
                    device.waitForIdle();

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                    }

                    device.wait(Until.hasObject(By.res(PACKAGE_NAME, "play_button")), TIMEOUT);
                    UiObject2 homePlayBtn = device.findObject(By.res(PACKAGE_NAME, "play_button"));
                    assertNotNull("Should be back on home screen", homePlayBtn);
                }
            }

        }
    }
}
