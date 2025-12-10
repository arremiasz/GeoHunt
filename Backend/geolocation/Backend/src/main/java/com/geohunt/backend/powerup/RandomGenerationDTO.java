package com.geohunt.backend.powerup;


import lombok.Data;

@Data
public class RandomGenerationDTO {
    public double lat;
    public double lon;
    public Powerup powerup;
}
