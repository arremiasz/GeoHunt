package com.jubair5.geohunt.shop;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Model class representing an item in the shop.
 */
public class ShopItem {
    private String title;
    private int cost;

    public ShopItem(String title, int cost) {
        this.title = title;
        this.cost = cost;
    }

    public ShopItem(JSONObject json) throws JSONException {
        this.title = json.getString("title");
        this.cost = json.getInt("cost");
    }

    public String getTitle() {
        return title;
    }

    public int getCost() {
        return cost;
    }
}
