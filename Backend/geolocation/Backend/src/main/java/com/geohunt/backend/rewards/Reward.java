package com.geohunt.backend.rewards;

import com.geohunt.backend.Shop.Shop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents inventory items that can be obtained via submissions
 * @author Evan Julson
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Reward {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    private int value; // Value used to determine probability of getting reward.

    @OneToOne(cascade = CascadeType.ALL)
    Shop shopItem; // Reference to the shop item
}
