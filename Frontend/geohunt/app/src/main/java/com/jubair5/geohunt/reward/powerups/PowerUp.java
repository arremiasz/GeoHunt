package com.jubair5.geohunt.reward.powerups;

/**
 * Interface that power ups can branch off of
 * @author Nathan Imig
 */
public interface PowerUp {
    void activate();
    int getImage();
    int getAmount();
    void setAmount(int amount);
    String getTitle();
    String getDescription();

}
