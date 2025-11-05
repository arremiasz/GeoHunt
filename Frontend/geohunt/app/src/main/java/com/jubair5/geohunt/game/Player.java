/**
 * Player class to handle websocket players
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.game;

public class Player {
    private String userId;
    private String username;

    public Player(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
