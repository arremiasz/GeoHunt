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
    private @Id long id;
    private String name;
    private String rewardType;


    public void update(Reward other){
        this.name = other.getName();
        this.rewardType = other.getRewardType();
    }
}
