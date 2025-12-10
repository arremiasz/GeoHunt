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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Launcher flow test that exercises LauncherActivity routing.
 * 
 * @author Alex Remiasz
 */
@RunWith(AndroidJUnit4.class)
public class LauncherFlowTest {

    private static final String PACKAGE_NAME = "com.jubair5.geohunt";
    private static final long TIMEOUT = 15000;
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";

    private UiDevice device;
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    private void setupActiveSession() {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isUserLoggedIn", true);
        editor.putLong("loginTimestamp", System.currentTimeMillis());
        editor.putInt("userId", 37);
        editor.putString("username", "aremiasz1");
        editor.putString("email", "aremiasz1@test.com");
        editor.apply();
    }

    private void clearSession() {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
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
     * Test launching with active session - should go to MainActivity.
     */
    @Test
    public void testLaunchWithActiveSession() {
        setupActiveSession();

        try (ActivityScenario<LauncherActivity> scenario = ActivityScenario.launch(LauncherActivity.class)) {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            dismissPermissionDialogIfPresent();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

            device.wait(Until.hasObject(By.text("Home")), TIMEOUT);
            UiObject2 homeTab = device.findObject(By.text("Home"));

            assertNotNull("With active session, should navigate to MainActivity and see Home tab", homeTab);
        }

        setupActiveSession();
    }

    /**
     * Test launching without active session - should go to AuthenticationActivity.
     */
    @Test
    public void testLaunchWithoutActiveSession() {
        clearSession();

        try (ActivityScenario<LauncherActivity> scenario = ActivityScenario.launch(LauncherActivity.class)) {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "login")), TIMEOUT);
            UiObject2 loginBtn = device.findObject(By.res(PACKAGE_NAME, "login"));

            assertNotNull("Without active session, should navigate to AuthenticationActivity and see login", loginBtn);
        }

        setupActiveSession();
    }
}
