package com.jubair5.geohunt.shop;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Model class representing an item in the shop.
 */
public class ShopItem {
    private int id;
    private String title;
    private String description;
    private int cost;

    public ShopItem(int id, String title, String description, int cost) {
        this.title = title;
        this.description = description;
        this.cost = cost;
    }

    public ShopItem(JSONObject json) throws JSONException {
        this.id = json.getInt("id");
        this.title = json.getString("name");
        this.description = json.optString("description", "");
        this.cost = json.getInt("price");
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getCost() {
        return cost;
    }
}
