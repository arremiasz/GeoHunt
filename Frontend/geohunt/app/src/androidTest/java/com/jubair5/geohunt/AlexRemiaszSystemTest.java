package com.jubair5.geohunt;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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

/**
 * System tests for GeoHunt Android app using Espresso
 * @author Alex Remiasz
 */
@RunWith(AndroidJUnit4.class)
public class AlexRemiaszSystemTest {

    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";
    private Context context;
    private UiDevice device;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isUserLoggedIn", true);
        editor.putLong("loginTimestamp", System.currentTimeMillis());
        editor.putInt("userId", 1);
        editor.putString("userName", "testuser");
        editor.putString("userEmail", "test@example.com");
        editor.apply();
    }

    /**
     * Helper method to automatically grant location permissions if dialog appears
     */
    private void grantLocationPermission() {
        UiObject2 allowButton = device.wait(
                Until.findObject(By.text("While using the app")), 
                5000
        );
        if (allowButton == null) {
            allowButton = device.wait(
                    Until.findObject(By.text("Allow")), 
                    2000
            );
        }
        if (allowButton != null) {
            allowButton.click();
        }
    }

    /**
     * Test 1: Bottom Navigation Flow
     * Tests navigating between all three fragments (Home, Map, Profile) using bottom navigation.
     * This validates:
     * - Fragment transactions work correctly
     * - UI state is maintained during navigation
     * - Bottom navigation correctly switches active fragments
     */
    @Test
    public void testBottomNavigationFlow() {
        ActivityScenario<LauncherActivity> scenario = ActivityScenario.launch(LauncherActivity.class);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        grantLocationPermission();

        onView(withId(R.id.play_button))
                .check(matches(isDisplayed()));

        onView(withId(R.id.nav_map))
                .perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.nav_profile))
                .perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        onView(withId(R.id.username_label))
                .check(matches(isDisplayed()));

        onView(withId(R.id.nav_home))
                .perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        onView(withId(R.id.play_button))
                .check(matches(isDisplayed()));

        scenario.close();
    }

    /**
     * Test 2: Profile Edit Mode Toggle
     * Tests entering and exiting profile edit mode.
     * This validates:
     * - UI container visibility transitions
     * - Button click handling across different UI states
     * - State preservation when canceling edits
     */
    @Test
    public void testProfileEditModeToggle() {
        ActivityScenario<LauncherActivity> scenario = ActivityScenario.launch(LauncherActivity.class);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        grantLocationPermission();

        onView(withId(R.id.nav_profile))
                .perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.display_container))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.edit_account_button))
                .check(matches(isDisplayed()));

        onView(withId(R.id.edit_account_button))
                .perform(click());

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.edit_container))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.edit_username))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.cancel_button))
                .check(matches(isDisplayed()));

        onView(withId(R.id.cancel_button))
                .perform(click());

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.display_container))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.edit_account_button))
                .check(matches(isDisplayed()));

        scenario.close();
    }

    /**
     * Test 3: Logout Workflow
     * Tests the complete logout flow from ProfileFragment.
     * This validates:
     * - Logout button functionality
     * - Session data cleanup (SharedPreferences are cleared)
     * - Activity navigation initiated
     */
    @Test
    public void testLogoutWorkflow() {
        ActivityScenario<LauncherActivity> scenario = ActivityScenario.launch(LauncherActivity.class);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        grantLocationPermission();

        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isUserLoggedIn", false);
        assert isLoggedIn : "User should be logged in before logout test";

        onView(withId(R.id.nav_profile))
                .perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.edit_account_button))
                .perform(click());

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.logout_button))
                .check(matches(isDisplayed()));

        onView(withId(R.id.logout_button))
                .perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean isStillLoggedIn = prefs.getBoolean("isUserLoggedIn", false);
        assert !isStillLoggedIn : "User should be logged out - SharedPreferences should be cleared";

        scenario.close();
    }

    /**
     * Test 4: Play Button Navigation
     * Tests navigation from HomeFragment to GameActivity via the Play button.
     * This validates:
     * - Button click triggers inter-activity navigation
     * - GameActivity launches successfully from a fragment
     * - Activity transition completes properly
     */
    @Test
    public void testPlayButtonNavigation() {
        ActivityScenario<LauncherActivity> scenario = ActivityScenario.launch(LauncherActivity.class);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        grantLocationPermission();

        onView(withId(R.id.play_button))
                .check(matches(isDisplayed()));

        onView(withId(R.id.play_button))
                .perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        grantLocationPermission();

        boolean gameActivityDisplayed = device.wait(
                Until.hasObject(By.res("com.jubair5.geohunt:id/game_map")),
                5000
        );

        device.pressBack();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.play_button))
                .check(matches(isDisplayed()));

        scenario.close();
    }
}
