/**
 * Player class to handle websocket players
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.game;

public class Player {
    private String userId;
    private String username;
    private boolean isReady;

    public Player(String userId, String username, boolean isReady) {
        this.userId = userId;
        this.username = username;
        this.isReady = isReady;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public boolean isReady() {
        return isReady;
    }

    /**
     * Sets the ready status of the player.
     * @param ready The new ready status.
     */
    public void setReady(boolean ready) {
        isReady = ready;
    }
}
