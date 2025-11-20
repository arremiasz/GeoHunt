package com.geohunt.backend.rewards;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Reward {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public @Id long id;
    public String name;
    public String rewardType;
    public String imagePath;
    public double valueInPoints;

    public void update(Reward other){
        this.name = other.getName();
        this.rewardType = other.getRewardType();
        this.imagePath = other.imagePath;
        this.valueInPoints = other.valueInPoints;
    }
}
