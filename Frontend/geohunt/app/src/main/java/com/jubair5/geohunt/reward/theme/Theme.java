/**
 * Friend Class
 * @author Nathan Imig
 */
package com.jubair5.geohunt.reward.theme;

import org.json.JSONObject;

public class Theme {
    private String name;
    private int price;
    private boolean obtained;


    public Theme(JSONObject json) {
        name = json.optString("name");
        price = Integer.parseInt(json.optString("price"));
        obtained = Boolean.parseBoolean(json.optString("obtained"));
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public boolean isObtained() {
        return obtained;
    }
}
