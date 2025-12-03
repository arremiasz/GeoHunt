package com.geohunt.backend.database;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Shop {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id long id;
    private String name;
    private String description;
    private String image;

}
