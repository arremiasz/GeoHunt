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
 * Shop flow test that purchases a shop item.
 * @author Alex Remiasz
 */
@RunWith(AndroidJUnit4.class)
public class ShopFlowTest {

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
     * Complete shop flow: navigate to shop, wait for items to load, purchase an
     * item.
     */
    @Test
    public void testShopPurchaseFlow() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            dismissPermissionDialogIfPresent();

            device.wait(Until.hasObject(By.text("Shop")), TIMEOUT);
            UiObject2 shopTab = device.findObject(By.text("Shop"));
            assertNotNull("Shop tab should exist", shopTab);
            shopTab.click();
            device.waitForIdle();

            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "shop_title")), TIMEOUT);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }

            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "shop_item_buy_button")), TIMEOUT);
            UiObject2 buyButton = device.findObject(By.res(PACKAGE_NAME, "shop_item_buy_button"));

            if (buyButton != null) {
                buyButton.click();
                device.waitForIdle();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                UiObject2 confirmBuyButton = device.findObject(By.res("android:id/button1"));
                if (confirmBuyButton == null) {
                    confirmBuyButton = device.findObject(By.text("Buy"));
                }
                if (confirmBuyButton == null) {
                    confirmBuyButton = device.findObject(By.textContains("Buy").clickable(true));
                }

                if (confirmBuyButton != null) {
                    confirmBuyButton.click();
                    device.waitForIdle();

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                    }
                } else {
                    fail("Could not find confirmation Buy button");
                }
            } else {
                fail("No shop_item_buy_button found - shop items may not have loaded");
            }
        }
    }
}
