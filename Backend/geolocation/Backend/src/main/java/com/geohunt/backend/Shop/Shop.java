package com.geohunt.backend.Shop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.geohunt.backend.powerup.Powerup;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({
        "userInventoryEntries",
        "transactions"
})
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "shopItem")
    @JsonIgnoreProperties("shopItem")
    private List<UserInventory> userInventoryEntries = new ArrayList<>();

    private String name;
    private String description;

    @OneToOne(optional = true)
    @JoinColumn(name = "powerup_id", unique = true)
    private Powerup powerup;

    @Column(columnDefinition = "mediumtext")
    private String image;

    @Enumerated(EnumType.STRING)
    private SHOP_ITEM_TYPE itemType;

    private long price;

    @OneToMany(mappedBy = "shopItem")
    @JsonManagedReference("transactions-shop")
    private List<Transactions> transactions;
}