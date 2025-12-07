package com.geohunt.backend.rewards;

import com.geohunt.backend.images.Image;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "reward_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Reward {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private String type;
    private String name;
    private int value;

    @OneToOne
    public Image rewardImage;
}
