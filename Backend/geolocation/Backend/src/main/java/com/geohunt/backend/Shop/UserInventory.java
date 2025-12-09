package com.geohunt.backend.Shop;

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
public class UserInventory {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private Account user;

    @ManyToOne
    @JoinColumn(name="shop_id")
    private Shop shopItem;

    private int quantity;
    private boolean equipped;
    private Date acquiredAt;
}
