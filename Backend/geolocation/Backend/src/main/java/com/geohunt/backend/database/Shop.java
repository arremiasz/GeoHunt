package com.geohunt.backend.database;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.geohunt.backend.util.SHOP_ITEM_TYPE;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    private String name;
    private String description;

    @Column(columnDefinition = "mediumtext")
    private String image;

    @Enumerated(EnumType.STRING)
    private SHOP_ITEM_TYPE itemType;

    private float price;

    @OneToMany(mappedBy = "shopItem")
    @JsonManagedReference("transactions-shop")
    private List<Transactions> transactions;

}