package com.geohunt.backend.powerup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.geohunt.backend.database.Account;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"accounts"})
public class Powerup {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id long id;

    private String name;
    private String affect;

    @Enumerated(EnumType.STRING)
    private PowerupEffects type;

    @ManyToMany(mappedBy = "powerups")
    @JsonIgnoreProperties("powerups")
    private Set<Account> accounts = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Powerup)) return false;
        Powerup p = (Powerup) o;
        return id == p.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
