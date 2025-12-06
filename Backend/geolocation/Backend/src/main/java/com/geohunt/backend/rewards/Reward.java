package com.geohunt.backend.rewards;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.geohunt.backend.images.Image;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "reward_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Reward {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public long id;
    public String name;
    public double valueInPoints;

    @OneToOne
    public Image rewardImage;
}
