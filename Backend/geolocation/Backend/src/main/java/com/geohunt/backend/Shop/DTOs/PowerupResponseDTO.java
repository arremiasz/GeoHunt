package com.geohunt.backend.Shop.DTOs;

import com.geohunt.backend.powerup.Powerup;
import lombok.Data;

@Data
public class PowerupResponseDTO {
    private Powerup powerup;
    private String message;
}
