package com.geohunt.backend.Shop.DTOs;

import lombok.Data;

@Data
public class PowerupShopDTO {
    private String shopName;
    private String description;
    private long price;
    private String image;
    private String powerupName;
    private String affect;
}
