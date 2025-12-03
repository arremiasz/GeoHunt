package com.jubair5.geohunt.reward.powerups;

/**
 * Interface that power ups can branch off of
 * @author Nathan Imig
 */
public interface PowerUp {
    void activate();
    void getPowerUp();
    String getDescription();

}
