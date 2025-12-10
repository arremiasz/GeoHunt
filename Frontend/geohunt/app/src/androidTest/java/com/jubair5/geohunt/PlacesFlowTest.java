package com.jubair5.geohunt;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.Direction;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Places flow test: add a place via camera, view it, delete it.
 * 
 * @author Alex Remiasz
 */
@RunWith(AndroidJUnit4.class)
public class PlacesFlowTest {

    private static final String PACKAGE_NAME = "com.jubair5.geohunt";
    private static final long TIMEOUT = 15000;
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
     * Complete places flow:
     * 1. Navigate to profile
     * 2. Click the add place button (first item in places grid)
     * 3. Take a photo when camera opens
     * 4. Confirm the photo
     * 5. Press submit
     * 6. Back to profile, click the new place
     * 7. Scroll down and press delete
     * 8. Confirm delete on popup
     */
    @Test
    public void testAddViewDeletePlaceFlow() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            dismissPermissionDialogIfPresent();

            // Step 1: Navigate to Profile tab
            device.wait(Until.hasObject(By.text("Profile")), TIMEOUT);
            UiObject2 profileTab = device.findObject(By.text("Profile"));
            assertNotNull("Profile tab should exist", profileTab);
            profileTab.click();
            device.waitForIdle();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }

            // Step 2: Click the add place button (first item in places RecyclerView with
            // add_icon)
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "add_icon")), TIMEOUT);
            UiObject2 addPlaceBtn = device.findObject(By.res(PACKAGE_NAME, "add_icon"));
            assertNotNull("Add place button should exist", addPlaceBtn);
            addPlaceBtn.click();
            device.waitForIdle();

            dismissPermissionDialogIfPresent();

            // Step 3: Wait for camera to load and take a picture
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
                device.click(device.getDisplayWidth() / 2, device.getDisplayHeight() / 2);
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

            // Step 4: Confirm the photo (usually a checkmark or "Done" button)
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
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

            // Step 5: Press submit button
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "submit_button")), TIMEOUT);
            UiObject2 submitBtn = device.findObject(By.res(PACKAGE_NAME, "submit_button"));
            if (submitBtn != null && submitBtn.isEnabled()) {
                submitBtn.click();
                device.waitForIdle();

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
            }

            // Step 6: Back to profile, wait for places to reload and click the new place
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "place_image")), TIMEOUT);
            UiObject2 placeImage = device.findObject(By.res(PACKAGE_NAME, "place_image"));
            if (placeImage != null) {
                placeImage.click();
                device.waitForIdle();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }

                // Step 7: Scroll down to see delete button and click it
                device.swipe(
                        device.getDisplayWidth() / 2,
                        device.getDisplayHeight() * 3 / 4,
                        device.getDisplayWidth() / 2,
                        device.getDisplayHeight() / 4,
                        10);
                device.waitForIdle();

                device.wait(Until.hasObject(By.res(PACKAGE_NAME, "delete_button")), TIMEOUT);
                UiObject2 deleteBtn = device.findObject(By.res(PACKAGE_NAME, "delete_button"));
                if (deleteBtn != null) {
                    deleteBtn.click();
                    device.waitForIdle();

                    // Step 8: Confirm delete on popup
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }

                    UiObject2 confirmDeleteBtn = device.findObject(By.res("android:id/button1"));
                    if (confirmDeleteBtn == null) {
                        confirmDeleteBtn = device.findObject(By.text("Delete"));
                    }
                    if (confirmDeleteBtn != null) {
                        confirmDeleteBtn.click();
                        device.waitForIdle();

                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            // Ignore
                        }
                    }
                }
            }

        }
    }
}
