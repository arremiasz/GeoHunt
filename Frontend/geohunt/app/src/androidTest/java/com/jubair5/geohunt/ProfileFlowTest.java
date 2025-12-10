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

import com.jubair5.geohunt.LauncherActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Profile flow test that exercises profile editing functionality.
 *
 * @author Alex Remiasz
 */
@RunWith(AndroidJUnit4.class)
public class ProfileFlowTest {

    private static final String PACKAGE_NAME = "com.jubair5.geohunt";
    private static final long TIMEOUT = 15000;
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";

    private UiDevice device;
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        clearLoginSession();
    }

    @After
    public void tearDown() {
        setupLoginSession();
    }

    private void clearLoginSession() {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    private void setupLoginSession() {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isUserLoggedIn", true);
        editor.putLong("loginTimestamp", System.currentTimeMillis());
        editor.putInt("userId", 61);
        editor.putString("username", "aremiasz1");
        editor.putString("email", "junit@email.com");
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

    private void typeInField(String resourceId, String text) {
        UiObject2 field = device.findObject(By.res(PACKAGE_NAME, resourceId));
        if (field != null) {
            field.click();
            field.clear();
            field.setText(text);
            device.waitForIdle();
            device.pressBack();
            device.waitForIdle();
        }
    }

    /**
     * Profile edit flow: navigate to profile, edit email, enter password, save.
     */
    @Test
    public void testProfileEditFlow() {
        try (ActivityScenario<LauncherActivity> scenario = ActivityScenario.launch(LauncherActivity.class)) {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();

            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "login_button")), TIMEOUT);

            typeInField("usernameLogin", "aremiasz1");
            typeInField("passwordLogin", "Pass1!");

            UiObject2 loginBtn = device.findObject(By.res(PACKAGE_NAME, "login"));
            assertNotNull("Login button should exist", loginBtn);
            loginBtn.click();
            device.waitForIdle();

            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "main_activity_container")), TIMEOUT);
            dismissPermissionDialogIfPresent();

            device.wait(Until.hasObject(By.text("Profile")), TIMEOUT);
            UiObject2 profileTab = device.findObject(By.text("Profile"));
            assertNotNull("Profile tab should exist", profileTab);
            profileTab.click();
            device.waitForIdle();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }

            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "edit_account_button")), TIMEOUT);
            UiObject2 editAccountBtn = device.findObject(By.res(PACKAGE_NAME, "edit_account_button"));
            assertNotNull("Edit account button should exist", editAccountBtn);

            editAccountBtn.click();
            device.waitForIdle();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            String newEmail = "test" + System.currentTimeMillis() + "@example.com";
            typeInField("edit_email", newEmail);

            typeInField("edit_current_password", "Pass1!");

            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "save_changes_button")), TIMEOUT);
            UiObject2 saveBtn = device.findObject(By.res(PACKAGE_NAME, "save_changes_button"));
            if (saveBtn != null) {
                saveBtn.click();
                device.waitForIdle();

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
            } else {
                fail("Save changes button not found");
            }
        }
    }

    /**
     * Profile edit flow: navigate to profile, edit email, enter password, save. (but it breaks)
     */
    @Test
    public void testProfileEditFlowWithError() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            dismissPermissionDialogIfPresent();

            device.wait(Until.hasObject(By.text("Profile")), TIMEOUT);
            UiObject2 profileTab = device.findObject(By.text("Profile"));
            assertNotNull("Profile tab should exist", profileTab);
            profileTab.click();
            device.waitForIdle();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }

            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "edit_account_button")), TIMEOUT);
            UiObject2 editAccountBtn = device.findObject(By.res(PACKAGE_NAME, "edit_account_button"));
            assertNotNull("Edit account button should exist", editAccountBtn);

            editAccountBtn.click();
            device.waitForIdle();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "delete_account_button")), TIMEOUT);
            UiObject2 saveBtn = device.findObject(By.res(PACKAGE_NAME, "delete_account_button"));
            if (saveBtn != null) {
                saveBtn.click();
                device.waitForIdle();

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
                    }
                }

            } else {
                fail("Save changes button not found");
            }

        }
    }

    @Test
    public void testProfileEditFlowWithLogout() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            dismissPermissionDialogIfPresent();

            device.wait(Until.hasObject(By.text("Profile")), TIMEOUT);
            UiObject2 profileTab = device.findObject(By.text("Profile"));
            assertNotNull("Profile tab should exist", profileTab);
            profileTab.click();
            device.waitForIdle();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }

            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "edit_account_button")), TIMEOUT);
            UiObject2 editAccountBtn = device.findObject(By.res(PACKAGE_NAME, "edit_account_button"));
            assertNotNull("Edit account button should exist", editAccountBtn);

            editAccountBtn.click();
            device.waitForIdle();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "logout_button")), TIMEOUT);
            UiObject2 saveBtn = device.findObject(By.res(PACKAGE_NAME, "logout_button"));
            if (saveBtn != null) {
                saveBtn.click();
                device.waitForIdle();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
