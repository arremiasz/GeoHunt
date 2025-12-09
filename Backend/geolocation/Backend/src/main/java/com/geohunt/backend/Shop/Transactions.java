package com.geohunt.backend.Shop;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.geohunt.backend.database.Account;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long transactionid;

    @ManyToOne
    @JoinColumn(name="shop_id")
    @JsonBackReference("transactions-shop")
    private Shop shopItem;

    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonBackReference("transactions-user")
    private Account user;

    private Date date;
    private long price;
}
