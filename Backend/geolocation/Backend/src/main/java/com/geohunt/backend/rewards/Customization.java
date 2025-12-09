package com.geohunt.backend.rewards;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue("Customization")
@NoArgsConstructor
public class Customization extends Reward {
}
