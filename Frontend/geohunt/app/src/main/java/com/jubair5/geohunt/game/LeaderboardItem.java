/**
 * Leaderboard item (players)
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.game;

public class LeaderboardItem {
    private String username;
    private double distance;

    public LeaderboardItem(String username, double distance) {
        this.username = username;
        this.distance = distance;
    }

    public String getUsername() {
        return username;
    }

    public double getDistance() {
        return distance;
    }
}
