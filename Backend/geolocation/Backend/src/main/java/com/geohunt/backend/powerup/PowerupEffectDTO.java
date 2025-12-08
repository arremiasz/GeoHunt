package com.geohunt.backend.powerup;

import com.geohunt.backend.database.Account;
import lombok.Data;

@Data
public class PowerupEffectDTO {
    private Powerup powerup;
    private PowerupEffects effects;
    private String LocationName;
    private int timeDecreaseInSeconds;
    private Account account;
}
