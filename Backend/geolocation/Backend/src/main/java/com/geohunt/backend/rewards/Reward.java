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
public class Reward {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public @Id long id;
    public String name;
    public String rewardType;
    public double valueInPoints;

    @OneToOne
    public Image rewardImage;

    public void update(Reward other){
        this.name = other.getName();
        this.rewardType = other.getRewardType();
        this.valueInPoints = other.valueInPoints;
        this.rewardImage = other.rewardImage;
    }
}
