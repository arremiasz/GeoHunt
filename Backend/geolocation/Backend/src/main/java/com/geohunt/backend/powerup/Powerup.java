package com.geohunt.backend.powerup;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Powerup {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id long id;

    private String name;
    private String affect;

    @Enumerated(EnumType.STRING)
    private PowerupEffects type;
}
