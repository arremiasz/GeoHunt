package com.geohunt.backend.Shop.DTOs;

import com.geohunt.backend.Shop.Shop;
import com.geohunt.backend.database.Account;
import lombok.Data;

import java.util.Date;

@Data
public class TransactionDTO {
    private Shop ShopItem;
    private Account user;
    private Date date;
    private long price;
}
