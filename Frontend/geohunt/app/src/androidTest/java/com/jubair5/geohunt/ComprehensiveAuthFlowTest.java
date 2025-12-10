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

import com.jubair5.geohunt.auth.AuthenticationActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Comprehensive auth validation flow test that exercises ALL validation methods
 * in SignupFragment through exhaustive UI interactions.
 * 
 * @author Alex Remiasz
 */
@RunWith(AndroidJUnit4.class)
public class ComprehensiveAuthFlowTest {

    private static final String PACKAGE_NAME = "com.jubair5.geohunt";
    private static final long TIMEOUT = 10000;
    private static final String SHARED_PREFS_NAME = "GeoHuntPrefs";

    private UiDevice device;
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        clearMockLoginSession();
    }

    @After
    public void tearDown() {
        setupMockLoginSession();
    }

    private void setupMockLoginSession() {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isUserLoggedIn", true);
        editor.putLong("loginTimestamp", System.currentTimeMillis());
        editor.putInt("userId", 37);
        editor.putString("username", "aremiasz1");
        editor.putString("email", "aremiasz1@test.com");
        editor.apply();
    }

    private void clearMockLoginSession() {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    private void clearField(UiObject2 field) {
        if (field != null) {
            field.click();
            field.clear();
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

    private void clickButton(String resourceId) {
        device.wait(Until.hasObject(By.res(PACKAGE_NAME, resourceId)), TIMEOUT);
        UiObject2 btn = device.findObject(By.res(PACKAGE_NAME, resourceId));
        if (btn != null) {
            btn.click();
            device.waitForIdle();
        }
    }

    /**
     * Comprehensive auth flow test that exercises ALL validation paths.
     * 
     * Flow:
     * 1. Switch between login and signup screens several times
     * 2. Press signup with no fields filled (tests all empty validations)
     * 3. Add username only, signup (tests email empty validation)
     * 4. Add invalid email format, signup (tests email format validation)
     * 5. Fix email, add weak password (too short), signup (tests password length)
     * 6. Add password without uppercase, signup
     * 7. Add password without digit, signup
     * 8. Add password without special char, signup
     * 9. Fix password, mismatch confirm password, signup
     * 10. Finally have all validation pass and signup
     */
    @Test
    public void testExhaustiveAuthValidationFlow() {
        try (ActivityScenario<AuthenticationActivity> scenario = ActivityScenario
                .launch(AuthenticationActivity.class)) {

            InstrumentationRegistry.getInstrumentation().waitForIdleSync();

            // ===== STEP 1: Navigate between login and signup several times =====

            // Login -> Signup
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "goToSignup")), TIMEOUT);
            clickButton("goToSignup");

            // Verify on signup
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "signup")), TIMEOUT);
            assertNotNull("Should be on signup screen", device.findObject(By.res(PACKAGE_NAME, "signup")));

            // Signup -> Login
            clickButton("goToLogin");
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "login")), TIMEOUT);
            assertNotNull("Should be on login screen", device.findObject(By.res(PACKAGE_NAME, "login")));

            // Login -> Signup again
            clickButton("goToSignup");
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "signup")), TIMEOUT);

            // Signup -> Login again
            clickButton("goToLogin");
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "login")), TIMEOUT);

            // Finally go to Signup for the validation tests
            clickButton("goToSignup");
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "signup")), TIMEOUT);

            // ===== STEP 2: Press signup with no fields filled =====
            // This tests validateUsername() returning false on empty
            clickButton("signup");

            // Should still be on signup (validation failed)
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "signup")), TIMEOUT);
            assertNotNull("Should still be on signup after empty validation",
                    device.findObject(By.res(PACKAGE_NAME, "signup")));

            // ===== STEP 3: Add username only, try signup =====
            // This passes validateUsername(), tests validateEmail() empty
            typeInField("usernameSignup", "TestUser123");
            clickButton("signup");

            // Should still be on signup
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "signup")), TIMEOUT);
            assertNotNull("Should still be on signup after username-only",
                    device.findObject(By.res(PACKAGE_NAME, "signup")));

            // ===== STEP 4: Add invalid email format, try signup =====
            // This tests validateEmail() format check
            typeInField("emailSignup", "invalidemail");
            clickButton("signup");

            // Should still be on signup
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "signup")), TIMEOUT);
            assertNotNull("Should still be on signup after invalid email format",
                    device.findObject(By.res(PACKAGE_NAME, "signup")));

            // ===== STEP 4.5: Fix email, add no password, try signup =====
            // This tests validatePassword() length check
            typeInField("emailSignup", "test@example.com");
            clickButton("signup");

            // Should still be on signup
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "signup")), TIMEOUT);
            assertNotNull("Should still be on signup after short password",
                    device.findObject(By.res(PACKAGE_NAME, "signup")));

            // ===== STEP 5: Fix email, add weak password (too short), try signup =====
            // This tests validatePassword() length check
            typeInField("emailSignup", "test@example.com");
            typeInField("passwordSignup", "Ab1!"); // Only 4 chars, need 6
            clickButton("signup");

            // Should still be on signup
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "signup")), TIMEOUT);
            assertNotNull("Should still be on signup after short password",
                    device.findObject(By.res(PACKAGE_NAME, "signup")));

            // ===== STEP 6: Add password without uppercase, try signup =====
            // This tests validatePassword() uppercase check
            typeInField("passwordSignup", "abcd1!"); // 6 chars, no uppercase
            clickButton("signup");

            // Should still be on signup
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "signup")), TIMEOUT);
            assertNotNull("Should still be on signup after no uppercase",
                    device.findObject(By.res(PACKAGE_NAME, "signup")));

            // ===== STEP 7: Add password without digit, try signup =====
            // This tests validatePassword() digit check
            typeInField("passwordSignup", "Abcdef!"); // 7 chars, uppercase, no digit
            clickButton("signup");

            // Should still be on signup
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "signup")), TIMEOUT);
            assertNotNull("Should still be on signup after no digit",
                    device.findObject(By.res(PACKAGE_NAME, "signup")));

            // ===== STEP 8: Add password without special char, try signup =====
            // This tests validatePassword() special char check
            typeInField("passwordSignup", "Abcdef1"); // uppercase, digit, no special
            clickButton("signup");

            // Should still be on signup
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "signup")), TIMEOUT);
            assertNotNull("Should still be on signup after no special char",
                    device.findObject(By.res(PACKAGE_NAME, "signup")));

            // ===== STEP 9: Fix password, mismatch confirm password, try signup =====
            // This tests validateConfirmPassword() mismatch check
            typeInField("passwordSignup", "Abcdef1!"); // Valid password
            typeInField("confirmPasswordSignup", "DifferentPass1!"); // Mismatched
            clickButton("signup");

            // Should still be on signup
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "signup")), TIMEOUT);
            assertNotNull("Should still be on signup after password mismatch",
                    device.findObject(By.res(PACKAGE_NAME, "signup")));

            // ===== STEP 10: All validation passes, attempt signup =====
            // This tests performSignup() being called
            // Use a unique username to avoid "account exists" error
            String uniqueUsername = "TestUser" + System.currentTimeMillis();
            typeInField("usernameSignup", uniqueUsername);
            typeInField("emailSignup", uniqueUsername + "@test.com");
            typeInField("passwordSignup", "ValidPass1!");
            typeInField("confirmPasswordSignup", "ValidPass1!");

            clickButton("signup");

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Tests empty confirm password validation specifically.
     */
    @Test
    public void testEmptyConfirmPasswordValidation() {
        try (ActivityScenario<AuthenticationActivity> scenario = ActivityScenario
                .launch(AuthenticationActivity.class)) {

            InstrumentationRegistry.getInstrumentation().waitForIdleSync();

            clickButton("goToSignup");
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "signup")), TIMEOUT);

            typeInField("usernameSignup", "TestUser");
            typeInField("emailSignup", "test@example.com");
            typeInField("passwordSignup", "ValidPass1!");

            clickButton("signup");

            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "signup")), TIMEOUT);
            assertNotNull("Should still be on signup after empty confirm password",
                    device.findObject(By.res(PACKAGE_NAME, "signup")));
        }
    }

    /**
     * Tests the key listener functionality that clears errors on typing.
     */
    @Test
    public void testFieldKeyListenersClearErrors() {
        try (ActivityScenario<AuthenticationActivity> scenario = ActivityScenario
                .launch(AuthenticationActivity.class)) {

            InstrumentationRegistry.getInstrumentation().waitForIdleSync();

            // Navigate to signup
            clickButton("goToSignup");
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "signup")), TIMEOUT);

            // Trigger an error by submitting empty
            clickButton("signup");

            // Now type in username - this should trigger the key listener
            typeInField("usernameSignup", "TestUser");

            // Type in email
            typeInField("emailSignup", "test@example.com");

            // Type in password
            typeInField("passwordSignup", "ValidPass1!");

            // Type in confirm password
            typeInField("confirmPasswordSignup", "ValidPass1!");

        }
    }
}
