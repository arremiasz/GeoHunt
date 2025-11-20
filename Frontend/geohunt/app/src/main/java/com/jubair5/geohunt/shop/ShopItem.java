package com.jubair5.geohunt.shop;

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

    public String getTitle() {
        return title;
    }

    public int getCost() {
        return cost;
    }
}
