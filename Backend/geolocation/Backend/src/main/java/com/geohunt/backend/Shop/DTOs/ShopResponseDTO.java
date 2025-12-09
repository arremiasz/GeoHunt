package com.geohunt.backend.Shop.DTOs;

import com.geohunt.backend.Shop.Shop;
import lombok.Data;

@Data
public class ShopResponseDTO {
    private Shop shop;
    private String message;
}
